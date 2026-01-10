// RUN_PIPELINE_TILL: BACKEND

package foo.bar

class Vec2(
    val x: Float,
    val y: Float
)

val instance = Vec2(1.0f, 2.0f)
val identicalHashCode = System.identityHashCode(instance)
val identicalHashCode2 = System.identityHashCode(Vec2(3.0f, 4.0f))