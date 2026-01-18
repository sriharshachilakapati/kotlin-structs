# V1: Struct-like Immutable Types via Compiler Plugin

**Author(s):**  Sri Harsha Chilakapati  
**Status:** Design-Frozen  
**Target Kotlin version:** 2.3+ (K2 only)  
**Related:** Value Classes, Project Valhalla  
**Implementation:** Compiler plugin (FIR + IR)  

---

## 1. Introduction

Kotlin currently lacks a mechanism to express immutable, multi-field value aggregates with predictable, allocation-free performance comparable to:

- C / C++ `struct`
- Rust `struct`
- Swift `struct`
- JVM Project Valhalla primitive classes (future)

Existing language features fall short:

- Data classes are immutable but heap-allocated.
- Mutable classes are performant but expose mutability.
- Value classes are restricted to a single field.

This proposal introduces a compiler plugin–based solution that provides struct-level performance while preserving a fully immutable user-facing API, applicable across JVM, Kotlin/Native, and Kotlin/JS.

## 2. Design Goals

The goal of this plugin is to enable struct-like performance using immutable Kotlin classes. The following are the characteristics of this proposal.

- Immutable by default
- Allocation-free for most operations
- Explicit boxing only at well-defined boundaries
- Prefictable and portable performance across platforms

### 2.1. Primary Goals

#### 2.1.1. Immutable Semantics

- Structs behave like pure value aggregates
- No observable identity
- No mutation through user APIs
- Behaviour independent of optimization strategy

#### 2.1.2. Allocation Elimination

- No heap allocation for:
    - Intermediate expressions
    - Temporary values
    - Function return values
- Allocation only occurs when:
    - Crossing Generic / Any boundaries
    - When used in Arrays
    - Explicitly exposing ABI

#### 2.1.3. Predictable Performance

- Performance does **not** depend on runtime escape analysis
- Same semantics across JVM / Native / JS
- No reliance on backend-specific optimizations

#### 2.1.4. Source Compatibility

- Users write idiomatic Kotlin
- No manual memory management
- No unsafe APIs
- No explicit carriers in user code

#### 2.1.5. Explicit ABI Control

- Java / reflection interop must be **explicit**
- No silent ABI changes
- Clear user opt-in for boxed exposure

### 2.2 Non-Goals

- Optimizing arrays or collections
- Supporting generics
- Full escape analysis
- Precise memory layout control
- Zero-allocation guarantees in all scenarios
- No mutable fields
- No reflection on lowered representations
- No inheritance between structs

---

## 3. User-Facing API

### 3.1. The @Struct Annotation

#### 3.1.1. Basic Usage

```kotlin
@Struct
class Vec2(
    val x: Float,
    val y: Float
)
```

A `@Struct` class represents an **immutable value aggretate** that the compiler is allowed to lower aggressively.

#### 3.1.2. Structural Constraints

A struct class must satisfy the following constraints:

- Must be **final**. No inheritance allowed.
- All primary constructor parameters must be `val` properties.
- Properties must be of supported types:
    - Primitive types (`Int`, `Float`, etc.)
    - Enums
    - Other `@Struct` types
- No observable identity.
- Deterministic initialization.

**Allowed:**

```kotlin
@Struct
class Rectangle(
    val position: Vec2,
    val size: Vec2
)
```

**Disallowed:**

- Any field which is declared as a `var`.
- Any field of unsupported type (e.g., `String`, `List`, user-defined classes).
- Inheritance from another class or interface.
- Custom `equals`, `hashCode`, or `toString` implementations.
- Identity checks (`===`, `!==`).
- Impure initialization

**Example (invalid):**

```kotlin
@Struct
class InvalidStruct(val x: Float) {
    init {
        println("Impure init") // Not allowed
    }
}
```

**Reasoning:**

Struct lowering removes instance creation; side effects tied to construction would silently disapper if the struct is optimized away. Hence we prevent such cases at compile time using FIR diagnostics.

#### 3.1.3. Secondary Constructors

Secondary constructors are **allowed** if they are pure and delegate to the primary constructor.

**Example (valid):**

