package tel.jeelpa.otter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentMainBinding
import tel.jeelpa.otter.ui.generic.autoCleared

@AndroidEntryPoint
class MainFragment: Fragment() {
    private var binding: FragmentMainBinding by autoCleared()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(binding.mainFragmentContainerView.id) as NavHostFragment
        binding.mainBottomNav.setupWithNavController(navHostFragment.navController)

        return binding.root
    }
}