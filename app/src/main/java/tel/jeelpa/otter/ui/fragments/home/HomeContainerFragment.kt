package tel.jeelpa.otter.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.FragmentHomeBinding
import tel.jeelpa.otter.factories.TrackerClientFactory
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlow
import javax.inject.Inject

@AndroidEntryPoint
class HomeContainerFragment : Fragment() {
    private var binding: FragmentHomeBinding by autoCleared()
    @Inject lateinit var trackerClientFactory: TrackerClientFactory

    private fun navigateTo(fragmentInstance: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.homeFragmentContainerView.id, fragmentInstance)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val noLoginFragment = NoLoginFragment()
        val userFragment = UserFragment()

        lifecycleScope.launch {
            trackerClientFactory().isLoggedIn().observeFlow(viewLifecycleOwner){
                when(it) {
                    true -> navigateTo(userFragment)
                    false -> navigateTo(noLoginFragment)
                }
            }
        }

        return binding.root
    }
}