```kotlin
@Struct
class Rectangle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    constructor(point: Vec2, size: Vec2)
        : this(point.x, point.y, size.x, size.y)
}
```

**Reasoning:**

- Delegation is purely structural
- No observable side effects
- Can be safely inlined during lowering

### 3.2. The @ExposeAbi Annotation

The `@ExposeAbi` annotation allows users to explicitly mark functions or properties that should expose boxed struct instances in their ABI.

#### 3.2.1. Basic Usage

```kotlin
@ExposeAbi
fun getOrigin(): Vec2 {
    return Vec2(0f, 0f)
}
```

By default, functions returning structs are lowered to avoid allocations. Marking a function with `@ExposeAbi` instructs the compiler to return a boxed instance of the struct instead.

#### 3.2.2. Use Case

The main use case for `@ExposeAbi` is interoperability with Java, where the caller expects a concrete object instance. The classes annotated with `@Struct` will still exist as normal classes at Runtime, and when `@ExposeAbi` is used, the compiler will generate code to create and return an instance of the struct class.

#### 3.2.3. Internal Functioning

Normally, every function which takes in a struct or returns a struct is rewritten to use lowered representations (e.g., mutable carriers). When `@ExposeAbi` is applied, the compiler generates additional code to box/unbox the struct at the function boundary.

Take the following example of a function:

```kotlin
fun add(a: Vec2, b: Vec2): Vec2 {
    return Vec2(a.x + b.x, a.y + b.y)
}
```

Without the `@ExposeAbi` annotation:

```kotlin
fun __structFn$add(
    ax: Float, ay: Float,
    bx: Float, by: Float,
    out: StructCarrier$Vec2
) {
    out.x = ax + bx
    out.y = ay + by
}
```

When the annotation is not applied, the original function is rewritten in IR. This is also done to reduce the synthetic symbol explosion. But when `@ExposeAbi` is applied, we would generate:

```kotlin
@ExposeAbi
fun add(a: Vec2, b: Vec2): Vec2 {
    val out = StructCarrier$Vec2(0f, 0f)
    __structFn$add(a.x, a.y, b.x, b.y, out)
    return Vec2(out.x, out.y) // Boxing step
}
```

in addition to the lowered function. This way, the user-visible API remains unchanged, and the boxing is explicit and controlled.

---

## 4. Conceptual Lowering Model

### 4.1. User View

User code is written as normal immutable classes:

```kotlin
val a = Vec2(1f, 2f)
val b = Vec2(3f, 4f)
val c = a + b         // Returns a new Vec2
```

From the user's perspective:

- `Vec2` is an immutable class.
- Operations like `plus` return new instances.
- No mutable state is observable.

### 4.2. Lowered View

Internally, the compiler plugin rewrites the above code to use mutable carriers and primitive parameters, achieving allocation-free performance:

```kotlin
val c = StructCarrier$Vec2(0f, 0f)
__structFn$Vec2$plus(
    1f, 2f,            // a.x, a.y
    3f, 4f,            // b.x, b.y
    c                  // out carrier
)
```

- No `Vec2` instances are created at runtime.
- Values are passed as primitives.
- Result is written into a temporary slot.

This preserves:

- Immutable semantics
- Referential transparency
- Predictable performance

### 4.3. Mutable Carrier Generation

For every `@Struct` class, the compiler plugin generates an internal mutable carrier class used for lowering.

**Example:**

Given:

```kotlin
@Struct
class Vec2(val x: Float, val y: Float)
```

The plugin generates:

```kotlin
class StructCarrier$Vec2(
    var x: Float,
    var y: Float
)
```

However, this carrier class is **not** visible to users and is only used internally by the compiler during lowering for return values and temporary storage.

- These exist **only in IR**
- Never appear in metadata or IDE
- Never escape into user visible APIs
- Mutation is only performed by compiler-generated code

While this does not guarantee zero allocations, it allows the compiler to optimize away most allocations in practice.

### 4.4. Function Signature Lowering

All functions that take or return structs are rewritten to use primitive parameters whenever possible and mutable carriers for return values.

**Source:**

```kotlin
fun add(a: Vec2, b: Vec2): Vec2 {
    return Vec2(a.x + b.x, a.y + b.y)
}
```

