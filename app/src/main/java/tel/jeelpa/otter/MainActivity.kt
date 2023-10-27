package tel.jeelpa.otter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.ActivityMainBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.mainFragmentContainerView.id) as NavHostFragment
        binding.mainBottomNav.setupWithNavController(navHostFragment.navController)

//        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
//        windowInsetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

//        // https://developer.android.com/guide/navigation/navigation-ui#argument
//        getOuterNavController().addOnDestinationChangedListener { _, _, arguments ->
//            val fullscreen = arguments?.getBoolean("fullscreen", false) == true
//            val orientation = arguments?.getString("orientation")?.uppercase()
//
//
//            when (fullscreen) {
//                // hide system-bars when fullscreen
//                true -> windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
//                // show system-bars when not fullscreen
//                false -> windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
//            }
//
//            // lock orientation to its value if set, else based on sensor
//            requestedOrientation = when (orientation) {
//                "LANDSCAPE" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
//                "PORTRAIT" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
//                else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
//            }
//        }


        // code start
//        onBackPressedDispatcher.addCallback(this) {
//            // Back is pressed... Finishing the activity
//            if (getOuterNavController().popBackStack().not()) {
//                finish()
//            }
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}