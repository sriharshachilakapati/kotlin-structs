// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

<!NON_FINAL_STRUCT!>@Struct // should be an error
open class Vec2f(
    val x: Float,
    val y: Float
)<!>
