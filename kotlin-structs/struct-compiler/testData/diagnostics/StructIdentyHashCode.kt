// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

@Struct
class Vec2(
    val x: Float,
    val y: Float
)

val instance = Vec2(1.0f, 2.0f)
val identicalHashCode = <!IDENTITY_ON_STRUCT!>System.identityHashCode(instance)<!>           // should be an error
val identicalHashCode2 = <!IDENTITY_ON_STRUCT!>System.identityHashCode(Vec2(3.0f, 4.0f))<!>  // should be an error