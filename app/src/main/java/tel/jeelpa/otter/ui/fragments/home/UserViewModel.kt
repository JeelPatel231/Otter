package tel.jeelpa.otter.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tel.jeelpa.otter.factories.TrackerClientFactory
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.User
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    trackerClientFactory: TrackerClientFactory
): ViewModel() {
    private val _userData: MutableStateFlow<User?> = MutableStateFlow(null)
    val userData get() = _userData.asStateFlow()

    private val _currentAnime: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val currentAnime get() = _currentAnime.asStateFlow()

    private val _currentManga: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val currentManga get() = _currentManga.asStateFlow()

    private val _recommendations: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val recommendations get() = _recommendations.asStateFlow()

    init {
        viewModelScope.launch { _userData.value = trackerClientFactory().getUser() }
        viewModelScope.launch { _currentAnime.value = trackerClientFactory().getCurrentAnime() }
        viewModelScope.launch { _currentManga.value = trackerClientFactory().getCurrentManga() }
        viewModelScope.launch { _recommendations.value = trackerClientFactory().getRecommendations() }
    }
}