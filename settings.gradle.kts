pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "otter"
include(":app")
include(":plugininterface")
include(":AnilistTrackerPlugin")
include(":MALTrackerPlugin")
include(":aniyomi-runtime")
