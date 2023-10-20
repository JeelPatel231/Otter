package tel.jeelpa.otter.ui.generic

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import tel.jeelpa.otter.R
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


fun FragmentActivity.getOuterNavController() : NavController {
    val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_activity_container_view) as NavHostFragment
    return navHostFragment.navController
}

// only works in single activity app, because the requireActivity() will point to the main activity
fun Fragment.getOuterNavController() : NavController = requireActivity().getOuterNavController()


fun crossfadeViews(contentView: View, loadingView: View) {

    val animationDuration = 500

    contentView.apply {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        alpha = 0f
        visibility = View.VISIBLE

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        animate()
            .alpha(1f)
            .setDuration(animationDuration.toLong())
            .setListener(null)
    }
    // Animate the loading view to 0% opacity. After the animation ends,
    // set its visibility to GONE as an optimization step (it won't
    // participate in layout passes, etc.)
    loadingView.animate()
        .alpha(0f)
        .setDuration(animationDuration.toLong())
        .setListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator) {
                loadingView.visibilityGone()
                if(loadingView is ShimmerFrameLayout){
                    loadingView.stopShimmer()
                    loadingView.hideShimmer()
                }
            }
        })
        .start()

}


fun View.visibilityGone() {
    this.visibility = View.GONE
}

fun <X> Flow<X>.observeFlow(lifecycleOwner: LifecycleOwner,callback: (X) -> Unit) =
    lifecycleOwner.lifecycleScope.launch { flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect(callback) }


fun Activity.showToast(text:String, duration: Int){
    runOnUiThread { Toast.makeText(this, text, duration).show() }
}

fun Context.copyToClipboard(label: String,text:String){
    val clipboardManager = getSystemService(ClipboardManager::class.java)
    clipboardManager.setPrimaryClip(ClipData.newPlainText(label,text))
}


// https://stackoverflow.com/a/60356890
fun Fragment.getNavParentFragment() = requireParentFragment().requireParentFragment()

fun Fragment.showToast(text:String, duration: Int = Toast.LENGTH_SHORT) =
    requireActivity().showToast(text, duration)

fun String.nullOnBlank(): String? {
    if (isBlank()) return null
    return this
}

//fun NavController.navigateWithDefaultAnimation(directions: NavDirections) =
//    navigate(directions, navOptions {
//        anim {
//            enter = R.anim.first_anim
//            exit = R.anim.first_anim
//            popEnter = R.anim.first_anim
//            popExit = R.anim.first_anim
//        }
//    })
