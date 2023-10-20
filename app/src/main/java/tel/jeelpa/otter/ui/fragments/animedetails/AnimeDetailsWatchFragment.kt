package tel.jeelpa.otter.ui.fragments.animedetails

import androidx.fragment.app.viewModels
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsWatchBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.getNavParentFragment


class AnimeDetailsWatchFragment :
    ViewBindingFragment<FragmentAnimeDetailsWatchBinding>(FragmentAnimeDetailsWatchBinding::inflate) {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels({ getNavParentFragment() })

    override fun onCreateBindingView() {
    }
}
