package com.goharsha.kotlinstructs.compiler.plugin.fir.checkers

import com.goharsha.kotlinstructs.compiler.plugin.fir.diagnostics.StructErrors
import com.goharsha.kotlinstructs.compiler.plugin.fir.isStruct
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.StandardClassIds

object StructPropertyDeclarationChecker : FirPropertyChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirProperty) {
        val lhsType = declaration.returnTypeRef.coneType
        val rhsType = declaration.initializer?.resolvedType ?: return

        if (lhsType.classId == StandardClassIds.Any && rhsType.isStruct(context.session)) {
            reporter.reportOn(declaration.initializer?.source ?: declaration.source, StructErrors.FORBIDDEN_STRUCT_CASTING)
        }
    }
}