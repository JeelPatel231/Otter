package tel.jeelpa.otter.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.factories.TrackerClientFactory
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    trackerClientFactory: TrackerClientFactory
): ViewModel() {
    val userData = suspendToFlow { trackerClientFactory().getUser() }
        .cacheInScope(viewModelScope)

    val currentAnime = suspendToFlow { trackerClientFactory().getCurrentAnime() }
        .cacheInScope(viewModelScope)

    val currentManga = suspendToFlow { trackerClientFactory().getCurrentManga() }
        .cacheInScope(viewModelScope)

    val recommendations = suspendToFlow { trackerClientFactory().getRecommendations() }
        .cacheInScope(viewModelScope)
}
