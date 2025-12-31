package com.goharsha.structs.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Vec2(val x: Float, val y: Float) {
    operator fun plus(other: Vec2): Vec2 {
        return Vec2(this.x + other.x, this.y + other.y)
    }

    operator fun times(scalar: Float): Vec2 {
        return Vec2(this.x * scalar, this.y * scalar)
    }

    fun scale(scale: Vec2): Vec2 {
        return Vec2(this.x * scale.x, this.y * scale.y)
    }

    fun rotate(angleRadians: Float): Vec2 {
        val cosTheta = cos(angleRadians)
        val sinTheta = sin(angleRadians)

        return Vec2(
            x * cosTheta - y * sinTheta,
            x * sinTheta + y * cosTheta
        )
    }

    fun dot(other: Vec2): Float {
        return this.x * other.x + this.y * other.y
    }

    fun magnitude(): Float {
        return sqrt(dot(this))
    }

    fun normalize(): Vec2 {
        val mag = magnitude()
        return if (mag == 0f) Vec2(0f, 0f) else Vec2(this.x / mag, this.y / mag)
    }
}