plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.soze.studio9910"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.soze.studio9910"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Retrofit – 핵심 HTTP 클라이언트
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // JSON ↔ 객체 변환용 Gson 컨버터
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // 네트워크 로깅(interceptor)
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // 코루틴 – 비동기 처리를 위해
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")

    // (선택) ViewModel / Lifecycle KTX – 구조화된 아키텍처용
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Firebase Auth (Kotlin용)
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

    // Coroutines for Firebase Task API
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}