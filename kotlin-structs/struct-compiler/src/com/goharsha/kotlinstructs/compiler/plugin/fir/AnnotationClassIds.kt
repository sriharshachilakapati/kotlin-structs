package com.goharsha.kotlinstructs.compiler.plugin.fir

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object AnnotationClassIds {
    val STRUCT = ClassId(
        packageFqName = FqName("com.goharsha.kotlinstructs"),
        topLevelName = Name.identifier("Struct")
    )

    val NO_LOWERING = ClassId(
        packageFqName = FqName("com.goharsha.kotlinstructs"),
        topLevelName = Name.identifier("NoLowering")
    )
}