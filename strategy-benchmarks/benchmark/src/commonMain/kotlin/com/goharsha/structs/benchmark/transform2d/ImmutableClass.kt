package com.goharsha.structs.benchmark.transform2d

import com.goharsha.structs.model.Vec2

fun transformPoint2dImmutableClass(
    point: Vec2,
    translate: Vec2,
    scale: Vec2,
    rotationRadians: Float
): Vec2 = point
    .scale(scale)
    .rotate(rotationRadians) + translate