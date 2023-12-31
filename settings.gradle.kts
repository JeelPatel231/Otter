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
    }
}

rootProject.name = "otter"
include(":app")
include(":plugininterface")
include(":plugin")
include(":AnilistTrackerPlugin")
include(":MALTrackerPlugin")
