package com.goharsha.kotlinstructs.compiler.plugin.fir.checkers

import com.goharsha.kotlinstructs.compiler.plugin.fir.StructErrors
import com.goharsha.kotlinstructs.compiler.plugin.fir.isStruct
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.declarations.utils.isMemberDeclaration
import org.jetbrains.kotlin.fir.declarations.utils.isOpen
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.name.StandardClassIds

object StructClassValidityChecker : FirClassChecker(MppCheckerKind.Common) {

    @OptIn(SymbolInternals::class)
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        if (!declaration.isStruct(context.session)) {
            return
        }

        // Check for 'var' fields
        declaration.processAllDeclarations(context.session) { member ->
            if (member.isMemberDeclaration && (member.fir as? FirProperty)?.isVar == true) {
                reporter.reportOn(member.fir.source, StructErrors.VAR_FIELD_IN_STRUCT)
            }
        }

        // Check for inheritance
        for (superType in declaration.superTypeRefs) {
            if (superType.coneType.classId == StandardClassIds.Any) {
                continue
            }

            reporter.reportOn(declaration.source, StructErrors.INHERITANCE_IN_STRUCT)
            break
        }

        // Check for final modifier
        if (declaration.isOpen) {
            reporter.reportOn(declaration.source, StructErrors.NON_FINAL_STRUCT)
        }
    }
}