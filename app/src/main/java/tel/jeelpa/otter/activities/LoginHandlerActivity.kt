package tel.jeelpa.otter.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tel.jeelpa.otter.trackerinterface.TrackerManager
import tel.jeelpa.otter.ui.generic.showToast
import javax.inject.Inject

@AndroidEntryPoint
class LoginHandlerActivity : AppCompatActivity() {

    @Inject lateinit var trackerFactory: TrackerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // handle login
        lifecycleScope.launch(Dispatchers.IO) {
            handleLogin()
            finish()
        }
    }

    private suspend fun handleLogin(){
        val userHandler = trackerFactory.getCurrentTracker().first()?.userClient
            ?: return showToast("You have no tracker selected/registered")

        val data = intent?.data
            ?: return showToast("Intent Data Null/Invalid!", Toast.LENGTH_SHORT)

        if(data.authority != "logintracker") {
            return showToast("Invalid Link Authority!", Toast.LENGTH_SHORT)
        }

        //"otter://logintracker/anilist/?access_token=...&token_type=...")
        when(data.path){
            "/anilist" -> userHandler.login(data)

            else -> return showToast("Unknown Link, Cannot Handle!", Toast.LENGTH_SHORT)
        }

        showToast("Logged In Successfully!", Toast.LENGTH_SHORT)
    }
}
