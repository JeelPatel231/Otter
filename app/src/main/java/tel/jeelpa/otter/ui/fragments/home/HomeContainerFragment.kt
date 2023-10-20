package tel.jeelpa.otter.ui.fragments.home

import androidx.fragment.app.Fragment
import tel.jeelpa.otter.databinding.FragmentHomeBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment

class HomeContainerFragment :
    ViewBindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private fun navigateTo(fragmentInstance: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.homeFragmentContainerView.id, fragmentInstance)
            .commit()
    }

    override fun onCreateBindingView() {
        val noLoginFragment = NoLoginFragment()
        val userFragment = UserFragment()

//        trackerUseCases.getUserDataUseCase().observeFlow(viewLifecycleOwner) {
//            when (it) {
//                null -> navigateTo(noLoginFragment)
//                else -> navigateTo(userFragment)
//            }
//        }
    }
}