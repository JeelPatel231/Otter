package tel.jeelpa.otter.ui.fragments.mangadetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import tel.jeelpa.otter.databinding.SampleTextScreenLayoutBinding
import tel.jeelpa.otter.ui.generic.autoCleared

class MangaDetailsReadFragment : Fragment() {
    private var binding: SampleTextScreenLayoutBinding by autoCleared()
    private val mangaDetailsViewModel: MangaDetailsViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        // Update Height after ViewPager makes fragment transaction
        binding.root.requestLayout()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SampleTextScreenLayoutBinding.inflate(inflater, container, false)

        binding.textView.text = "Manga ${mangaDetailsViewModel.navArgs.id}"

        return binding.root
    }
}