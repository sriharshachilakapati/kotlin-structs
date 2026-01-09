package com.goharsha.kotlinstructs.compiler.plugin.fir

import com.goharsha.kotlinstructs.BuildConfig
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory

object StructErrorMessages : BaseDiagnosticRendererFactory() {
    override val MAP by KtDiagnosticFactoryToRendererMap(BuildConfig.KOTLIN_PLUGIN_ID) { map ->
        map.put(
            StructErrors.VAR_FIELD_IN_STRUCT,
            "Structs cannot have 'var' fields. Make sure that field {0} is declared using 'val'."
        )

        map.put(
            StructErrors.INHERITANCE_IN_STRUCT,
            "Structs cannot participate in inheritance. Ensure the struct does not extend or implement any classes or interfaces."
        )

        map.put(
            StructErrors.NON_FINAL_STRUCT,
            "Structs must be declared as 'final'. Remove any 'open' or 'abstract' modifiers from the struct declaration."
        )
    }
}