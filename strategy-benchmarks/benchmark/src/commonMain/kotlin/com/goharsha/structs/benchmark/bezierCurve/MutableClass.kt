package com.goharsha.structs.benchmark.bezierCurve

import com.goharsha.structs.model.MutableVec2

fun lerpMutableClass(a: MutableVec2, b: MutableVec2, t: Float, out: MutableVec2) {
    out.x = a.x * (1f - t) + b.x * t
    out.y = a.y * (1f - t) + b.y * t
}

fun bezierQuadraticMutableClass(
    p0: MutableVec2,
    p1: MutableVec2,
    p2: MutableVec2,
    t: Float,
    out: MutableVec2
) {
    val a = MutableVec2(0f, 0f)
    val b = MutableVec2(0f, 0f)
    lerpMutableClass(p0, p1, t, a)
    lerpMutableClass(p1, p2, t, b)
    lerpMutableClass(a, b, t, out)
}