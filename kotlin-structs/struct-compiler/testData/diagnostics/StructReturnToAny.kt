// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

@Struct
class Vec2(
    val x: Float,
    val y: Float
)

class Vec3(
    val x: Float,
    val y: Float,
    val z: Float
)

fun returnsAny(): Any {
    val instance = Vec2(1.0f, 2.0f)
    return <!FORBIDDEN_STRUCT_CASTING!>instance<!> // should be an error
}

fun returnsAny2(): Any {
    val instance = Vec3(1.0f, 2.0f, 3.0f)
    return instance                         // should be fine
}