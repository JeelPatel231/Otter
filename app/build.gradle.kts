import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // dagger hilt
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    // kotlin navigation safe args plugin
    // NOTE: if you are using java, remove `.kotlin`
    id("androidx.navigation.safeargs.kotlin")

    // kotlin parcel
    id("kotlin-parcelize")

    // serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20"
}

val configProperties = Properties().apply { load(rootProject.file("config.properties").inputStream()) }

android {
    namespace = "tel.jeelpa.otter"
    compileSdk = 34

    defaultConfig {
        applicationId = "tel.jeelpa.otter"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    applicationVariants.all {
        configProperties.entries.forEach {
            buildConfigField("String", it.key.toString(), it.value.toString())
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    // dagger-hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")
    // shimmer effect
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    // image loading
    implementation("io.coil-kt:coil:2.3.0")
    // markwon markdown
    implementation("io.noties.markwon:core:4.6.2")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // exoplayer
    val media3_version = "1.1.1"
    // For media playback using ExoPlayer
    implementation("androidx.media3:media3-exoplayer:$media3_version")

    // For DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:$media3_version")
    // For HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:$media3_version")
    // For loading data using the OkHttp network stack
    implementation("androidx.media3:media3-datasource-okhttp:$media3_version")
    // For building media playback UIs
    implementation("androidx.media3:media3-ui:$media3_version")
    // For exposing and controlling media sessions
    implementation("androidx.media3:media3-session:$media3_version")

    val core_ktx_version = "1.12.0"
    implementation("androidx.core:core-ktx:${core_ktx_version}")
    implementation("androidx.appcompat:appcompat:1.6.1")

    val material_version = "1.10.0"
    implementation("com.google.android.material:material:${material_version}")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // memory leaks
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.11")

    // data layer
    implementation(project(mapOf("path" to ":otterlib")))
    // common plugin middleware reference
    implementation(project(mapOf("path" to ":reference")))

    // reflection
    implementation(kotlin("reflect"))
}

kapt {
    correctErrorTypes = true
}