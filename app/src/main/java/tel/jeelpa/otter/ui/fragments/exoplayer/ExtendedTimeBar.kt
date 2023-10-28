package tel.jeelpa.otter.ui.fragments.exoplayer

import android.content.Context
import android.util.AttributeSet
import androidx.media3.ui.DefaultTimeBar


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class ExtendedTimeBar(
    context: Context,
    attrs: AttributeSet?
) : DefaultTimeBar(context, attrs) {
    private var enabled = false
    private var forceDisabled = false
    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        super.setEnabled(!forceDisabled && this.enabled)
    }

    fun setForceDisabled(forceDisabled: Boolean) {
        this.forceDisabled = forceDisabled
        isEnabled = enabled
    }
}