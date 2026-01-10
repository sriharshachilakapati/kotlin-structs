package com.goharsha.kotlinstructs.compiler.plugin.fir.checkers

import com.goharsha.kotlinstructs.compiler.plugin.fir.diagnostics.StructErrors
import com.goharsha.kotlinstructs.compiler.plugin.fir.isStruct
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirVariableAssignmentChecker
import org.jetbrains.kotlin.fir.expressions.FirVariableAssignment
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.StandardClassIds

object StructVariableAssignmentChecker : FirVariableAssignmentChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirVariableAssignment) {
        val lhsType = expression.lValue.resolvedType
        val rhsType = expression.rValue.resolvedType

        if (lhsType.classId == StandardClassIds.Any && rhsType.isStruct(context.session)) {
            reporter.reportOn(expression.rValue.source, StructErrors.FORBIDDEN_STRUCT_CASTING)
        }
    }
}