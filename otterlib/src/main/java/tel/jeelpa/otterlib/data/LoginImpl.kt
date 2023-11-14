package tel.jeelpa.otterlib.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import tel.jeelpa.otterlib.repository.LoginProcedure


class LoginImpl (
    private val context: Context,
    private val id: String,
    private val redirectUri: String,
) : LoginProcedure {
    override operator fun invoke() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://anilist.co/api/v2/oauth/authorize?client_id=${id}&redirect_uri=${redirectUri}&response_type=code")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}