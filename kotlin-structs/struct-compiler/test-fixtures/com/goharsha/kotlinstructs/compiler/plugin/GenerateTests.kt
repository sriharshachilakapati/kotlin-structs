package com.goharsha.kotlinstructs.compiler.plugin

import com.goharsha.kotlinstructs.compiler.plugin.runners.AbstractJvmBoxTest
import com.goharsha.kotlinstructs.compiler.plugin.runners.AbstractJvmDiagnosticTest
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "struct-compiler/testData", testsRoot = "struct-compiler/test-gen") {
            testClass<AbstractJvmDiagnosticTest> {
                model("diagnostics")
            }

            testClass<AbstractJvmBoxTest> {
                model("box")
            }
        }
    }
}
