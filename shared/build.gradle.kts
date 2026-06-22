import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    androidLibrary {
        namespace = "glowie.glowie_app.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kable)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

val copyBuiltinScripts by tasks.registering {
    description = "Copies script files from builtin_scripts/ to Compose Resources and generates manifest.txt"
    val scriptDir = rootProject.layout.projectDirectory.dir("builtin_scripts")
    val targetDir = project.layout.projectDirectory.dir("src/commonMain/composeResources/files/builtin_scripts")

    inputs.dir(scriptDir)
    outputs.dir(targetDir)

    doLast {
        targetDir.asFile.mkdirs()
        scriptDir.asFile.listFiles { f -> f.extension == "lua" }?.forEach { file ->
            file.copyTo(targetDir.file(file.name).asFile, overwrite = true)
        }
        val filenames = scriptDir.asFile.listFiles { f -> f.extension == "lua" }
            ?.map { it.name }
            ?.sorted()
            ?.joinToString("\n") ?: ""
        targetDir.file("manifest.txt").asFile.writeText(filenames)
    }
}

tasks.matching { it.name.startsWith("generateComposeResClass") || it.name.startsWith("copyNonXmlValueResources") }.configureEach {
    dependsOn(copyBuiltinScripts)
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}