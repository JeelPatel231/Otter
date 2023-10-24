package tel.jeelpa.otter

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.ActivityMainBinding
import tel.jeelpa.otter.ui.generic.getOuterNavController

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // https://developer.android.com/guide/navigation/navigation-ui#argument
        getOuterNavController().addOnDestinationChangedListener { _, _, arguments ->
            val fullscreen = arguments?.getBoolean("fullscreen", false) == true
            val orientation = arguments?.getString("orientation")?.uppercase()


            when (fullscreen) {
                // hide system-bars when fullscreen
                true -> windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                // show system-bars when not fullscreen
                false -> windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }

            // lock orientation to its value if set, else based on sensor
            requestedOrientation = when (orientation) {
                "LANDSCAPE" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                "PORTRAIT" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }

        // code start
        onBackPressedDispatcher.addCallback(this) {
            // Back is pressed... Finishing the activity
            if (getOuterNavController().popBackStack().not()) {
                finish()
            }
        }

    }

}