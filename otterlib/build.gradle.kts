plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    // dagger hilt
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    // parcel impl
    id("kotlin-parcelize")

    // apollo graphql
    id("com.apollographql.apollo3") version "3.8.2"

    // serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20"
}

android {
    namespace = "tel.jeelpa.otterlib"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    // dagger-hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    // kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // apollo client for anilist
    implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")

    // Jsoup
    implementation("org.jsoup:jsoup:1.16.1")

    // defaults
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // dataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}

apollo {
    service("service") {
        packageName.set("tel.jeelpa.otter")
    }
}

kapt {
    correctErrorTypes = true
}
