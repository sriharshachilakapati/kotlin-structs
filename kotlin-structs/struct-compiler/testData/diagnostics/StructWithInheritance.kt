// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

interface SomeInterface

<!INHERITANCE_IN_STRUCT!>@Struct
class Vec2(
    val x: Float,
    val y: Float
) : SomeInterface<!> // should be an error

@Struct
class Vec2f(
    val x: Float,
    val y: Float
)

<!INHERITANCE_IN_STRUCT!>@Struct
class Vec3f(
    x: Float,
    y: Float,
    val z: Float
) : <!FINAL_SUPERTYPE!>Vec2f<!>(x, y)<!> // should be an error