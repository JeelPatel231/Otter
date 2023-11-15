package tel.jeelpa.otter.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import tel.jeelpa.otter.databinding.FragmentHomeBinding
import tel.jeelpa.otter.trackerinterface.TrackerManager
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlowFlex
import javax.inject.Inject

@HiltViewModel
class HomeContainerViewModel @Inject constructor(
    private val trackerManager: TrackerManager
) : ViewModel() {

    val loggedIn = flow {
        trackerManager.getCurrentTracker()
            .mapNotNull { it?.userClient?.isLoggedIn() }
            .collect { emitAll(it) }
    }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}

@AndroidEntryPoint
class HomeContainerFragment : Fragment() {
    private var binding: FragmentHomeBinding by autoCleared()
    private val viewModel: HomeContainerViewModel by viewModels()

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

        viewModel.loggedIn.distinctUntilChanged().observeFlowFlex(viewLifecycleOwner) {
            collectLatest {
                when (it) {
                    true -> navigateTo(UserFragment())
                    false -> navigateTo(NoLoginFragment())
                }
            }
        }

        return binding.root
    }
}