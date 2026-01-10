// RUN_PIPELINE_TILL: BACKEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

@Struct
class Vec2(
    val x: Float,
    val y: Float
)

val a = Vec2(1.0f, 2.0f)
val b = Vec2(3.0f, 4.0f)

val areEqual = a == b
val areNotEqual = a != b