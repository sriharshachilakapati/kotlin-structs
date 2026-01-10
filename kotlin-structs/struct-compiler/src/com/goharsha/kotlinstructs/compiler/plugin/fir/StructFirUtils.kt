package com.goharsha.kotlinstructs.compiler.plugin.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType

fun FirClass.isStruct(session: FirSession): Boolean =
    hasAnnotation(AnnotationClassIds.STRUCT, session)

fun FirRegularClassSymbol.isStruct() =
    resolvedAnnotationClassIds.any { it == AnnotationClassIds.STRUCT }

fun ConeKotlinType.isStruct(session: FirSession): Boolean =
    toRegularClassSymbol(session)?.isStruct() == true