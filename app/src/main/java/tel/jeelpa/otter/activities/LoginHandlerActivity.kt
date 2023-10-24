package tel.jeelpa.otter.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otterlib.repository.TrackerClient
import javax.inject.Inject

@AndroidEntryPoint
class LoginHandlerActivity : AppCompatActivity() {

    @Inject lateinit var userHandler: TrackerClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // handle login
        lifecycleScope.launch(Dispatchers.IO) {
            handleLogin()
            finish()
        }
    }

    private suspend fun handleLogin(){
        val data = intent?.data ?: return showToast("Intent Data Null/Invalid!", Toast.LENGTH_SHORT)

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
