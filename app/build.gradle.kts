plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}
// A chave OPENROUTER_API_KEY deve ser configurada via secrets.properties (não comite arquivos com a chave)
val openRouterKey = providers.gradleProperty("OPENROUTER_API_KEY").getOrElse("")

android {
    namespace = "com.studiocar.studio"
  compileSdk = 37


    defaultConfig {
        applicationId = "com.studiocar.studio"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "OPENROUTER_API_KEY", "\"$openRouterKey\"")
        
        // Configuração para o MediaPipe
        ndk {
            // Inclui x86_64 para suporte a emuladores, mantendo o alinhamento de 16 KB.
            // Removemos armeabi-v7a para garantir compatibilidade total com 16 KB (64-bit apenas).
            abiFilters.addAll(listOf("arm64-v8a", "x86_64"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Força um único APK independente da arquitetura (Universal)
            // Força suporte a arquiteturas 64-bit e legado 32-bit (ARM)
            ndk {
                abiFilters.addAll(listOf("arm64-v8a", "x86_64"))
            }
        }
    }

    packaging {
        jniLibs {
            // Resolve o erro de alinhamento ELF de 16 KB no Android 15 desativando o empacotamento legado
            useLegacyPackaging = false
            excludes += listOf("/META-INF/**")
            
            // Mantém símbolos de debug para bibliotecas que falham no stripping (evita avisos e corrupção)
            keepDebugSymbols += listOf(
                "**/libandroidx.graphics.path.so",
                "**/libbarhopper_v3.so",
                "**/libdatastore_shared_counter.so",
                "**/libimage_processing_util_jni.so",
                "**/libmediapipe_tasks_vision_jni.so",
                "**/libmediapipe_tasks_jni.so",
                "**/libmlkit_google_ocr_pipeline.so",
                "**/libonnxruntime.so",
                "**/libonnxruntime4j_jni.so",
                "**/libsurface_util_jni.so"
            )
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

kotlin {
    jvmToolchain(17)
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.google.material)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
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
    implementation(libs.retrofit.serialization)
    
    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    
    // Database & Extras
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)

    // ML Kit & Security
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.mlkit.text.recognition)
    implementation(libs.androidx.security.crypto)

    // ONNX Runtime for SAM 2 (Atualizado para suportar alinhamento 16 KB)
    implementation(libs.onnxruntime.android)
    
    // Google AI SDK (Gemini)
    implementation(libs.generativeai)
}