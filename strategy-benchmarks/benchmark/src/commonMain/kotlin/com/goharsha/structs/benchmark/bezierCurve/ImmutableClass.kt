package com.goharsha.structs.benchmark.bezierCurve

import com.goharsha.structs.model.Vec2

fun lerpImmutableClass(a: Vec2, b: Vec2, t: Float): Vec2 =
    a * (1f - t) + b * t

fun bezierQuadraticImmutableClass(
    p0: Vec2,
    p1: Vec2,
    p2: Vec2,
    t: Float
): Vec2 {
    val a = lerpImmutableClass(p0, p1, t)
    val b = lerpImmutableClass(p1, p2, t)
    return lerpImmutableClass(a, b, t)
}