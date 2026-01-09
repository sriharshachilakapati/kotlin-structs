// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

@Struct
class Vec2(
    <!VAR_FIELD_IN_STRUCT!>var x: Float<!>, // should be an error
    val y: Float
)
