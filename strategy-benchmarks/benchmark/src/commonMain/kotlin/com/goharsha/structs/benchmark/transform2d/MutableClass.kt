package com.goharsha.structs.benchmark.transform2d

import com.goharsha.structs.model.MutableVec2

fun transformPoint2dMutableClass(
    point: MutableVec2,
    translate: MutableVec2,
    scale: MutableVec2,
    rotationRadians: Float,
    out: MutableVec2
) {
    out.x = point.x
    out.y = point.y

    out.scale(scale)
    out.rotate(rotationRadians)
    out.plus(translate)
}