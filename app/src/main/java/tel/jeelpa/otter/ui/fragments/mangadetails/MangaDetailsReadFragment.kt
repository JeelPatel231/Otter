package tel.jeelpa.otter.ui.fragments.mangadetails

import androidx.fragment.app.viewModels
import tel.jeelpa.otter.databinding.SampleTextScreenLayoutBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.getNavParentFragment

class MangaDetailsReadFragment : ViewBindingFragment<SampleTextScreenLayoutBinding>(SampleTextScreenLayoutBinding::inflate) {
    private val mangaDetailsViewModel: MangaDetailsViewModel by viewModels({ getNavParentFragment() })

    override fun onCreateBindingView() {
        binding.textView.text = "Manga ${mangaDetailsViewModel.navArgs.media.id}"
    }
}