package tel.jeelpa.otter.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.plugininterface.tracker.repository.UserClient
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val userClient: UserClient
): ViewModel() {

    val userData = suspendToFlow { userClient.getUser() }
        .cacheInScope(viewModelScope)

    val currentAnime = suspendToFlow { userClient.getCurrentAnime() }
        .cacheInScope(viewModelScope)

    val currentManga = suspendToFlow { userClient.getCurrentManga() }
        .cacheInScope(viewModelScope)

    val recommendations = suspendToFlow { userClient.getRecommendations() }
        .cacheInScope(viewModelScope)
}