**Conceptual Lowered Form:**

```kotlin
fun __structFn$add(
    ax: Float, ay: Float,
    bx: Float, by: Float,
    out: StructCarrier$Vec2
) {
    out.x = ax + bx
    out.y = ay + by
}
```

- Parameters are expanded into primitives.
- Return value is written into a caller-provided mutable carrier.
- No allocations occur within the function body.

However we will only flatten the parameters when possible. If the struct is passed around as a whole (e.g., stored in a collection), we will pass the carrier object instead.

#### 4.4.1. Parameter Flattening

##### 4.4.1.1. Why Flatten Parameters?

Flattening parameters into primitives allows:

- Avoiding allocations for struct parameters
- Enabling backend optimizations (e.g., scalarization)
- Improves inlining
- Reduces GC pressure

But it increases the number of parameters, which can have trade-offs. If we flatten too aggressively:

- JVM may hit parameter limits
- Bloats signatures
- Inreases stack pressure post optimizations

##### 4.4.1.2. JVM Descriptor Units

In the Java Language Specification, there is mentioning of limits on the number of parameters a method can have, measured in "descriptor units". The rules are as follows:

- One unit is consumed by function receiver (`this`)
- Long and Double types consume two units
- All other types consume one unit

The JVM specification mandates a maximum of 255 descriptor units for method parameters. Exceeding this limit results in a `java.lang.VerifyError` at runtime.

##### 4.4.1.3. Flattening Limit Policy

To balance the benefits of parameter flattening with the risks of exceeding JVM limits, the compiler plugin employs the following policy:

- **Limit: 128 descriptor units**
- Limit includes:
    - Flattened struct fields
    - Non-struct parameters
    - Implicit return carriers
    - Function receiver (`this`, if applicable)

This conservative limit ensures that even after optimizations, the method signatures remain within safe bounds across all target platforms.

##### 4.4.1.4. Partial Flattening

If flattening all struct parameters would exceed the 128-unit limit, the plugin falls back to passing some structs as whole carrier objects instead of flattening them.

**Example:**

```kotlin
fun f(a: Vec2, b: BigStruct, c: Vec2)
```

Might be lowered to:

```kotlin
fun __structFn$f(
    ax: Float, ay: Float,          // Flattened Vec2 'a'
    b: BigStruct,                  // Passed as carrier
    cx: Float, cy: Float           // Flattened Vec2 'c'
)
```

This ensures that the total descriptor units remain within the defined limit while still benefiting from flattening where possible.

**We employ greedy flattening**: we flatten as many struct parameters as possible without exceeding the limit, prioritizing smaller structs first.

### 4.5. Suspension Semantics

Suspend functions and lambdas are rewritten similarly, with mutable carriers allocated per coroutine region.

#### 4.5.1. Definitions

To reason about correctness, reuse, and safety of mutable struct carriers, the following terms are used throughout this specification.

##### 4.5.1.1. Suspension Point

A **suspension point** is a program location where execution of the current coroutine **may suspend** and later **resume**.

Formally, a suspension point is any call expression that:

- Is marked **suspend** and
- Is not known to complete synchronously

Examples include:

- `delay(...)`
- `yield()`
- `await(...)`
- Any user-defined suspend function call

##### 4.5.1.2. Suspension Region

A **suspension region** is a contigous region of execution within a coroutine **between two suspension points**, or between:

- Function entry and the first suspension point
- A suspension point and the next suspension point
- The last suspension point and function exit

Within a single suspension region:

- Execution is guaranteed to be sequential and uninterrupted
- No suspension occurs
- Mutable carriers can be safely reused
- No concurrent access is possible

Suspension regions are **conceptual**, not syntactic, and are defined by control flow.

##### 4.5.1.3. Context Switch

A **context switch** occurs when a suspended coroutine resumes execution:

- On a different thread
- On a different worker (Kotin/Native)
- On a different event loop tick (Kotlin/JS)

Context switches are **implicit** and **unobservable** to user code, except through ordering effects.

#### 4.5.2. Core Rule

Mutable carriers are allowed to be reused across suspension regions as long as **they do not escape into different execution context**.

