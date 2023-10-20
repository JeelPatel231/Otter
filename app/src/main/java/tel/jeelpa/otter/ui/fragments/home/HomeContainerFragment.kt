package tel.jeelpa.otter.ui.fragments.home

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentHomeBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otterlib.store.UserStore
import javax.inject.Inject

@AndroidEntryPoint
class HomeContainerFragment :
    ViewBindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject lateinit var userStore: UserStore

    private fun navigateTo(fragmentInstance: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.homeFragmentContainerView.id, fragmentInstance)
            .commit()
    }

    override fun onCreateBindingView() {
        val noLoginFragment = NoLoginFragment()
        val userFragment = UserFragment()

        userStore.getAccessToken.observeFlow(viewLifecycleOwner) {
            when (it) {
                null -> navigateTo(noLoginFragment)
                else -> navigateTo(userFragment)
            }
        }
    }
}