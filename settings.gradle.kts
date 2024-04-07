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
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "otter"
include(":app")
include(":plugininterface")
include(":plugin")
include(":AnilistTrackerPlugin")
include(":MALTrackerPlugin")
include(":aniyomi-runtime")
