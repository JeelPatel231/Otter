package tel.jeelpa.otter.ui.fragments

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentMainBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment

@AndroidEntryPoint
class MainFragment: ViewBindingFragment<FragmentMainBinding>(
    FragmentMainBinding::inflate
) {
    override fun onCreateBindingView() {
        val navHostFragment = childFragmentManager.findFragmentById(binding.mainFragmentContainerView.id) as NavHostFragment
        binding.mainBottomNav.setupWithNavController(navHostFragment.navController)
    }
}