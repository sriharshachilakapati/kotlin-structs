package com.goharsha.structs.benchmark.transform2d

import com.goharsha.structs.model.Vec2_plus
import com.goharsha.structs.model.Vec2_rotate
import com.goharsha.structs.model.Vec2_scale
import com.goharsha.structs.unpackFloatsA
import com.goharsha.structs.unpackFloatsB

fun transformPoint2dStructPacked(
    pointX: Float, pointY: Float,
    translateX: Float, translateY: Float,
    scaleX: Float, scaleY: Float,
    rotationRadians: Float
): Long {
    val scaled = Vec2_scale(pointX, pointY, scaleX, scaleY)
    val rotated = Vec2_rotate(
        ax = unpackFloatsA(scaled),
        ay = unpackFloatsB(scaled),
        angleRadians = rotationRadians
    )

    return Vec2_plus(
        ax = unpackFloatsA(rotated),
        ay = unpackFloatsB(rotated),
        bx = translateX,
        by = translateY
    )
}