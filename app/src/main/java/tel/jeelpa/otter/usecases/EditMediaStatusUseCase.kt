package tel.jeelpa.otter.usecases

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tel.jeelpa.otter.ui.fragments.mediaCommon.MediaEditorBottomSheetFactory
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.repository.UserClient

interface EditMediaStatusCallback {
    operator fun invoke(mediaCardData: MediaCardData): Boolean
}

data class EditMediaStatusUseCase(
    private val userClient: UserClient,
    private val getBottomSheet: MediaEditorBottomSheetFactory
) {
    fun <T> forFragment(frag: T) : EditMediaStatusCallback where T: FragmentActivity, T: LifecycleOwner {
        return object : EditMediaStatusCallback {
            override operator fun invoke(mediaCardData: MediaCardData): Boolean {
                frag.lifecycleScope.launch {
                    val isLoggedIn = userClient.isLoggedIn().first()
                    if(!isLoggedIn) {
                        frag.showToast("Login to update progress!")
                    } else {
                        val bottomSheet = getBottomSheet(mediaCardData)
                        bottomSheet.show(frag.supportFragmentManager, null)
                    }
                }
                return true
            }
        }
//        return {
//            frag.lifecycleScope.launch {
//                if (!userClient.isLoggedIn().first())
//                    return@launch frag.showToast("User Not Logged In!")
//
//            }
//            true
//        }
    }
}