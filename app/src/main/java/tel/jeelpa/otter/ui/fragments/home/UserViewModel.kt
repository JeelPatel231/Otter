package tel.jeelpa.otter.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import tel.jeelpa.otter.trackerinterface.TrackerManager
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    trackerManager: TrackerManager
): ViewModel() {
    val trackerClient = suspend {
        trackerManager.getCurrentTracker().first()?.userClient
            ?: throw IllegalStateException("No Tracker Selected/Registered")
    }

    val userData = suspendToFlow { trackerClient().getUser() }
        .cacheInScope(viewModelScope)

    val currentAnime = suspendToFlow { trackerClient().getCurrentAnime() }
        .cacheInScope(viewModelScope)

    val currentManga = suspendToFlow { trackerClient().getCurrentManga() }
        .cacheInScope(viewModelScope)

    val recommendations = suspendToFlow { trackerClient().getRecommendations() }
        .cacheInScope(viewModelScope)
}