A mutable carrier is considered to **escape into a different context** if **any** of the following are true:

1. It is **captured** by a lambda passed to a suspend function
2. It is **stored** in a data structure that outlives the suspension region
3. It is **shared** across concurrently executing coroutine paths

#### 4.5.3. Context Switch Safety

A carrier **does not escape** merely because:

- Execution suspends
- The coroutine resumes on a different thread or worker
- The function contains `delay`, `await`, or similar calls

Only context switches require new carrier allocation. A new mutable carrier **must be allocated** at:

- Suspend lambdas passed to:
    - `launch { ... }`
    - `async { ... }`
    - `withContext { ... }`
    - `coroutineScope { ... }`
- Any user-defined suspend function parameter of function type

These create new coroutine contexts.

#### 4.5.4. Examples

##### 4.5.4.1. Reuse across Suspension Regions

**Source:**

```kotlin
suspend fun moveTwice(p: Vec2, delta: Vec2): Vec2 {
    var r = p + delta
    delay(100)
    r = r + delta
    return r
}
```

**Lowered Conceptual Form:**

```kotlin
suspend fun __structFn$moveTwice(
    px: Float, py: Float,
    dx: Float, dy: Float,
    out: StructCarrier$Vec2
) {
    val r = StructCarrier$Vec2(0f, 0f)
    __structFn$Vec2$plus(px, py, dx, dy, r)   // r = p + delta
    delay(100)                                // Suspension point
    __structFn$Vec2$plus(r.x, r.y, dx, dy, r) // r = r + delta
    out.x = r.x
    out.y = r.y
}
```

**Why this is Safe:**

- No new coroutine context is entered
- `out` is not captured
- Resumption thread does not matter

##### 4.5.4.2. New Lambda Context

**Source:**

```kotlin
suspend fun f(pos: Vec2, delta: Vec2) {
    val r = pos + delta
    withContext(Dispatchers.Default) {
        println(r)
    }
}
```

**Lowered Conceptual Form:**

```kotlin
suspend fun __structFn$f(
    posx: Float, posy: Float,
    deltax: Float, deltay: Float
) {
    val r = StructCarrier$Vec2(0f, 0f)
    __structFn$Vec2$plus(posx, posy, deltax, deltay, r) // r = pos + delta

    withContext(Dispatchers.Default) {
        val r2 = StructCarrier$Vec2(0f, 0f) // New carrier for lambda
        r2.x = r.x
        r2.y = r.y
        println(Vec2(r2.x, r2.y)) // Boxing step for 'Any'
    }
}
```

**Why reallocation is required:**

- `withContext { ... }` creates a new coroutine context
- `r` is captured by the lambda
- Mutating `r` in new context would be unsafe

### 4.6. Lambdas and Function Types

Function types involving structs are rewritten similarly to normal functions.

**Source:**

```kotlin
suspend fun withCallback(
    pos: Vec2,
    delta: Vec2,
    block: suspend (Vec2) -> Unit
) {
    val newPos = pos + delta
    block(newPos)
}
```

**Lowered Conceptual Form:**

```kotlin
suspend fun __structFn$withCallback(
    posx: Float, posy: Float,
    deltax: Float, deltay: Float,
    block: suspend (Float, Float) -> Unit
) {
    val newPos = StructCarrier$Vec2(0f, 0f)
    __structFn$Vec2$plus(posx, posy, deltax, deltay, newPos) // newPos = pos + delta

    // Call the suspend lambda with flattened parameters
    block(newPos.x, newPos.y)
}
```

**Reasoning:**

- Function types are rewritten to accept flattened parameters.
- Prevents implicit boxing of structs.
- Lambda signatures are part of IR rewriting.

### 4.7. Boxing Boundaries

Structs are boxed **only** when crossing explicit ABI boundaries, such as:

- Functions marked with `@ExposeAbi`
- Functions that take `Any` parameters
- Functions that return `Any`
- Passing structs to parameters of Generic Types

### 4.8. Diagnostics & Safety Checks

The compiler plugin emits FIR diagnostics for:

- Identity checks (`===`, `!==`)
- Mutable fields
- Unsupported field types
- Impure initialization
- Struct inheritance

