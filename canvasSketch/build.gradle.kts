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
        kotlinCompilerExtensionVersion = libs.versions.compose.core.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.bundles.compose.design)
    implementation(libs.bundles.coroutines)
    implementation(libs.compose.ui.preview)
    implementation(libs.graphics.glm)
    implementation(libs.bundles.compose.jetpack)
    implementation(libs.bundles.accompanist)
    debugImplementation(libs.debug.compose.ui.tooling)
}
