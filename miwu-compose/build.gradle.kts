import org.gradle.kotlin.dsl.sourceSets

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.ksp)
}

group = "miwu.compose"
version = "unspecified"

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            api(project(":miwu-common"))
            implementation(compose.runtime)
            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
        jvmMain.dependencies {
            implementation(libs.miwu.miot.api)
            implementation(libs.miwu.support)
            implementation(libs.miwu.support.annotation)
        }
    }
}


dependencies {
    ksp(libs.miwu.android.processor)
    ksp(project(":miwu-compose-processor"))
}


