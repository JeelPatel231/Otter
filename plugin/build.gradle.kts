plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "tel.jeelpa.plugin"
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

    implementation(project(mapOf("path" to ":reference")))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}


val JarName = "JeelsWeebPack.jar"
tasks.register<Copy>("unzip") {
    dependsOn("build")
    val inputAar = file("$buildDir/outputs/aar/plugin-debug.aar")
    val output = file("$buildDir/outputs/unzipped/")
    from(zipTree(inputAar))
    into(output)
}

tasks.register<Exec>("dexJar") {
    val output = file("$buildDir/outputs/")
    val referenceBuildDir = project(":reference").buildDir
    val classPathJar = file("$referenceBuildDir/intermediates/full_jar/debug/full.jar")
    dependsOn("unzip")

    val dexExec = file(android.sdkDirectory.absolutePath + "/build-tools/" + android.buildToolsVersion + "/d8")
    commandLine(dexExec,"$buildDir/outputs/unzipped/classes.jar","--classpath",classPathJar,"--output", "$output/plugindex.jar")
}

tasks.register<Exec>("pushPlugin") {
    val packageName = "tel.jeelpa.otter"
    dependsOn("dexJar")
    val pluginDex = file("$buildDir/outputs/plugindex.jar")
    val packagePath = file("/data/data/$packageName/files/plugins/$JarName")
    commandLine(android.adbExecutable, "push", pluginDex, "/data/local/tmp")
    commandLine(android.adbExecutable, "shell","run-as", packageName, "cp", "/data/local/tmp/plugindex.jar", packagePath)
}



//data class Plugin(
//    val name: String,
//    val classes: List<String>,
//    val author: String,
//)
//
//val AuthorName = "JeelPatel231"
//
//tasks.register<Copy>("dumpClasses") {
//    dependsOn("build")
//    val folder = "debug" // "release"
//
//    val outputDir = file("${buildDir.absolutePath}/tmp/kotlin-classes/$folder/")
//    val classesDir = fileTree(outputDir) {
//        include("**/*.class")
//    }.filter {
//        '$' !in it.name
//    }.map {
//        it.relativeTo(outputDir).path.removeSuffix(".class").replace('/','.')
//    }
//
//    val plugin = Plugin(
//        name = JarName,
//        classes = classesDir,
//        author = AuthorName
//    )
//
//    val str = com.google.gson.Gson().toJson(plugin)
//    file("${project.rootDir}/nice.json").writeText(str)
//
//}