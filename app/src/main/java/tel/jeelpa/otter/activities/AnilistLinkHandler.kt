package tel.jeelpa.otter.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tel.jeelpa.otter.ui.fragments.character.CharacterActivity
import tel.jeelpa.otter.ui.generic.nullOnBlank

class AnilistLinkHandler : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get the intent data
        val data = intent.data ?: return finish()

        val splitString = data.path?.split("/")?.mapNotNull { it.nullOnBlank() }

        // check the path
        val toBeStarted = when (splitString?.first()) {
            "character" -> Intent(this, CharacterActivity::class.java).apply {
                putExtra("characterId", splitString[1].toInt())
            }

            else -> return finish()
        }

        startActivity(toBeStarted)
        return finish()
    }
}