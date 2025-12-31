package com.goharsha.structs.benchmark.bezierCurve

import com.goharsha.structs.model.Vec2_plus
import com.goharsha.structs.model.Vec2_times
import com.goharsha.structs.unpackFloatsA
import com.goharsha.structs.unpackFloatsB

fun lerpStructPacked(
    ax: Float, ay: Float,
    bx: Float, by: Float,
    t: Float
): Long {
    val lhs = Vec2_times(ax, ay, 1f - t)
    val rhs = Vec2_times(bx, by, t)

    return Vec2_plus(
        ax = unpackFloatsA(lhs),
        ay = unpackFloatsB(lhs),
        bx = unpackFloatsA(rhs),
        by = unpackFloatsB(rhs)
    )
}

fun bezierQuadraticStructPacked(
    p0x: Float, p0y: Float,
    p1x: Float, p1y: Float,
    p2x: Float, p2y: Float,
    t: Float
): Long {
    val a = lerpStructPacked(p0x, p0y, p1x, p1y, t)
    val b = lerpStructPacked(p1x, p1y, p2x, p2y, t)

    return lerpStructPacked(
        ax = unpackFloatsA(a),
        ay = unpackFloatsB(a),
        bx = unpackFloatsA(b),
        by = unpackFloatsB(b),
        t = t
    )
}