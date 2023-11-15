plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    // apollo graphql
    id("com.apollographql.apollo3") version "3.8.2"

    // serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20"
}

android {
    namespace = "tel.jeelpa.anilisttrackerplugin"
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
    implementation(project(mapOf("path" to ":trackerinterface")))

    // kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // apollo client for anilist
    api("com.apollographql.apollo3:apollo-runtime:3.8.2")

    // Jsoup
    implementation("org.jsoup:jsoup:1.16.1")


    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}


apollo {
    service("service") {
        packageName.set("tel.jeelpa.otter.anilisttrackerplugin.models")
    }
}


tasks.register<Exec>("dexJar") {
    val currentProjectName = project.name
    val referenceBuildDir = project(":trackerinterface").buildDir
    val classPathJar = file("$referenceBuildDir/intermediates/full_jar/debug/full.jar")
    dependsOn(":${currentProjectName}:build")
    val dexExec =
        file(android.sdkDirectory.absolutePath + "/build-tools/" + android.buildToolsVersion + "/d8")
    commandLine(
        dexExec,
        "$buildDir/outputs/aar/${currentProjectName}-debug.aar",
        "--classpath",
        classPathJar,
        "--output",
        "AnilistClientPlugin.jar"
    )
}
