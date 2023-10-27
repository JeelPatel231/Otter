package tel.jeelpa.otter.ui.fragments.animedetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.ActivityAnimeBinding

@AndroidEntryPoint
class AnimeActivity: AppCompatActivity() {
    private var _binding: ActivityAnimeBinding? = null
    private val binding
            get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAnimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}