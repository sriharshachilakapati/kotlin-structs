package com.goharsha.kotlinstructs.compiler.plugin.fir.checkers

import com.goharsha.kotlinstructs.compiler.plugin.fir.diagnostics.StructErrors
import com.goharsha.kotlinstructs.compiler.plugin.fir.isStruct
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirExpressionChecker
import org.jetbrains.kotlin.fir.expressions.FirOperation
import org.jetbrains.kotlin.fir.expressions.FirTypeOperatorCall
import org.jetbrains.kotlin.fir.expressions.argument
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.resolvedType

object StructForbiddenCastChecker : FirExpressionChecker<FirTypeOperatorCall>(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirTypeOperatorCall) {
        val op = expression.operation

        if (op != FirOperation.AS &&
            op != FirOperation.SAFE_AS &&
            op != FirOperation.IS &&
            op != FirOperation.NOT_IS
        ) {
            return
        }

        val lhsType = expression.argument.resolvedType
        val rhsType = expression.conversionTypeRef.coneType

        if (lhsType.isStruct(context.session) || rhsType.isStruct(context.session)) {
            reporter.reportOn(expression.source, StructErrors.FORBIDDEN_STRUCT_CASTING)
        }
    }
}