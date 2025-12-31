package com.goharsha.structs.benchmark.bezierCurve

import com.goharsha.structs.model.STRUCT_VEC2_FIELD_COUNT
import com.goharsha.structs.model.STRUCT_VEC2_X_INDEX
import com.goharsha.structs.model.STRUCT_VEC2_Y_INDEX
import com.goharsha.structs.model.Vec2_plus
import com.goharsha.structs.model.Vec2_times

fun lerpStructSlotSpecialized(
    ax: Float, ay: Float,
    bx: Float, by: Float,
    t: Float,
    out: FloatArray, offset: Int
) {
    val buffer = FloatArray(STRUCT_VEC2_FIELD_COUNT)

    Vec2_times(ax, ay, 1f - t, buffer)
    Vec2_times(bx, by, t, out, offset)

    Vec2_plus(
        ax = buffer[STRUCT_VEC2_X_INDEX],
        ay = buffer[STRUCT_VEC2_Y_INDEX],
        bx = out[offset + STRUCT_VEC2_X_INDEX],
        by = out[offset + STRUCT_VEC2_Y_INDEX],
        out = out,
        offset = offset
    )
}

fun bezierQuadraticStructSlotSpecialized(
    p0x: Float, p0y: Float,
    p1x: Float, p1y: Float,
    p2x: Float, p2y: Float,
    t: Float,
    out: FloatArray, offset: Int = 0
) {
    val buffer = FloatArray(STRUCT_VEC2_FIELD_COUNT * 2)

    lerpStructSlotSpecialized(p0x, p0y, p1x, p1y, t, buffer, offset = 0)
    lerpStructSlotSpecialized(p1x, p1y, p2x, p2y, t, buffer, offset = STRUCT_VEC2_FIELD_COUNT)

    lerpStructSlotSpecialized(
        ax = buffer[STRUCT_VEC2_X_INDEX],
        ay = buffer[STRUCT_VEC2_Y_INDEX],
        bx = buffer[STRUCT_VEC2_X_INDEX + STRUCT_VEC2_FIELD_COUNT],
        by = buffer[STRUCT_VEC2_Y_INDEX + STRUCT_VEC2_FIELD_COUNT],
        t = t,
        out = out,
        offset = offset
    )
}