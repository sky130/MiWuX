import org.gradle.kotlin.dsl.sourceSets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotReload)
    alias(libs.plugins.ksp)
}

group = "miwu.compose"
version = "unspecified"

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            api(project(":miwu-common"))
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
        }
        jvmMain.dependencies {
            implementation(libs.miwu.miot.api)
            implementation(libs.miwu.support)
            implementation(libs.miwu.support.annotation)
        }
    }
}


ksp {

}

//ksp {
//    arg("miwu.spec.enabled", "false")
//    arg("miwu.icon.enabled", "false")
//}

dependencies {
    add("kspJvm", libs.miwu.support.processor)
    add("kspJvm", project(":miwu-compose-processor"))
}

afterEvaluate {
    tasks.named("compileKotlinJvm") {
        dependsOn("kspKotlinJvm")
    }
}

