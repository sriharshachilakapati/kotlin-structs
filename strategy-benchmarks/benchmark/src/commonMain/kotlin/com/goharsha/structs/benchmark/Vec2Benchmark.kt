package com.goharsha.structs.benchmark

import com.goharsha.structs.benchmark.bezierCurve.bezierQuadraticImmutableClass
import com.goharsha.structs.benchmark.bezierCurve.bezierQuadraticMutableClass
import com.goharsha.structs.benchmark.bezierCurve.bezierQuadraticStructPacked
import com.goharsha.structs.benchmark.bezierCurve.bezierQuadraticStructSlotGeneric
import com.goharsha.structs.benchmark.bezierCurve.bezierQuadraticStructSlotSpecialized
import com.goharsha.structs.benchmark.blinnPhong.blinnPhongKernelImmutableClass
import com.goharsha.structs.benchmark.blinnPhong.blinnPhongKernelMutableClass
import com.goharsha.structs.benchmark.blinnPhong.blinnPhongKernelStructPacked
import com.goharsha.structs.benchmark.blinnPhong.blinnPhongKernelStructSlotGeneric
import com.goharsha.structs.benchmark.blinnPhong.blinnPhongKernelStructSlotSpecialized
import com.goharsha.structs.benchmark.transform2d.transformPoint2dImmutableClass
import com.goharsha.structs.benchmark.transform2d.transformPoint2dMutableClass
import com.goharsha.structs.benchmark.transform2d.transformPoint2dStructPacked
import com.goharsha.structs.benchmark.transform2d.transformPoint2dStructSlotGeneric
import com.goharsha.structs.benchmark.transform2d.transformPoint2dStructSlotSpecialized
import com.goharsha.structs.model.MutableVec2
import com.goharsha.structs.model.STRUCT_SIZE_VEC2
import com.goharsha.structs.model.STRUCT_VEC2_FIELD_COUNT
import com.goharsha.structs.model.STRUCT_VEC2_X_OFFSET
import com.goharsha.structs.model.STRUCT_VEC2_Y_OFFSET
import com.goharsha.structs.model.Vec2
import com.goharsha.structs.readFloat
import com.goharsha.structs.writeFloat
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

@State(Scope.Benchmark)
open class Vec2Benchmark {

    private lateinit var normalClass: Vec2
    private lateinit var lightDirClass: Vec2
    private lateinit var viewDirClass: Vec2
    private lateinit var pointClass: Vec2
    private lateinit var scaleClass: Vec2
    private lateinit var translateClass: Vec2

    private lateinit var normalMutableClass: MutableVec2
    private lateinit var lightDirMutableClass: MutableVec2
    private lateinit var viewDirMutableClass: MutableVec2
    private lateinit var pointMutableClass: MutableVec2
    private lateinit var scaleMutableClass: MutableVec2
    private lateinit var translateMutableClass: MutableVec2

    private lateinit var normalStruct: ByteArray
    private lateinit var lightDirStruct: ByteArray
    private lateinit var viewDirStruct: ByteArray
    private lateinit var pointStruct: ByteArray
    private lateinit var scaleStruct: ByteArray
    private lateinit var translateStruct: ByteArray

    private val diffuse = 0.8f
    private val specular = 0.5f
    private val angleRadians = 23f

    @Setup
    fun setup() {
        normalClass = Vec2(0.3f, 0.7f)
        lightDirClass = Vec2(0.6f, 0.4f)
        viewDirClass = Vec2(0.2f, 0.9f)
        pointClass = Vec2(1.0f, 1.0f)
        scaleClass = Vec2(1.5f, 0.5f)
        translateClass = Vec2(2.0f, 3.0f)

        normalMutableClass = MutableVec2(normalClass.x, normalClass.y)
        lightDirMutableClass = MutableVec2(lightDirClass.x, lightDirClass.y)
        viewDirMutableClass = MutableVec2(viewDirClass.x, viewDirClass.y)
        pointMutableClass = MutableVec2(pointClass.x, pointClass.y)
        scaleMutableClass = MutableVec2(scaleClass.x, scaleClass.y)
        translateMutableClass = MutableVec2(translateClass.x, translateClass.y)

        normalStruct = makeVec2Struct(normalClass.x, normalClass.y)
        lightDirStruct = makeVec2Struct(lightDirClass.x, lightDirClass.y)
        viewDirStruct = makeVec2Struct(viewDirClass.x, viewDirClass.y)
        pointStruct = makeVec2Struct(pointClass.x, pointClass.y)
        scaleStruct = makeVec2Struct(scaleClass.x, scaleClass.y)
        translateStruct = makeVec2Struct(translateClass.x, translateClass.y)
    }

