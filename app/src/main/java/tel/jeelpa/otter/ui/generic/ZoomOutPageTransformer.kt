package tel.jeelpa.otter.ui.generic

import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.viewpager2.widget.ViewPager2

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scale = 1.1F
        page.apply {
            translationY = (scale - 1) * page.height / 2
            alpha = 0f
            scaleX = scale
            scaleY = scale
            visibility = View.VISIBLE
        }

        // Start Animation for a short period of time
        page.animate()
            .translationY(0F)
            .scaleX(1F)
            .scaleY(1F)
            .setDuration(page.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .setInterpolator(OvershootInterpolator(1.3F))
            .alpha(1f)
    }
}
