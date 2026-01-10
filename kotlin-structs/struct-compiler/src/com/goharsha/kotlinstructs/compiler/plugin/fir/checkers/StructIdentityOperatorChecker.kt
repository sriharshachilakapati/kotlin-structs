package com.goharsha.kotlinstructs.compiler.plugin.fir.checkers

import com.goharsha.kotlinstructs.compiler.plugin.fir.StructErrors.IDENTITY_ON_STRUCT
import com.goharsha.kotlinstructs.compiler.plugin.fir.isStruct
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirExpressionChecker
import org.jetbrains.kotlin.fir.expressions.FirEqualityOperatorCall
import org.jetbrains.kotlin.fir.expressions.FirOperation
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.types.resolvedType

object StructIdentityOperatorChecker : FirExpressionChecker<FirEqualityOperatorCall>(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirEqualityOperatorCall) {
        val op = expression.operation

        if (op != FirOperation.IDENTITY && op != FirOperation.NOT_IDENTITY) {
            return
        }

        val lhsType = expression.arguments[0].resolvedType
        val rhsType = expression.arguments[1].resolvedType

        if (lhsType.isStruct(context.session) || rhsType.isStruct(context.session)) {
            reporter.reportOn(expression.source, IDENTITY_ON_STRUCT)
        }
    }
}