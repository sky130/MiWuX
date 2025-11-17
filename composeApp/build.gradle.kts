import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // alias(libs.plugins.composeHotReload)
    kotlin("plugin.serialization") version "2.2.0"
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.core)
            implementation(libs.resultat)
            implementation(libs.russhwolf.multiplatform.settings)
            implementation(libs.russhwolf.multiplatform.settings.coroutines)
            implementation(libs.russhwolf.multiplatform.settings.datastore)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.androidx.datastore.preferences)
            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
            // implementation(libs.voyager.navigator)
            // implementation(libs.voyager.screenModel)
            // implementation(libs.voyager.bottomSheetNavigator)
            // implementation(libs.voyager.transitions)
            // implementation(libs.voyager.koin)
            implementation(project(":miwu-common"))
            implementation(project(":miwu-compose"))
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta05")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.androidx.datastore.core.jvm)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.qrose)
            implementation(libs.gson)
            implementation(libs.miwu.miot.api)
            implementation(libs.miwu.miot.api.impl)
            implementation(libs.miwu.support)
            implementation(libs.miwu.support.annotation)
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
