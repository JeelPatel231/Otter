package tel.jeelpa.otter

import android.app.Application
import android.content.Intent
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OtterApplication: Application() {

    override fun onCreate() {
        super.onCreate()

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

}