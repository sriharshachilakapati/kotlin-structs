package com.goharsha.structs.model

import com.goharsha.structs.packFloats
import com.goharsha.structs.writeFloat
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// #region: Struct Generalized
const val STRUCT_SIZE_VEC2 = 2 * Float.SIZE_BYTES
const val STRUCT_VEC2_X_OFFSET = 0
const val STRUCT_VEC2_Y_OFFSET = Float.SIZE_BYTES

fun Vec2_plus(ax: Float, ay: Float, bx: Float, by: Float, out: ByteArray, offset: Int = 0) {
    writeFloat(out, offset + STRUCT_VEC2_X_OFFSET, ax + bx)
    writeFloat(out, offset + STRUCT_VEC2_Y_OFFSET, ay + by)
}

fun Vec2_times(ax: Float, ay: Float, scalar: Float, out: ByteArray, offset: Int = 0) {
    writeFloat(out, offset + STRUCT_VEC2_X_OFFSET, ax * scalar)
    writeFloat(out, offset + STRUCT_VEC2_Y_OFFSET, ay * scalar)
}

fun Vec2_scale(
    ax: Float, ay: Float,
    sx: Float, sy: Float,
    out: ByteArray, offset: Int = 0
) {
    writeFloat(out, offset + STRUCT_VEC2_X_OFFSET, ax * sx)
    writeFloat(out, offset + STRUCT_VEC2_Y_OFFSET, ay * sy)
}

fun Vec2_rotate(
    ax: Float, ay: Float,
    angleRadians: Float,
    out: ByteArray, offset: Int = 0
) {
    val cosTheta = cos(angleRadians)
    val sinTheta = sin(angleRadians)

    writeFloat(out, offset + STRUCT_VEC2_X_OFFSET, ax * cosTheta - ay * sinTheta)
    writeFloat(out, offset + STRUCT_VEC2_Y_OFFSET, ax * sinTheta + ay * cosTheta)
}

fun Vec2_normalize(ax: Float, ay: Float, out: ByteArray, offset: Int = 0) {
    val mag = Vec2_magnitude(ax, ay)
    if (mag == 0f) {
        writeFloat(out, offset + STRUCT_VEC2_X_OFFSET, 0f)
        writeFloat(out, offset + STRUCT_VEC2_Y_OFFSET, 0f)
    } else {
        writeFloat(out, offset + STRUCT_VEC2_X_OFFSET, ax / mag)
        writeFloat(out, offset + STRUCT_VEC2_Y_OFFSET, ay / mag)
    }
}

// #region: Struct Specialized (Floats)
const val STRUCT_VEC2_FIELD_COUNT = 2
const val STRUCT_VEC2_X_INDEX = 0
const val STRUCT_VEC2_Y_INDEX = 1

fun Vec2_plus(ax: Float, ay: Float, bx: Float, by: Float, out: FloatArray, offset: Int = 0) {
    out[offset + STRUCT_VEC2_X_INDEX] = ax + bx
    out[offset + STRUCT_VEC2_Y_INDEX] = ay + by
}

fun Vec2_times(ax: Float, ay: Float, scalar: Float, out: FloatArray, offset: Int = 0) {
    out[offset + STRUCT_VEC2_X_INDEX] = ax * scalar
    out[offset + STRUCT_VEC2_Y_INDEX] = ay * scalar
}

fun Vec2_scale(ax: Float, ay: Float, sx: Float, sy: Float, out: FloatArray, offset: Int = 0) {
    out[offset + STRUCT_VEC2_X_INDEX] = ax * sx
    out[offset + STRUCT_VEC2_Y_INDEX] = ay * sy
}

fun Vec2_rotate(
    ax: Float, ay: Float,
    angleRadians: Float,
    out: FloatArray, offset: Int = 0
) {
    val cosTheta = cos(angleRadians)
    val sinTheta = sin(angleRadians)

    out[offset + STRUCT_VEC2_X_INDEX] = ax * cosTheta - ay * sinTheta
    out[offset + STRUCT_VEC2_Y_INDEX] = ax * sinTheta + ay * cosTheta
}

fun Vec2_normalize(ax: Float, ay: Float, out: FloatArray, offset: Int = 0) {
    val mag = Vec2_magnitude(ax, ay)
    if (mag == 0f) {
        out[offset + STRUCT_VEC2_X_INDEX] = 0f
        out[offset + STRUCT_VEC2_Y_INDEX] = 0f
    } else {
        out[offset + STRUCT_VEC2_X_INDEX] = ax / mag
        out[offset + STRUCT_VEC2_Y_INDEX] = ay / mag
    }
}

// #region: Struct packed
fun Vec2_plus(ax: Float, ay: Float, bx: Float, by: Float): Long {
    return packFloats(ax + bx, ay + by)
}

fun Vec2_times(ax: Float, ay: Float, scalar: Float): Long {
    return packFloats(ax * scalar, ay * scalar)
}

fun Vec2_scale(ax: Float, ay: Float, sx: Float, sy: Float): Long {
    return packFloats(ax * sx, ay * sy)
}

fun Vec2_rotate(ax: Float, ay: Float, angleRadians: Float): Long {
    val cosTheta = cos(angleRadians)
    val sinTheta = sin(angleRadians)

    return packFloats(
        ax * cosTheta - ay * sinTheta,
        ax * sinTheta + ay * cosTheta
    )
}

fun Vec2_normalize(ax: Float, ay: Float): Long {
    val mag = Vec2_magnitude(ax, ay)
    return if (mag == 0f) {
        packFloats(0f, 0f)
    } else {
        packFloats(ax / mag, ay / mag)
    }
}

// #region: Struct Common
fun Vec2_dot(ax: Float, ay: Float, bx: Float, by: Float): Float {
    return ax * bx + ay * by
}

fun Vec2_magnitude(ax: Float, ay: Float): Float {
    return sqrt(Vec2_dot(ax, ay, ax, ay))
}