# kotlin-structs

A Kotlin compiler plugin that introduces **struct-like performance** for immutable Kotlin classes across **JVM, Kotlin/Native, and Kotlin/JS**, without changing the Kotlin language or relying on platform-specific intrinsics.

This project focuses on **predictable performance**, **cross-platform correctness**, and **debuggability**, inspired by value types in C/C++, Project Valhalla, and real-world benchmarking.

---

## Motivation

Kotlin lacks a way to express **immutable value objects** that are also **consistently fast** across platforms.

Today:

- Immutable `data class` objects allocate
- Escape analysis is unpredictable
- Performance differs wildly between JVM, JS, and Native
- Existing solutions (value classes) are limited to single fields

This project explores a **compiler-plugin-based solution** that:

- Preserves idiomatic Kotlin syntax
- Produces code equivalent to hand-written, high-performance mutable math code
- Works uniformly in Kotlin Multiplatform (KMP)

---

## What This Project Is

- A **Kotlin compiler plugin (K2-only)**
- A **struct lowering framework**, not a runtime library
- A **research-driven implementation**, guided by extensive benchmarks
- A **pragmatic approach** that prioritizes real performance over theoretical purity

---

## What This Project Is Not

- Not a language proposal (yet)
- Not a replacement for value classes
- Not a memory-management framework
- Not a JNI / Panama / FFI solution
- Not trying to outsmart the JVM or JS engines

---

## High-Level Idea

Users write normal immutable Kotlin classes:

```
@Struct
class Vec2(val x: Float, val y: Float) {
    operator fun plus(other: Vec2) =
        Vec2(x + other.x, y + other.y)
}
```

The compiler plugin rewrites this into **mutable-carrier-based code** internally:

- No allocations on hot paths
- No struct objects at runtime
- Predictable performance across platforms
- Debuggers still show logical values

---

## Project Structure

```
.
├── kotlin-structs/
│   ├── struct-api/        # Public annotations (e.g., @Struct, @ExposeAbi)
│   ├── struct-compiler/   # K2 compiler plugin (FIR + IR)
│   └── gradle-plugin/     # Gradle plugin to apply the compiler plugin
│
├── strategy-benchmarks/   # KMP benchmarks used for evaluation
├── docs/                  # Design documents, proposals, KEEP-style specs
└── README.md
```

---

## Benchmarks

Benchmarks include real-world math kernels:

- Blinn–Phong lighting
- Quadratic Bezier curves
- 2D transforms
- Chained vector operations

Results show:

- Mutable-class lowering is consistently fastest
- Slot-based and packed strategies are dramatically slower on JS
- Native performance improves significantly but still benefits from mutability
- JVM already performs well, but lowering reduces unpredictability

Detailed benchmark results are documented in `docs/`.

---

## Current Status

- Specification complete (V1)
- Benchmarks validated across platforms
- Plugin implementation starting next

---

## Roadmap

- **V1**: Mutable carrier lowering (current focus)
- **V2**: Array optimizations (non-escaping arrays, unrolling, flattening)
- **V3**: Collection support, adapters, interop helpers
- **Future**:
  - Serialization adapters
  - C / JNI / CInterop glue generation
  - Layout annotations

---

## Inspiration & Related Work

- Project Valhalla (JVM)
- LWJGL struct patterns
- C / C++ value types
- Compose compiler plugin architecture
- Kotlin FIR + IR pipelines

---

## Disclaimer

This is an experimental compiler plugin.

APIs, behavior, and implementation details may change.

The project prioritizes **correctness and performance clarity** over backward compatibility during early development.

---

## License

MIT License, see [LICENSE](LICENSE) for details.

---

## Contributing

Discussion and feedback welcome.

Implementation contributions will be accepted once the core plugin architecture stabilizes.