    @Benchmark
    fun benchmarkBlinnPhongImmutableClass() =
        blinnPhongKernelImmutableClass(
            normal = normalClass,
            lightDir = lightDirClass,
            viewDir = viewDirClass,
            diffuse = diffuse,
            specular = specular
        )

    @Benchmark
    fun benchmarkBlinnPhongMutableClass() =
        blinnPhongKernelMutableClass(
            normal = normalMutableClass,
            lightDir = lightDirMutableClass,
            viewDir = viewDirMutableClass,
            diffuse = diffuse,
            specular = specular
        )

    @Benchmark
    fun benchmarkBlinnPhongStructPacked() =
        blinnPhongKernelStructPacked(
            normalX = readFloat(normalStruct, STRUCT_VEC2_X_OFFSET),
            normalY = readFloat(normalStruct, STRUCT_VEC2_Y_OFFSET),
            lightDirX = readFloat(lightDirStruct, STRUCT_VEC2_X_OFFSET),
            lightDirY = readFloat(lightDirStruct, STRUCT_VEC2_Y_OFFSET),
            viewDirX = readFloat(viewDirStruct, STRUCT_VEC2_X_OFFSET),
            viewDirY = readFloat(viewDirStruct, STRUCT_VEC2_Y_OFFSET),
            diffuse = diffuse,
            specular = specular
        )

    @Benchmark
    fun benchmarkBlinnPhongStructSlotGeneric() =
        blinnPhongKernelStructSlotGeneric(
            normalX = readFloat(normalStruct, STRUCT_VEC2_X_OFFSET),
            normalY = readFloat(normalStruct, STRUCT_VEC2_Y_OFFSET),
            lightDirX = readFloat(lightDirStruct, STRUCT_VEC2_X_OFFSET),
            lightDirY = readFloat(lightDirStruct, STRUCT_VEC2_Y_OFFSET),
            viewDirX = readFloat(viewDirStruct, STRUCT_VEC2_X_OFFSET),
            viewDirY = readFloat(viewDirStruct, STRUCT_VEC2_Y_OFFSET),
            diffuse = diffuse,
            specular = specular
        )

    @Benchmark
    fun benchmarkBlinnPhongStructSlotSpecialized() =
        blinnPhongKernelStructSlotSpecialized(
            normalX = readFloat(normalStruct, STRUCT_VEC2_X_OFFSET),
            normalY = readFloat(normalStruct, STRUCT_VEC2_Y_OFFSET),
            lightDirX = readFloat(lightDirStruct, STRUCT_VEC2_X_OFFSET),
            lightDirY = readFloat(lightDirStruct, STRUCT_VEC2_Y_OFFSET),
            viewDirX = readFloat(viewDirStruct, STRUCT_VEC2_X_OFFSET),
            viewDirY = readFloat(viewDirStruct, STRUCT_VEC2_Y_OFFSET),
            diffuse = diffuse,
            specular = specular
        )

    @Benchmark
    fun benchmarkBezierCurveImmutableClass() =
        bezierQuadraticImmutableClass(
            p0 = normalClass,
            p1 = lightDirClass,
            p2 = viewDirClass,
            t = 0.5f
        )

    @Benchmark
    fun benchmarkBezierCurveMutableClass(): MutableVec2 {
        val out = MutableVec2(0f, 0f)
        bezierQuadraticMutableClass(
            p0 = normalMutableClass,
            p1 = lightDirMutableClass,
            p2 = viewDirMutableClass,
            t = 0.5f,
            out = out
        )
        return out
    }

    @Benchmark
    fun benchmarkBezierCurveStructPacked() =
        bezierQuadraticStructPacked(
            p0x = readFloat(normalStruct, STRUCT_VEC2_X_OFFSET),
            p0y = readFloat(normalStruct, STRUCT_VEC2_Y_OFFSET),
            p1x = readFloat(lightDirStruct, STRUCT_VEC2_X_OFFSET),
            p1y = readFloat(lightDirStruct, STRUCT_VEC2_Y_OFFSET),
            p2x = readFloat(viewDirStruct, STRUCT_VEC2_X_OFFSET),
            p2y = readFloat(viewDirStruct, STRUCT_VEC2_Y_OFFSET),
            t = 0.5f
        )

    @Benchmark
    fun benchmarkBezierCurveStructSlotGeneric(): ByteArray {
        val out = ByteArray(STRUCT_SIZE_VEC2)
        bezierQuadraticStructSlotGeneric(
            p0x = readFloat(normalStruct, STRUCT_VEC2_X_OFFSET),
            p0y = readFloat(normalStruct, STRUCT_VEC2_Y_OFFSET),
            p1x = readFloat(lightDirStruct, STRUCT_VEC2_X_OFFSET),
            p1y = readFloat(lightDirStruct, STRUCT_VEC2_Y_OFFSET),
            p2x = readFloat(viewDirStruct, STRUCT_VEC2_X_OFFSET),
            p2y = readFloat(viewDirStruct, STRUCT_VEC2_Y_OFFSET),
            t = 0.5f,
            out = out
        )
        return out
    }

