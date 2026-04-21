plugins {
    alias(libs.plugins.android.application)
}


android {
    namespace = "com.studio.tline"
  compileSdk = 36

    defaultConfig {
        applicationId = "com.studio.tline"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Chave OpenRouter do secrets.properties
        // Para usar a chave OpenRouter, crie um arquivo secrets.properties na raiz do projeto
        // com a seguinte linha: OPENROUTER_API_KEY="sua_chave_aqui"
        val openRouterKey = "CHAVE_OPENROUTER_AQUI" // Substitua pela sua chave real ou configure via secrets.properties
        buildConfigField("String", "OPENROUTER_API_KEY", "\"$openRouterKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Força um único APK independente da arquitetura (Universal)
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            }
        }
    }

    // Configuração do nome do APK final (movida para androidComponents para compatibilidade com Kotlin DSL)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.mediapipe.tasks.vision)
    
    // CameraX + Extensions (HDR)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    
    // Image & EXIF
    implementation(libs.coil.compose)
    implementation(libs.timber)
    implementation(libs.androidx.exifinterface)
    
    // Networking
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    
    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    
    // Database & Extras
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)
}