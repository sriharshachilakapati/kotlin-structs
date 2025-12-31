# V1: Struct-like Immutable Types via Compiler Plugin

**Author(s):**  Sri Harsha Chilakapati
**Status:** Draft  
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

---

## 2. Goals

### 2.1 Primary Goals

- Enable high-performance immutable data types
- Avoid heap allocations in hot paths
- Work consistently across JVM, Native, and JS
- Preserve ABI stability for libraries
- Require no runtime dependency

### 2.2 Non-Goals (V1)

- Optimizing arrays or collections
- Supporting generics
- Full escape analysis
- Precise memory layout control
- Zero-allocation guarantees in all scenarios

---

## 3. User-Facing API

### 3.1 Declaring a Struct

```kotlin
@Struct
class Vec2(
    val x: Float,
    val y: Float
)
```

### 3.2 Restrictions

- No inheritance
- Final class
- All properties must be `val`
- No generics
- No identity guarantees
- Cannot be cast to `Any` or stored in arbitrary collections

---

## 4. Motivation via Benchmarks

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

## 5. Evaluated Lowering Strategies

The following strategies were evaluated:

1. Immutable data classes
2. Mutable classes
3. Packed return values (bit packing)
4. Slot-based return using arrays
5. Specialized slot-based returns

---

## 6. Benchmark Results

### 6.1 Kotlin/JS (Node.js)

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

### 6.2 Kotlin/Native (Linux x64)

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

### 6.3 JVM (HotSpot)

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

## 7. Key Conclusion

Across all platforms, mutable classes are the fastest and most reliable representation.

All other strategies introduce:

- additional indirections
- worse calling conventions
- runtime penalties (especially on JS)

---

## 8. Chosen Strategy for V1

V1 exclusively lowers structs into mutable carrier classes.

- No packing
- No array-based slots
- No bit-level encoding

This decision is entirely data-driven.

---

## 9. Lowered Representation

Given:

```kotlin
@Struct
class Vec2(val x: Float, val y: Float)

operator fun plus(a: Vec2, b: Vec2): Vec2
```

The compiler plugin generates:

```kotlin
internal class MutableVec2(
    var x: Float,
    var y: Float
)
```

And lowered functions such as:

```kotlin
fun Vec2_plus(
    ax: Float, ay: Float,
    bx: Float, by: Float,
    out: MutableVec2
)
```

---

## 10. Call-Site Rewriting

User code:

```kotlin
val c = a + b
```

Lowered IR (conceptually):

```kotlin
val tmp = MutableVec2(0f, 0f)
Vec2_plus(a.x, a.y, b.x, b.y, tmp)
```

Allocation responsibility is moved to the caller, enabling reuse and avoiding escapes.

---

## 11. Immutability & Safety

- Users never see mutable carriers
- Mutation is compiler-generated only
- Carriers never escape user-visible APIs
- Copies are enforced on field storage

---

## 12. Suspend Functions and Coroutines

- Mutable carriers are allocated per coroutine region
- No sharing across suspension points
- Lambdas and suspend lambdas are rewritten consistently
- Verified experimentally for correctness

---

## 13. Compiler Architecture

### 13.1 FIR Phase Responsibilities

- Detect `@Struct`
- Validate constraints
- Declare generated symbols
- Attach metadata
- Respect `@NoLowering`

### 13.2 IR Phase Responsibilities

- Rewrite function bodies
- Rewrite call sites
- Insert carrier allocations
- Remove immutable allocations

---

## 14. Testing Strategy

### 14.1 Compile-Time Tests

- FIR diagnostics
- IR symbol verification
- IR text dumps

### 14.2 Runtime Correctness Tests

```kotlin
@NoLowering
fun normal(): Float = ...

fun lowered(): Float = ...

@Test
fun sameResult() {
    assertEquals(normal(), lowered())
}
```

---

## 15. Future Work

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

## 16. Summary

This proposal introduces a practical, data-backed approach to struct-like performance in Kotlin today.

By embracing mutable lowering internally while preserving immutable APIs externally, it achieves:

- High performance
- Cross-platform consistency
- ABI stability
- Minimal complexity

The design deliberately starts conservative, establishing a solid foundation for future optimizations.

---

## 17. Rejected Alternatives

This section documents alternative designs that were explored during the research and benchmarking phase, along with concrete reasons for rejecting them in V1.

---

### 17.1 Slot-Based Return Using Primitive Arrays

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

### 17.2 Packed Return Values (Bit Packing)

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

### 17.3 ByteBuffer / TypedArray Struct Storage

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

### 17.4 Thread-Local Slot Pools

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

### 17.5 Escape-Analysis-Driven Scalarization in Plugin

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

### 17.6 Relying Solely on JVM Escape Analysis

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

### 17.7 Value Classes (Inline Classes)

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

### 17.8 Treating Structs as Arrays in User Code

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

## 18. Summary of Rejections

Across extensive benchmarking and experimentation:

- **Mutable carrier classes consistently outperform all alternatives**
- **They align best with JVM EA, Native scalarization, and JS object models**
- **They preserve debuggability and readability**
- **They scale naturally to suspend functions and lambdas**
- **They maintain ABI stability**

All other approaches introduce either correctness risk, performance regression, or excessive complexity.

For these reasons, **mutable-class lowering is the sole strategy adopted for V1**.
