package com.goharsha.structs.benchmark.transform2d

import com.goharsha.structs.model.STRUCT_VEC2_X_INDEX
import com.goharsha.structs.model.STRUCT_VEC2_Y_INDEX
import com.goharsha.structs.model.Vec2_plus
import com.goharsha.structs.model.Vec2_rotate
import com.goharsha.structs.model.Vec2_scale

fun transformPoint2dStructSlotSpecialized(
    pointX: Float, pointY: Float,
    translateX: Float, translateY: Float,
    scaleX: Float, scaleY: Float,
    rotationRadians: Float,
    out: FloatArray
) {
    Vec2_scale(pointX, pointY, scaleX, scaleY, out)

    Vec2_rotate(
        ax = out[STRUCT_VEC2_X_INDEX],
        ay = out[STRUCT_VEC2_Y_INDEX],
        angleRadians = rotationRadians,
        out = out
    )

    Vec2_plus(
        ax = out[STRUCT_VEC2_X_INDEX],
        ay = out[STRUCT_VEC2_Y_INDEX],
        bx = translateX,
        by = translateY,
        out = out
    )
}