plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxBenchmark)
}

kotlin {
    js {
        nodejs()
    }

    jvm()

    linuxX64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
        }
    }
}

benchmark {
    configurations {
        named("main") {
            warmups = 20
            iterations = 10
            iterationTime = 5L
            iterationTimeUnit = "s"
            outputTimeUnit = "ns"
            mode = "AverageTime"
        }
    }

    targets {
        register("jvm")
        register("js")
        register("linuxX64")
        register("macosArm64")
    }
}
