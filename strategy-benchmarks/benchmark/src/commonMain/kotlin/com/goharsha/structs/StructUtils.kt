package com.goharsha.structs

fun writeFloat(buffer: ByteArray, offset: Int, value: Float) {
    val intBits = value.toRawBits()
    buffer[offset] = (intBits and 0xFF).toByte()
    buffer[offset + 1] = ((intBits ushr 8) and 0xFF).toByte()
    buffer[offset + 2] = ((intBits ushr 16) and 0xFF).toByte()
    buffer[offset + 3] = ((intBits ushr 24) and 0xFF).toByte()
}

fun readFloat(buffer: ByteArray, offset: Int): Float {
    val intBits = (buffer[offset].toInt() and 0xFF) or
            ((buffer[offset + 1].toInt() and 0xFF) shl 8) or
            ((buffer[offset + 2].toInt() and 0xFF) shl 16) or
            ((buffer[offset + 3].toInt() and 0xFF) shl 24)
    return Float.fromBits(intBits)
}

fun packInts(a: Int, b: Int): Long =
    (a.toLong() and 0xFFFFFFFF) or ((b.toLong() and 0xFFFFFFFF) shl 32)

fun unpackIntsA(value: Long): Int =
    (value and 0xFFFFFFFF).toInt()

fun unpackIntsB(value: Long): Int =
    ((value ushr 32) and 0xFFFFFFFF).toInt()

fun packFloats(a: Float, b: Float): Long =
    packInts(a.toRawBits(), b.toRawBits())

fun unpackFloatsA(value: Long): Float =
    Float.fromBits(unpackIntsA(value))

fun unpackFloatsB(value: Long): Float =
    Float.fromBits(unpackIntsB(value))