package tel.jeelpa.otter

import android.app.Application
import android.content.Intent
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import tel.jeelpa.otter.plugins.PluginInitializer
import javax.inject.Inject

@HiltAndroidApp
class OtterApplication: Application() {

    @Inject lateinit var pluginInitializer: PluginInitializer

    override fun onCreate() {
        super.onCreate()

        // load all plugins before starting main activity
        loadPlugins()

        // Apply dynamic color
        DynamicColors.applyToActivitiesIfAvailable(this)

        // add uncaught exceptions custom handler
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            handleUncaughtExceptions(thread, exception)
        }

    }

    private fun handleUncaughtExceptions(thread: Thread, e: Throwable){
        val errorStackTrace = e.stackTraceToString()

        val intent = Intent().apply {
            action = "tel.jeelpa.otter.SEND_LOG"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Intent.EXTRA_TEXT, errorStackTrace)
        }
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }

    private fun loadPlugins(){
        // find the directory and create if they dont exist,
        // if its null, throw exception
        val pluginPath = getExternalFilesDir("plugins")
//        val pluginPath = File(filesDir,"plugins")
            ?.apply { if(!exists()) mkdirs() }
            ?: throw IllegalStateException()

        // list all files with ".jar" extension and load them as plugins
        pluginPath.listFiles()
            ?.filter { it.extension == "jar" }
            ?.forEach { pluginInitializer(it.absolutePath) }

    }

}