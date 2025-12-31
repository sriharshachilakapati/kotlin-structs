package com.goharsha.structs.benchmark.blinnPhong

import com.goharsha.structs.model.Vec2
import kotlin.math.max

fun blinnPhongKernelImmutableClass(
    normal: Vec2,
    lightDir: Vec2,
    viewDir: Vec2,
    diffuse: Float,
    specular: Float
): Float {
    val halfVector = (lightDir + viewDir).normalize()

    val ndotl = max(0f, normal.dot(lightDir))
    val ndoth = max(0f, normal.dot(halfVector))

    return diffuse * ndotl + specular * ndoth * ndoth
}