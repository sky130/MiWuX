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

compose.resources {
    publicResClass = true
    packageOfResClass = "miwu.common.resources"
    generateResClass = auto
}