// TODO: get plugins from version catalog once this issue is tackled: https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(libs.plugins.android.application.get().pluginId).version(libs.versions.gradle.get()).apply(false)
    id(libs.plugins.android.library.get().pluginId).version(libs.versions.gradle.get()).apply(false)
    id(libs.plugins.kotlin.android.get().pluginId).version(libs.versions.kotlin.get()).apply(false)
    kotlin(libs.plugins.kotlin.serialization.get().pluginId).version(libs.versions.kotlin.get())
    id(libs.plugins.ktlint.get().pluginId).version(libs.versions.ktlint.get())
    id(libs.plugins.detekt.get().pluginId).version(libs.versions.detekt.get())
}

buildscript {
    dependencies {
        classpath(libs.hilt.classpath)
    }
}

subprojects {
    apply(plugin = Plugins.ktlint)
    apply(plugin = Plugins.detekt)

    detekt {
        config = files("$rootDir/${ModuleExtension.FilePath.detekt}")
        buildUponDefaultConfig = true
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            )
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
