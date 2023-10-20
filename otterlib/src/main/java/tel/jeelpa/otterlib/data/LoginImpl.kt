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
        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER).apply {
            data = Uri.parse("https://anilist.co/api/v2/oauth/authorize?client_id=${id}&redirect_uri=${redirectUri}&response_type=code")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}