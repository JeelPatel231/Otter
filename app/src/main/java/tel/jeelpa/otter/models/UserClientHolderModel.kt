package tel.jeelpa.otter.models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.trackerinterface.repository.UserClient
import javax.inject.Inject

@HiltViewModel
class UserClientHolderModel @Inject constructor(
    val userClient: UserClient
): ViewModel()
