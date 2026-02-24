plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")
}

android {
    kapt {
        correctErrorTypes = true
    }
    namespace = "com.example.marsphotos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.marsphotos"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // ✅ Compose BOM: Cambiado a Junio 2024 para compatibilidad con AGP 8.5.2
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))

    // Eliminadas versiones manuales 1.10.3 que causaban conflicto
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ✅ Lifecycle: Ajustado a 2.8.4 (Estable y compatible)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

    // ✅ Corrutinas: Esenciales para MutableStateFlow y launch
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Core-KTX compatible
    implementation("androidx.core:core-ktx:1.13.1")

    // Retrofit & Serialization
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Recomendado para tu JSON
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.test.espresso:espresso-core:3.7.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Otros
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Testing
    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("org.simpleframework:simple-xml:2.7.1") {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "xpp3", module = "xpp3")
        exclude(group = "stax", module = "stax")
    }
}