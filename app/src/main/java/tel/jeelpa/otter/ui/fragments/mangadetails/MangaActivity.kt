package tel.jeelpa.otter.ui.fragments.mangadetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.ActivityMangaBinding

@AndroidEntryPoint
class MangaActivity: AppCompatActivity() {
    private var _binding: ActivityMangaBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMangaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}