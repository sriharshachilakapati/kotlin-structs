package com.goharsha.kotlinstructs.compiler.plugin.fir.diagnostics

import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.psi.KtElement

object StructErrors : KtDiagnosticsContainer() {
    val VAR_FIELD_IN_STRUCT by diagnosticsError0<KtElement>()
    val INHERITANCE_IN_STRUCT by diagnosticsError0<KtElement>()
    val NON_FINAL_STRUCT by diagnosticsError0<KtElement>()
    val IDENTITY_ON_STRUCT by diagnosticsError0<KtElement>()
    val FORBIDDEN_STRUCT_CASTING by diagnosticsError0<KtElement>()

    override fun getRendererFactory() = StructErrorMessages
}