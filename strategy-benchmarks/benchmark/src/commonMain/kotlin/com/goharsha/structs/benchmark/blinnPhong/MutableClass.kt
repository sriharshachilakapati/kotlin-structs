package com.goharsha.structs.benchmark.blinnPhong

import com.goharsha.structs.model.MutableVec2
import kotlin.math.max

fun blinnPhongKernelMutableClass(
    normal: MutableVec2,
    lightDir: MutableVec2,
    viewDir: MutableVec2,
    diffuse: Float,
    specular: Float
): Float {
    val halfVector = MutableVec2(lightDir.x, lightDir.y)
    halfVector.plus(viewDir)
    halfVector.normalize()

    val ndotl = max(0f, normal.dot(lightDir))
    val ndoth = max(0f, normal.dot(halfVector))

    return diffuse * ndotl + specular * ndoth * ndoth
}