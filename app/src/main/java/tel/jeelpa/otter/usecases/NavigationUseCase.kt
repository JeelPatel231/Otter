package tel.jeelpa.otter.usecases

import android.app.Application
import android.content.Intent
import tel.jeelpa.otter.ui.fragments.animedetails.AnimeActivity
import tel.jeelpa.otter.ui.fragments.mangadetails.MangaActivity
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaCardData

data class GetNavigationUseCase(
    private val application: Application
) {
    operator fun invoke(mediaCardData: MediaCardData) {
        val activity = when (mediaCardData.type) {
            AppMediaType.ANIME -> AnimeActivity::class.java
           AppMediaType.MANGA -> MangaActivity::class.java
            else -> throw IllegalStateException("Unknown Media Type")
        }
        val newIntent = Intent(application, activity)
            .putExtra("data", mediaCardData)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        application.startActivity(newIntent)
    }
}