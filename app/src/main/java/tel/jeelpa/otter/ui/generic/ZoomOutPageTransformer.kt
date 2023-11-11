package tel.jeelpa.otter.ui.generic

import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.viewpager2.widget.ViewPager2

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            alpha = 0f
            scaleX = 1.1F
            scaleY = 1.1F
            visibility = View.VISIBLE
        }

        // Start Animation for a short period of time
        page.animate()
            .scaleX(1F)
            .scaleY(1F)
            .setDuration(page.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .setInterpolator(OvershootInterpolator(1.5F))
            .alpha(1f)
    }
}