The goal is to prevent silent semantic changes and guide users toward safe patterns.

---

## 5. Motivation via Benchmarks

Extensive benchmarks were conducted using real computational kernels:

- Blinn–Phong lighting
- Quadratic Bezier curve
- 2D point transformations

Benchmarks were run on:

- Linux x64
- JVM (HotSpot)
- Kotlin/Native
- Kotlin/JS (Node.js)

Each strategy was tested under identical workloads.

---

## 6. Evaluated Lowering Strategies

The following strategies were evaluated:

1. Immutable data classes
2. Mutable classes
3. Packed return values (bit packing)
4. Slot-based return using arrays
5. Specialized slot-based returns

---

## 7. Benchmark Results

### 7.1 Kotlin/JS (Node.js)

```
js summary:
Benchmark                                                    Mode Cnt Score      Error  Units
Vec2Benchmark.benchmarkBezierCurveImmutableClass             avgt 10    43.654 ±  0.690 ns/op
Vec2Benchmark.benchmarkBezierCurveMutableClass               avgt 10     7.419 ±  1.230 ns/op
Vec2Benchmark.benchmarkBezierCurveStructPacked               avgt 10  1060.074 ±  7.524 ns/op
Vec2Benchmark.benchmarkBezierCurveStructSlotGeneric          avgt 10   590.137 ±  1.263 ns/op
Vec2Benchmark.benchmarkBezierCurveStructSlotSpecialized      avgt 10   233.072 ±  2.162 ns/op
Vec2Benchmark.benchmarkBlinnPhongImmutableClass              avgt 10     7.894 ±  0.755 ns/op
Vec2Benchmark.benchmarkBlinnPhongMutableClass                avgt 10     9.134 ±  0.705 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructPacked                avgt 10   311.036 ±  3.916 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructSlotGeneric           avgt 10   192.743 ±  2.962 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructSlotSpecialized       avgt 10    94.854 ±  0.352 ns/op
Vec2Benchmark.benchmarkTransformPoint2dImmutableClass        avgt 10     7.100 ±  1.205 ns/op
Vec2Benchmark.benchmarkTransformPoint2dMutableClass          avgt 10     6.602 ±  0.993 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructPacked          avgt 10   378.710 ± 11.921 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructSlotGeneric     avgt 10   242.293 ±  2.439 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructSlotSpecialized avgt 10    91.406 ±  1.213 ns/op
```

Observation:

Manual struct emulation is catastrophically slow on JS. Mutable object reuse is the only viable strategy. We can also see that packed structs perform the worst in JS. This is because we are choosing to pack into a `long` field, and JS only has 53-bit numbers, and hence Kotlin emulates Long using two 32-bit fields.

---

### 7.2 Kotlin/Native (Linux x64)

```
linuxX64 summary:
Benchmark                                                    Mode Cnt Score    Error Units
Vec2Benchmark.benchmarkBezierCurveImmutableClass             avgt 10  82.823 ± 0.283 ns/op
Vec2Benchmark.benchmarkBezierCurveMutableClass               avgt 10  18.724 ± 0.226 ns/op
Vec2Benchmark.benchmarkBezierCurveStructPacked               avgt 10  24.418 ± 0.120 ns/op
Vec2Benchmark.benchmarkBezierCurveStructSlotGeneric          avgt 10  70.987 ± 0.209 ns/op
Vec2Benchmark.benchmarkBezierCurveStructSlotSpecialized      avgt 10  46.670 ± 0.464 ns/op
Vec2Benchmark.benchmarkBlinnPhongImmutableClass              avgt 10  33.438 ± 0.145 ns/op
Vec2Benchmark.benchmarkBlinnPhongMutableClass                avgt 10  20.041 ± 0.277 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructPacked                avgt 10  26.048 ± 0.195 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructSlotGeneric           avgt 10  44.349 ± 0.332 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructSlotSpecialized       avgt 10  30.927 ± 0.410 ns/op
Vec2Benchmark.benchmarkTransformPoint2dImmutableClass        avgt 10  39.344 ± 0.407 ns/op
Vec2Benchmark.benchmarkTransformPoint2dMutableClass          avgt 10  24.574 ± 0.275 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructPacked          avgt 10  32.956 ± 0.087 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructSlotGeneric     avgt 10  42.458 ± 0.422 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructSlotSpecialized avgt 10  36.150 ± 0.426 ns/op
```

