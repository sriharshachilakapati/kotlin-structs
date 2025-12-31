package com.goharsha.structs.benchmark.transform2d

import com.goharsha.structs.model.STRUCT_VEC2_X_OFFSET
import com.goharsha.structs.model.STRUCT_VEC2_Y_OFFSET
import com.goharsha.structs.model.Vec2_plus
import com.goharsha.structs.model.Vec2_rotate
import com.goharsha.structs.model.Vec2_scale
import com.goharsha.structs.readFloat

fun transformPoint2dStructSlotGeneric(
    pointX: Float, pointY: Float,
    translateX: Float, translateY: Float,
    scaleX: Float, scaleY: Float,
    rotationRadians: Float,
    out: ByteArray
) {
    Vec2_scale(pointX, pointY, scaleX, scaleY, out)

    Vec2_rotate(
        ax = readFloat(out, STRUCT_VEC2_X_OFFSET),
        ay = readFloat(out, STRUCT_VEC2_Y_OFFSET),
        angleRadians = rotationRadians,
        out = out
    )

    Vec2_plus(
        ax = readFloat(out, STRUCT_VEC2_X_OFFSET),
        ay = readFloat(out, STRUCT_VEC2_Y_OFFSET),
        bx = translateX,
        by = translateY,
        out = out
    )
}