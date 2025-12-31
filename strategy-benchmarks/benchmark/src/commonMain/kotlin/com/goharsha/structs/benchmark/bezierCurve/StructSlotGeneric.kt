package com.goharsha.structs.benchmark.bezierCurve

import com.goharsha.structs.model.STRUCT_SIZE_VEC2
import com.goharsha.structs.model.STRUCT_VEC2_X_OFFSET
import com.goharsha.structs.model.STRUCT_VEC2_Y_OFFSET
import com.goharsha.structs.model.Vec2_plus
import com.goharsha.structs.model.Vec2_times
import com.goharsha.structs.readFloat

fun lerpStructSlotGeneric(
    ax: Float, ay: Float,
    bx: Float, by: Float,
    t: Float,
    out: ByteArray, offset: Int
) {
    val buffer = ByteArray(STRUCT_SIZE_VEC2)

    Vec2_times(ax, ay, 1f - t, buffer)
    Vec2_times(bx, by, t, out, offset)
    Vec2_plus(
        ax = readFloat(buffer, STRUCT_VEC2_X_OFFSET),
        ay = readFloat(buffer, STRUCT_VEC2_Y_OFFSET),
        bx = readFloat(out, offset + STRUCT_VEC2_X_OFFSET),
        by = readFloat(out, offset + STRUCT_VEC2_Y_OFFSET),
        out = out,
        offset = offset
    )
}

fun bezierQuadraticStructSlotGeneric(
    p0x: Float, p0y: Float,
    p1x: Float, p1y: Float,
    p2x: Float, p2y: Float,
    t: Float,
    out: ByteArray, offset: Int = 0
) {
    val buffer = ByteArray(STRUCT_SIZE_VEC2 * 2)

    lerpStructSlotGeneric(p0x, p0y, p1x, p1y, t, buffer, offset = 0)
    lerpStructSlotGeneric(p1x, p1y, p2x, p2y, t, buffer, offset = STRUCT_SIZE_VEC2)

    lerpStructSlotGeneric(
        ax = readFloat(buffer, STRUCT_VEC2_X_OFFSET),
        ay = readFloat(buffer, STRUCT_VEC2_Y_OFFSET),
        bx = readFloat(buffer, STRUCT_VEC2_X_OFFSET + STRUCT_SIZE_VEC2),
        by = readFloat(buffer, STRUCT_VEC2_Y_OFFSET + STRUCT_SIZE_VEC2),
        t = t,
        out = out,
        offset = offset
    )
}