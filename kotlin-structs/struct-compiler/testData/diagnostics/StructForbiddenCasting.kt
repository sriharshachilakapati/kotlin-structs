// RUN_PIPELINE_TILL: FRONTEND

package foo.bar

import com.goharsha.kotlinstructs.Struct

class Vec3(
    val x: Float,
    val y: Float,
    val z: Float
)

val instance1 = Vec3(1.0f, 2.0f, 3.0f)
val instanceAsAny1 = instance1 as Any                  // should be allowed

val isInstanceAsAny1Vec3 = instanceAsAny1 is Vec3      // should be allowed
val isInstanceAsAny1NotVec3 = instanceAsAny1 !is Vec3  // should be allowed

@Struct
class Vec2(
    val x: Float,
    val y: Float
)

val instance2 = Vec2(1.0f, 2.0f)
val instanceAsAny2 = <!FORBIDDEN_STRUCT_CASTING!>instance2 as Any<!>   // should be error

val isInstanceAsAny2Vec2 = <!FORBIDDEN_STRUCT_CASTING!>instanceAsAny2 is Vec2<!>     // should be error
val isInstanceAsAny2NotVec2 = <!FORBIDDEN_STRUCT_CASTING!>instanceAsAny2 !is Vec2<!> // should be error
val isInstanceAsAny2NotVec3 = instanceAsAny2 !is Vec3