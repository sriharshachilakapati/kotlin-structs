package com.goharsha.structs.benchmark.blinnPhong

import com.goharsha.structs.model.STRUCT_VEC2_FIELD_COUNT
import com.goharsha.structs.model.STRUCT_VEC2_X_INDEX
import com.goharsha.structs.model.STRUCT_VEC2_Y_INDEX
import com.goharsha.structs.model.Vec2_dot
import com.goharsha.structs.model.Vec2_normalize
import com.goharsha.structs.model.Vec2_plus
import kotlin.math.max

fun blinnPhongKernelStructSlotSpecialized(
    normalX: Float, normalY: Float,
    lightDirX: Float, lightDirY: Float,
    viewDirX: Float, viewDirY: Float,
    diffuse: Float,
    specular: Float
): Float {
    val halfVector = FloatArray(STRUCT_VEC2_FIELD_COUNT)

    Vec2_plus(lightDirX, lightDirY, viewDirX, viewDirY, halfVector)
    Vec2_normalize(
        ax = halfVector[STRUCT_VEC2_X_INDEX],
        ay = halfVector[STRUCT_VEC2_Y_INDEX],
        out = halfVector
    )

    val ndotl = max(0f, Vec2_dot(normalX, normalY, lightDirX, lightDirY))

    val ndoth = max(
        0f,
        Vec2_dot(
            ax = normalX,
            ay = normalY,
            bx = halfVector[STRUCT_VEC2_X_INDEX],
            by = halfVector[STRUCT_VEC2_Y_INDEX]
        )
    )

    return diffuse * ndotl + specular * ndoth * ndoth
}