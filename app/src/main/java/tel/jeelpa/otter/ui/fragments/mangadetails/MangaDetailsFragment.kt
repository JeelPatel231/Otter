package tel.jeelpa.otter.ui.fragments.mangadetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.CommonMediaDetailsBinding
import tel.jeelpa.otter.databinding.FragmentMangaDetailsBinding
import tel.jeelpa.otter.ui.generic.ViewPageNavigatorAdapter
import tel.jeelpa.otter.ui.generic.ZoomOutPageTransformer
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.fadeInto
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.setupWithBottomNav

@AndroidEntryPoint
class MangaDetailsFragment : Fragment() {
    private val mangaDetailsViewModel: MangaDetailsViewModel by activityViewModels()
    private var binding: FragmentMangaDetailsBinding by autoCleared()
    private var commonBinding: CommonMediaDetailsBinding by autoCleared()

    private val mangaInfoFragment = MangaDetailsInfoFragment()
    private val mangaReadFragment = MangaDetailsReadFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMangaDetailsBinding.inflate(inflater, container, false)
        commonBinding = CommonMediaDetailsBinding.bind(binding.root)

        val viewPageNavigatorAdapter = ViewPageNavigatorAdapter(
            childFragmentManager,
            lifecycle,
            arrayOf(mangaInfoFragment, mangaReadFragment)
        )

        commonBinding.pagerContainer.apply {
            adapter = viewPageNavigatorAdapter
            isUserInputEnabled = false
            setupWithBottomNav(binding.bottomNavigationBar)
            setPageTransformer(ZoomOutPageTransformer())
        }

        commonBinding.toolbar.title = mangaDetailsViewModel.navArgs.title
        // eagerly load the low dimension cached imaged from
        commonBinding.coverImage.load(mangaDetailsViewModel.navArgs.coverImage){
            scale(Scale.FILL)
        }
        // then load the high quality image when the API request fulfills
        mangaDetailsViewModel.mangaDetails.observeFlow(viewLifecycleOwner){
            commonBinding.coverImage.fadeInto(it.coverImage)
        }

        return binding.root
    }
}
