// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

@Struct
class Vec2(
    val x: Float,
    val y: Float
)

val a = Vec2(1.0f, 2.0f)
val b = Vec2(3.0f, 4.0f)

val areIdentical = <!IDENTITY_ON_STRUCT!>a === b<!>     // should be an error
val areNotIdentical = <!IDENTITY_ON_STRUCT!>a !== b<!>  // should be an error