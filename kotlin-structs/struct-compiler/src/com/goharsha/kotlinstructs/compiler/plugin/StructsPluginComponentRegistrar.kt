package com.goharsha.kotlinstructs.compiler.plugin

import com.goharsha.kotlinstructs.BuildConfig
import com.goharsha.kotlinstructs.compiler.plugin.fir.StructsFirExtensionRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

import com.goharsha.kotlinstructs.compiler.plugin.ir.StructsIrGenerationExtension

class StructsPluginComponentRegistrar: CompilerPluginRegistrar() {

    override val pluginId: String
        get() = BuildConfig.KOTLIN_PLUGIN_ID

    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        FirExtensionRegistrarAdapter.registerExtension(StructsFirExtensionRegistrar())
        IrGenerationExtension.registerExtension(StructsIrGenerationExtension())
    }
}
