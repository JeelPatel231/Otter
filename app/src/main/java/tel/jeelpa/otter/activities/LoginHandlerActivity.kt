package tel.jeelpa.otter.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tel.jeelpa.otter.models.UserClientHolderModel
import tel.jeelpa.otter.ui.generic.showToast

@AndroidEntryPoint
class LoginHandlerActivity : AppCompatActivity() {

    private val viewModel: UserClientHolderModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // handle login
        lifecycleScope.launch(Dispatchers.IO) {
            handleLogin()
            finish()
        }
    }

    private suspend fun handleLogin(){
        val data = intent?.data
            ?: return showToast("Intent Data Null/Invalid!", Toast.LENGTH_SHORT)

        if(data.authority != "logintracker") {
            return showToast("Invalid Link Authority!", Toast.LENGTH_SHORT)
        }

        //"otter://logintracker/anilist/?access_token=...&token_type=...")
        try {
            viewModel.userClient.login(data.toString())
        } catch (e: Throwable){
            return showToast(e.message ?: "Failed to Handle Link, Unknown Cause.", Toast.LENGTH_SHORT)
        }

        showToast("Logged In Successfully!", Toast.LENGTH_SHORT)
    }
}
