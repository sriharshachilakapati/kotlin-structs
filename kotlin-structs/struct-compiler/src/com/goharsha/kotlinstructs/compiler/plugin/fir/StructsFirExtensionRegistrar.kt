package com.goharsha.kotlinstructs.compiler.plugin.fir

import com.goharsha.kotlinstructs.compiler.plugin.fir.checkers.StructFunctionCallChecker
import com.goharsha.kotlinstructs.compiler.plugin.fir.checkers.StructClassValidityChecker
import com.goharsha.kotlinstructs.compiler.plugin.fir.checkers.StructForbiddenCastChecker
import com.goharsha.kotlinstructs.compiler.plugin.fir.checkers.StructIdentityOperatorChecker
import com.goharsha.kotlinstructs.compiler.plugin.fir.checkers.StructPropertyDeclarationChecker
import com.goharsha.kotlinstructs.compiler.plugin.fir.checkers.StructReturnChecker
import com.goharsha.kotlinstructs.compiler.plugin.fir.checkers.StructVariableAssignmentChecker
import com.goharsha.kotlinstructs.compiler.plugin.fir.diagnostics.StructErrors
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirEqualityOperatorCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirReturnExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirTypeOperatorCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirVariableAssignmentChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class StructsFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::StructsFirCheckersExtension

        registerDiagnosticContainers(StructErrors)
    }
}

class StructsFirCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {

    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val classCheckers: Set<FirClassChecker>
            get() = setOf(StructClassValidityChecker)

        override val propertyCheckers: Set<FirPropertyChecker>
            get() = setOf(StructPropertyDeclarationChecker)
    }

    override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
        override val equalityOperatorCallCheckers: Set<FirEqualityOperatorCallChecker> =
            setOf(StructIdentityOperatorChecker)

        override val typeOperatorCallCheckers: Set<FirTypeOperatorCallChecker> =
            setOf(StructForbiddenCastChecker)

        override val functionCallCheckers: Set<FirFunctionCallChecker> =
            setOf(StructFunctionCallChecker)

        override val variableAssignmentCheckers: Set<FirVariableAssignmentChecker> =
            setOf(StructVariableAssignmentChecker)

        override val returnExpressionCheckers: Set<FirReturnExpressionChecker> =
            setOf(StructReturnChecker)
    }
}