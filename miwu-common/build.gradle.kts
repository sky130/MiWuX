import org.gradle.kotlin.dsl.sourceSets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotReload)
}

group = "miwu.common"
version = "unspecified"


kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.animation)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.miwu.miot.api)
            implementation(libs.miwu.support)
            implementation(libs.miwu.support.annotation)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "miwu.common.resources"
    generateResClass = auto
}