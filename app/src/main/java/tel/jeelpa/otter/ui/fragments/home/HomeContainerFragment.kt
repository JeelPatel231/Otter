package tel.jeelpa.otter.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentHomeBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otterlib.store.UserStore
import javax.inject.Inject

@AndroidEntryPoint
class HomeContainerFragment : Fragment() {
    private var binding: FragmentHomeBinding by autoCleared()
    @Inject lateinit var userStore: UserStore

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

        userStore.getAccessToken.observeFlow(viewLifecycleOwner) {
            when (it) {
                null -> navigateTo(noLoginFragment)
                else -> navigateTo(userFragment)
            }
        }

        return binding.root
    }
}