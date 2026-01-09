plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.binary.compatibility.validator) apply false
    alias(libs.plugins.buildconfig) apply false
}

allprojects {
    group = "com.goharsha.kotlinstructs"
    version = "0.0.1-SNAPSHOT"
}
