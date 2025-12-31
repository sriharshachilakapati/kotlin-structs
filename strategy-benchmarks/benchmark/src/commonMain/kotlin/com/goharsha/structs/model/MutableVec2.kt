package com.goharsha.structs.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MutableVec2(var x: Float, var y: Float) {

    fun plus(other: MutableVec2) {
        this.x += other.x
        this.y += other.y
    }

    fun times(scalar: Float) {
        this.x *= scalar
        this.y *= scalar
    }

    fun scale(scale: MutableVec2) {
        this.x *= scale.x
        this.y *= scale.y
    }

    fun rotate(angleRadians: Float) {
        val cosTheta = cos(angleRadians)
        val sinTheta = sin(angleRadians)

        val newX = x * cosTheta - y * sinTheta
        val newY = x * sinTheta + y * cosTheta

        this.x = newX
        this.y = newY
    }

    fun dot(other: MutableVec2): Float {
        return this.x * other.x + this.y * other.y
    }

    fun magnitude(): Float {
        return sqrt(dot(this))
    }

    fun normalize() {
        val mag = magnitude()
        if (mag != 0f) {
            this.x /= mag
            this.y /= mag
        } else {
            this.x = 0f
            this.y = 0f
        }
    }
}