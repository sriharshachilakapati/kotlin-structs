package com.goharsha.kotlinstructs.compiler.plugin.fir.checkers

import com.goharsha.kotlinstructs.compiler.plugin.fir.diagnostics.StructErrors
import com.goharsha.kotlinstructs.compiler.plugin.fir.isStruct
import com.goharsha.kotlinstructs.compiler.plugin.fir.isStructUnsafeSuperType
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.context.findClosest
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirReturnExpressionChecker
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.resolvedType

object StructReturnChecker : FirReturnExpressionChecker(MppCheckerKind.Common) {

    @OptIn(SymbolInternals::class)
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirReturnExpression) {
        val function = context.findClosest<FirFunctionSymbol<FirFunction>>()
        val returnType = function?.fir?.returnTypeRef?.coneType ?: return

        val returnedExpr = expression.result
        val returnedType = returnedExpr.resolvedType

        if (!returnedType.isStruct(context.session)) return

        if (returnType.isStructUnsafeSuperType(context.session)) {
            reporter.reportOn(expression.result.source, StructErrors.FORBIDDEN_STRUCT_CASTING)
        }
    }
}