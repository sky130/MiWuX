import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotReload)
    kotlin("plugin.serialization") version "2.3.0"
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":miwu-common"))
            implementation(project(":miwu-compose"))


            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.jetbrains.androidx.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.androidx.lifecycle.runtime.compose)
            implementation(libs.jetbrains.androidx.lifecycle.viewmodel.nav3)


            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core)


            implementation(libs.resultat)
            implementation(libs.russhwolf.multiplatform.settings)
            implementation(libs.russhwolf.multiplatform.settings.coroutines)
            implementation(libs.russhwolf.multiplatform.settings.datastore)


            implementation(libs.androidx.datastore.preferences)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.okhttp)
            implementation(libs.jetbrains.androidx.navigation3.ui)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
       }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.androidx.datastore.core.jvm)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.qrose)
            implementation(libs.miwu.miot.api)
            implementation(libs.miwu.miot.api.kmp.impl)
            implementation(libs.miwu.support)
            implementation(libs.miwu.support.annotation)
            // implementation(libs.androidx.navigation3.runtime.jvmstubs)
            // implementation(libs.miwu.support.processor)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.miwu.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.github.miwu"
            packageVersion = "1.0.0"
        }
    }
}
