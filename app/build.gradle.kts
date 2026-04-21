plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}


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
        
        // A chave OPENROUTER_API_KEY deve ser configurada via secrets.properties (não comite arquivos com a chave)
        val openRouterKey = project.findProperty("OPENROUTER_API_KEY") ?: ""
        buildConfigField("String", "OPENROUTER_API_KEY", "\"$openRouterKey\"")
        
        // Configuração para o MediaPipe
        ndk {
            // Mantém apenas ABIs usadas em dispositivos Android reais para evitar
            // empacotar libs x86/x86_64 com problemas de alinhamento de página (16 KB).
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
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
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
            }
        }
    }

    packaging {
        jniLibs {
            // Garante que binários nativos x86/x86_64 não sejam empacotados no artefato final.
            excludes += setOf("**/x86/*.so", "**/x86_64/*.so")
            // Resolve o erro de alinhamento ELF de 16 KB no Android 15 extraindo as libs
            useLegacyPackaging = true
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
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
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
    annotationProcessor(libs.androidx.room.compiler)
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