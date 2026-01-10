// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

@Struct
class Vec2(
    val x: Float,
    val y: Float
)

val instance = Vec2(1.0f, 2.0f)
val identicalHashCode = System.identityHashCode(<!FORBIDDEN_STRUCT_CASTING!>instance<!>)           // should be an error
val identicalHashCode2 = System.identityHashCode(<!FORBIDDEN_STRUCT_CASTING!>Vec2(3.0f, 4.0f)<!>)  // should be an error

class Test(anyVal: Any)

val testInstance = Test(<!FORBIDDEN_STRUCT_CASTING!>instance<!>)  // should be an error