Observation:

Native benefits from scalarization, but mutable classes remain consistently fastest.

---

### 7.3 JVM (HotSpot)

```
jvm summary:
Benchmark                                                    Mode Cnt Score    Error Units
Vec2Benchmark.benchmarkBezierCurveImmutableClass             avgt 10   4.395 ± 0.093 ns/op
Vec2Benchmark.benchmarkBezierCurveMutableClass               avgt 10   3.882 ± 0.063 ns/op
Vec2Benchmark.benchmarkBezierCurveStructPacked               avgt 10  12.202 ± 0.025 ns/op
Vec2Benchmark.benchmarkBezierCurveStructSlotGeneric          avgt 10  22.336 ± 0.388 ns/op
Vec2Benchmark.benchmarkBezierCurveStructSlotSpecialized      avgt 10   8.706 ± 0.281 ns/op
Vec2Benchmark.benchmarkBlinnPhongImmutableClass              avgt 10   5.031 ± 0.026 ns/op
Vec2Benchmark.benchmarkBlinnPhongMutableClass                avgt 10   5.136 ± 0.022 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructPacked                avgt 10  11.269 ± 0.026 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructSlotGeneric           avgt 10  13.502 ± 0.040 ns/op
Vec2Benchmark.benchmarkBlinnPhongStructSlotSpecialized       avgt 10   9.241 ± 0.031 ns/op
Vec2Benchmark.benchmarkTransformPoint2dImmutableClass        avgt 10  23.569 ± 0.453 ns/op
Vec2Benchmark.benchmarkTransformPoint2dMutableClass          avgt 10  23.389 ± 0.723 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructPacked          avgt 10  31.408 ± 0.305 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructSlotGeneric     avgt 10  35.291 ± 0.521 ns/op
Vec2Benchmark.benchmarkTransformPoint2dStructSlotSpecialized avgt 10  26.376 ± 0.709 ns/op
```

Observation:

JVM escape analysis already optimizes mutable carriers extremely well. Manual struct lowering is slower than trusting the JIT.

---

## 8. Key Conclusion

Across all platforms, mutable classes are the fastest and most reliable representation.

All other strategies introduce:

- additional indirections
- worse calling conventions
- runtime penalties (especially on JS)

---

## 9. Compiler Architecture

### 9.1 FIR Phase Responsibilities

- Detect `@Struct`
- Validate constraints
- Declare generated symbols
- Attach metadata
- Respect `@ExposeAbi`

### 9.2 IR Phase Responsibilities

- Rewrite function bodies
- Rewrite call sites
- Insert carrier allocations
- Remove immutable allocations

---

## 10. Testing Strategy

### 10.1 Compile-Time Tests

- FIR diagnostics
- IR symbol verification
- IR text dumps

### 10.2 Runtime Correctness Tests

TODO: Expand

---

## 11. Future Work

### V2

- Array lowering
- Loop unrolling
- Scalar Replacement of Aggregates (SROA)

### V3

- Collections
- Layout annotations
- Serialization adapters
- JNI / Cinterop glue generation

---

## 12. Summary

This proposal introduces a practical, data-backed approach to struct-like performance in Kotlin today.

By embracing mutable lowering internally while preserving immutable APIs externally, it achieves:

- High performance
- Cross-platform consistency
- ABI stability
- Minimal complexity

The design deliberately starts conservative, establishing a solid foundation for future optimizations.

---

## 13. Rejected Alternatives

This section documents alternative designs that were explored during the research and benchmarking phase, along with concrete reasons for rejecting them in V1.

---

### 13.1 Slot-Based Return Using Primitive Arrays

**Description**

Functions return structs by writing into a caller-provided primitive array (`FloatArray`, `IntArray`) or a `ByteArray` for heterogeneous fields.

**Example**

