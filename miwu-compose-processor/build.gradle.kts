import org.gradle.kotlin.dsl.sourceSets

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

group = "miwu.compose.processor"
version = "debug"

dependencies {
    implementation(libs.square.kotlin.poet)
    implementation(libs.squareup.kotlinpoet.ksp)
    implementation(libs.symbol.processing.api)
    implementation(libs.miwu.miot.api)
    implementation(libs.miwu.support)
    implementation(libs.miwu.support.annotation)
}