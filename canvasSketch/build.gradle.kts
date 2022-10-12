plugins {
    id(Plugins.library)
    kotlin(Plugins.android)
}

android {
    compileSdk = ModuleExtension.compileSdkVersion

    defaultConfig {
        minSdk = ModuleExtension.DefaultConfigs.minSdkVersion
        targetSdk = ModuleExtension.DefaultConfigs.targetSdkVersion

        testInstrumentationRunner = ModuleExtension.DefaultConfigs.testInstrumentationRunner
        consumerProguardFiles(ModuleExtension.DefaultConfigs.defaultConsumerProguardFiles)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = ModuleExtension.jvmTarget
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.bundles.accompanist)
    implementation(libs.compose.compiler)
    implementation(libs.compose.ui)
    implementation(libs.compose.activity)
    implementation(libs.compose.extended.icons)
    implementation(libs.bundles.compose.jetpack)
    implementation(libs.bundles.coroutines)
    implementation(libs.compose.ui.preview)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material3)
    implementation(libs.graphics.glm)

    debugImplementation(libs.debug.compose.ui.tooling)
}
