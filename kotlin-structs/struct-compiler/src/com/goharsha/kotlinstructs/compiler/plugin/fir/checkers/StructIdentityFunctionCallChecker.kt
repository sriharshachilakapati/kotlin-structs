package com.goharsha.kotlinstructs.compiler.plugin.fir.checkers

import com.goharsha.kotlinstructs.compiler.plugin.fir.diagnostics.StructErrors
import com.goharsha.kotlinstructs.compiler.plugin.fir.isStruct
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.toResolvedFunctionSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.StandardClassIds
import kotlin.math.min

object StructIdentityFunctionCallChecker : FirFunctionCallChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        val callee = expression.calleeReference.toResolvedFunctionSymbol() ?: return

        val parameters = callee.valueParameterSymbols
        val arguments = expression.arguments

        // Defensive programming: Ensure we don't go out of bounds
        val count = min(parameters.size, arguments.size)

        for (i in 0 until count) {
            val paramType = parameters[i].resolvedReturnType
            val argType = arguments[i].resolvedType

            // If the function parameter takes Any, we shouldn't be passing a struct type here
            if (paramType.classId == StandardClassIds.Any && argType.isStruct(context.session)) {
                reporter.reportOn(arguments[i].source, StructErrors.IDENTITY_ON_STRUCT)
            }
        }
    }
}