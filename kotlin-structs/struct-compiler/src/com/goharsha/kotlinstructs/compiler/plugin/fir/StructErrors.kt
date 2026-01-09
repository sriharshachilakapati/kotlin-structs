package com.goharsha.kotlinstructs.compiler.plugin.fir

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.error0

object StructErrors : KtDiagnosticsContainer() {
    val VAR_FIELD_IN_STRUCT by error0<PsiElement>()
    val INHERITANCE_IN_STRUCT by error0<PsiElement>()
    val NON_FINAL_STRUCT by error0<PsiElement>()

    override fun getRendererFactory() = StructErrorMessages
}