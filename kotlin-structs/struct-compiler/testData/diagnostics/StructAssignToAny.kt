// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

@Struct
class Vec2(
    val x: Float,
    val y: Float
)

fun main() {
    val instance = Vec2(1.0f, 2.0f)

    var anyVal2: Any? = null
    anyVal2 = <!FORBIDDEN_STRUCT_CASTING!>instance<!>         // should be an error

    val anyVal: Any = <!FORBIDDEN_STRUCT_CASTING!>instance<!> // should be an error
    val anyArray = arrayOfNulls<Any?>(1)
    anyArray[0] = <!FORBIDDEN_STRUCT_CASTING!>instance<!>     // should be an error
}