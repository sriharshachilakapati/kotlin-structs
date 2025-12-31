package com.goharsha.structs.benchmark.blinnPhong

import com.goharsha.structs.model.Vec2_dot
import com.goharsha.structs.model.Vec2_normalize
import com.goharsha.structs.model.Vec2_plus
import com.goharsha.structs.unpackFloatsA
import com.goharsha.structs.unpackFloatsB
import kotlin.math.max

fun blinnPhongKernelStructPacked(
    normalX: Float, normalY: Float,
    lightDirX: Float, lightDirY: Float,
    viewDirX: Float, viewDirY: Float,
    diffuse: Float,
    specular: Float
): Float {
    var halfVector = Vec2_plus(lightDirX, lightDirY, viewDirX, viewDirY)
    halfVector = Vec2_normalize(
        ax = unpackFloatsA(halfVector),
        ay = unpackFloatsB(halfVector)
    )

    val ndotl = max(0f, Vec2_dot(normalX, normalY, lightDirX, lightDirY))
    val ndoth = max(0f, Vec2_dot(normalX, normalY, unpackFloatsA(halfVector), unpackFloatsB(halfVector)))

    return diffuse * ndotl + specular * ndoth * ndoth
}