```kotlin
fun add(
    ax: Float, ay: Float,
    bx: Float, by: Float,
    out: FloatArray
)
```

**Reasons for Rejection**

- **Kotlin/JS catastrophic performance**  
  Benchmarks showed 10×–100× slowdowns compared to mutable classes.
- **Poor JIT behavior on JVM**  
  Extra bounds checks and indirections prevent scalarization.
- **Native regression**  
  Slower than mutable carriers despite reduced allocation.
- **ABI pollution**  
  Function signatures become platform- and strategy-specific.
- **Debuggability issues**  
  Debuggers expose arrays instead of logical values.

**Status**

Rejected for V1. May be reconsidered for niche cases in V2.

---

### 13.2 Packed Return Values (Bit Packing)

**Description**

Small structs (e.g., two `Float`s or `Int`s) are packed into a single `Long` or `Double`.

**Example**

```kotlin
fun addPacked(ax: Float, ay: Float, bx: Float, by: Float): Long
```

**Reasons for Rejection**

- **Extremely slow on JS**
- **Additional bit manipulation overhead**
- **Loss of debuggability**
- **Hard to generalize beyond 2 fields**
- **Precision risks for floating-point packing**
- **Complicates suspend functions and lambdas**

**Benchmark Outcome**

Consistently slower than mutable class lowering on all platforms.

**Status**

Rejected.

---

### 13.3 ByteBuffer / TypedArray Struct Storage

**Description**

Structs stored in flat buffers (similar to LWJGL `MemoryStack` or C structs).

**Reasons for Rejection**

- **Manual memory management complexity**
- **Requires explicit allocation APIs**
- **Unnatural Kotlin usage**
- **JS TypedArray overhead**
- **Unsafe without strict lifetime tracking**
- **Hard to compose with coroutines**

**Status**

Out of scope for this plugin. Better suited for explicit low-level libraries.

---

### 13.4 Thread-Local Slot Pools

**Description**

Reuse struct slots via `ThreadLocal` object pools.

**Reasons for Rejection**

- **Unavailable on Kotlin/JS**
- **Incorrect under coroutines**
- **Cross-thread resumption breaks safety**
- **Extremely hard to reason about correctness**

**Status**

Rejected outright.

---

### 13.5 Escape-Analysis-Driven Scalarization in Plugin

**Description**

Implement full escape analysis in the compiler plugin to avoid allocations.

**Reasons for Rejection**

- **Requires whole-program analysis**
- **Not compatible with incremental compilation**
- **Hard to make ABI-stable**
- **Duplicates VM/compiler responsibilities**
- **High implementation complexity**

**Status**

Deferred to potential future research (V3+).

---

### 13.6 Relying Solely on JVM Escape Analysis

**Description**

Trust JVM HotSpot to optimize immutable classes automatically.

**Reasons for Rejection**

- **Does not apply to JS or Native**
- **Unpredictable across JVM versions**
- **No guarantees**
- **Fails in library boundaries**

**Status**

Insufficient for KMP goals.

---

### 13.7 Value Classes (Inline Classes)

**Description**

Use value classes as struct replacements.

**Reasons for Rejection**

- **Single-field limitation**
- **Boxing in generics**
- **No control over layout**
- **Cannot express Vec2 / Mat4 / Quaternion**
- **Different semantic intent**

**Status**

Not applicable.

---

### 13.8 Treating Structs as Arrays in User Code

**Description**

Lower `Collection<Struct>` to `Collection<Any>` or primitive arrays.

**Reasons for Rejection**

- **Destroys type safety**
- **Boxing still occurs**
- **Unreadable lowered code**
- **Poor debugging experience**
- **User-visible semantic changes**

**Status**

Rejected.

---

## 14. Summary of Rejections

Across extensive benchmarking and experimentation:

- **Mutable carrier classes consistently outperform all alternatives**
- **They align best with JVM EA, Native scalarization, and JS object models**
- **They preserve debuggability and readability**
- **They scale naturally to suspend functions and lambdas**
- **They maintain ABI stability**

All other approaches introduce either correctness risk, performance regression, or excessive complexity.

For these reasons, **mutable-class lowering is the sole strategy adopted for V1**.