    @Benchmark
    fun benchmarkBezierCurveStructSlotSpecialized(): FloatArray {
        val out = FloatArray(STRUCT_VEC2_FIELD_COUNT)
        bezierQuadraticStructSlotSpecialized(
            p0x = readFloat(normalStruct, STRUCT_VEC2_X_OFFSET),
            p0y = readFloat(normalStruct, STRUCT_VEC2_Y_OFFSET),
            p1x = readFloat(lightDirStruct, STRUCT_VEC2_X_OFFSET),
            p1y = readFloat(lightDirStruct, STRUCT_VEC2_Y_OFFSET),
            p2x = readFloat(viewDirStruct, STRUCT_VEC2_X_OFFSET),
            p2y = readFloat(viewDirStruct, STRUCT_VEC2_Y_OFFSET),
            t = 0.5f,
            out = out
        )
        return out
    }

    @Benchmark
    fun benchmarkTransformPoint2dImmutableClass() =
        transformPoint2dImmutableClass(
            point = pointClass,
            translate = translateClass,
            scale = scaleClass,
            rotationRadians = angleRadians
        )

    @Benchmark
    fun benchmarkTransformPoint2dMutableClass(): MutableVec2 {
        val out = MutableVec2(0f, 0f)
        transformPoint2dMutableClass(
            point = pointMutableClass,
            translate = translateMutableClass,
            scale = scaleMutableClass,
            rotationRadians = angleRadians,
            out = out
        )
        return out
    }

    @Benchmark
    fun benchmarkTransformPoint2dStructPacked() =
        transformPoint2dStructPacked(
            pointX = readFloat(pointStruct, STRUCT_VEC2_X_OFFSET),
            pointY = readFloat(pointStruct, STRUCT_VEC2_Y_OFFSET),
            translateX = readFloat(translateStruct, STRUCT_VEC2_X_OFFSET),
            translateY = readFloat(translateStruct, STRUCT_VEC2_Y_OFFSET),
            scaleX = readFloat(scaleStruct, STRUCT_VEC2_X_OFFSET),
            scaleY = readFloat(scaleStruct, STRUCT_VEC2_Y_OFFSET),
            rotationRadians = angleRadians
        )

    @Benchmark
    fun benchmarkTransformPoint2dStructSlotGeneric(): ByteArray {
        val out = ByteArray(STRUCT_SIZE_VEC2)
        transformPoint2dStructSlotGeneric(
            pointX = readFloat(pointStruct, STRUCT_VEC2_X_OFFSET),
            pointY = readFloat(pointStruct, STRUCT_VEC2_Y_OFFSET),
            translateX = readFloat(translateStruct, STRUCT_VEC2_X_OFFSET),
            translateY = readFloat(translateStruct, STRUCT_VEC2_Y_OFFSET),
            scaleX = readFloat(scaleStruct, STRUCT_VEC2_X_OFFSET),
            scaleY = readFloat(scaleStruct, STRUCT_VEC2_Y_OFFSET),
            rotationRadians = angleRadians,
            out = out
        )
        return out
    }

    @Benchmark
    fun benchmarkTransformPoint2dStructSlotSpecialized(): FloatArray {
        val out = FloatArray(STRUCT_VEC2_FIELD_COUNT)
        transformPoint2dStructSlotSpecialized(
            pointX = readFloat(pointStruct, STRUCT_VEC2_X_OFFSET),
            pointY = readFloat(pointStruct, STRUCT_VEC2_Y_OFFSET),
            translateX = readFloat(translateStruct, STRUCT_VEC2_X_OFFSET),
            translateY = readFloat(translateStruct, STRUCT_VEC2_Y_OFFSET),
            scaleX = readFloat(scaleStruct, STRUCT_VEC2_X_OFFSET),
            scaleY = readFloat(scaleStruct, STRUCT_VEC2_Y_OFFSET),
            rotationRadians = angleRadians,
            out = out
        )
        return out
    }

    companion object {
        fun makeVec2Struct(x: Float, y: Float): ByteArray {
            val struct = ByteArray(STRUCT_SIZE_VEC2)
            writeFloat(struct, STRUCT_VEC2_X_OFFSET, x)
            writeFloat(struct, STRUCT_VEC2_Y_OFFSET, y)
            return struct
        }
    }
}