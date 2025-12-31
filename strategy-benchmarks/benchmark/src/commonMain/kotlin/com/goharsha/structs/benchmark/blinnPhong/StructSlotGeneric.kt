package com.goharsha.structs.benchmark.blinnPhong

import com.goharsha.structs.model.STRUCT_SIZE_VEC2
import com.goharsha.structs.model.STRUCT_VEC2_X_OFFSET
import com.goharsha.structs.model.STRUCT_VEC2_Y_OFFSET
import com.goharsha.structs.model.Vec2_dot
import com.goharsha.structs.model.Vec2_normalize
import com.goharsha.structs.model.Vec2_plus
import com.goharsha.structs.readFloat
import kotlin.math.max

fun blinnPhongKernelStructSlotGeneric(
    normalX: Float, normalY: Float,
    lightDirX: Float, lightDirY: Float,
    viewDirX: Float, viewDirY: Float,
    diffuse: Float,
    specular: Float
): Float {
    val halfVector = ByteArray(STRUCT_SIZE_VEC2)

    Vec2_plus(lightDirX, lightDirY, viewDirX, viewDirY, halfVector)

    Vec2_normalize(
        ax = readFloat(halfVector, STRUCT_VEC2_X_OFFSET),
        ay = readFloat(halfVector, STRUCT_VEC2_Y_OFFSET),
        out = halfVector
    )

    val ndotl = max(0f, Vec2_dot(normalX, normalY, lightDirX, lightDirY))

    val ndoth = max(
        0f,
        Vec2_dot(
            ax = normalX,
            ay = normalY,
            bx = readFloat(halfVector, STRUCT_VEC2_X_OFFSET),
            by = readFloat(halfVector, STRUCT_VEC2_Y_OFFSET)
        )
    )

    return diffuse * ndotl + specular * ndoth * ndoth
}