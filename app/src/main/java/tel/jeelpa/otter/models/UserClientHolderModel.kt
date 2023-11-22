package tel.jeelpa.otter.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import tel.jeelpa.plugininterface.tracker.repository.UserClient
import javax.inject.Inject

@HiltViewModel
class UserClientHolderModel @Inject constructor(
    val userClient: UserClient
): ViewModel() {
    val loggedIn = flow {
        emitAll(userClient.isLoggedIn())
    }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}
