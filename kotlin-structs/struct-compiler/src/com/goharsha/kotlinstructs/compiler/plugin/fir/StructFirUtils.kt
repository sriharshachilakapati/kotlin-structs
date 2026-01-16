package com.goharsha.kotlinstructs.compiler.plugin.fir

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.utils.modality
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeTypeParameterType
import org.jetbrains.kotlin.fir.types.isAnyOrNullableAny

fun FirClass.isStruct(session: FirSession): Boolean =
    hasAnnotation(AnnotationClassIds.STRUCT, session)

fun FirRegularClassSymbol.isStruct() =
    resolvedAnnotationClassIds.any { it == AnnotationClassIds.STRUCT }

fun ConeKotlinType.isStruct(session: FirSession): Boolean =
    toRegularClassSymbol(session)?.isStruct() == true

fun ConeKotlinType.isStructUnsafeSuperType(session: FirSession): Boolean {
    if (isAnyOrNullableAny) return true

    if (this is ConeTypeParameterType) return true

    val classSymbol = toClassSymbol(session) ?: return true
    if (classSymbol.classKind == ClassKind.INTERFACE) return true

    if (classSymbol.modality != Modality.FINAL) return true

    if (!isStruct(session)) return true

    return false
}