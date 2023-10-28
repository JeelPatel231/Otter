package tel.jeelpa.otter.ui.generic

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tel.jeelpa.otter.ui.fragments.animedetails.AnimeActivity
import tel.jeelpa.otter.ui.fragments.mangadetails.MangaActivity
import tel.jeelpa.otterlib.models.AppMediaType
import tel.jeelpa.otterlib.models.MediaCardData


//fun FragmentActivity.getOuterNavController() : NavController {
//    val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_activity_container_view) as NavHostFragment
//    return navHostFragment.navController
//}

// only works in single activity app, because the requireActivity() will point to the main activity
//fun Fragment.getOuterNavController() : NavController = requireActivity().getOuterNavController()


fun FragmentManager.getNavControllerFromHost(resId: Int) =
    (findFragmentById(resId) as NavHostFragment).navController

fun FragmentActivity.getNavControllerFromHost(@IdRes resId: Int): NavController =
     supportFragmentManager.getNavControllerFromHost(resId)

fun Fragment.getNavControllerFromHost(@IdRes resId: Int) : NavController =
    childFragmentManager.getNavControllerFromHost(resId)


suspend fun <A, B> Collection<A>.asyncForEach(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

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

// Parcelables Extension
//inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
//    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
//    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
//}
//
//inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
//    SDK_INT >= 33 -> getParcelable(key, T::class.java)
//    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
//}
//
//inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
//    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
//    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
//}
//
//inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
//    SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
//    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
//}


fun Context.navigateToMediaDetails(mediaCardData: MediaCardData){
    val activity = when(mediaCardData.type) {
        AppMediaType.ANIME -> AnimeActivity::class.java
        AppMediaType.MANGA -> MangaActivity::class.java
        else -> throw IllegalStateException("Unknown Media Type")
    }
    val newIntent = Intent(this, activity)
        .putExtra("data", mediaCardData)
    startActivity(newIntent)
}

fun View.visibilityGone() {
    this.visibility = View.GONE
}

fun <X> Flow<X>.observeFlow(lifecycleOwner: LifecycleOwner,callback: (X) -> Unit) =
    lifecycleOwner.lifecycleScope.launch { flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect(callback) }


fun Activity.showToast(text:String, duration: Int = Toast.LENGTH_SHORT){
    runOnUiThread { Toast.makeText(this, text, duration).show() }
}

fun Context.copyToClipboard(label: String,text:String){
    val clipboardManager = getSystemService(ClipboardManager::class.java)
    clipboardManager.setPrimaryClip(ClipData.newPlainText(label,text))
}


// https://stackoverflow.com/a/60356890
//fun Fragment.getNavParentFragment() = requireParentFragment().requireParentFragment()

fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
    requireActivity().showToast(text, duration)

fun Context.showToast(text:String, duration: Int = Toast.LENGTH_SHORT) =
   Toast.makeText(this, text, duration).show()


fun Fragment.goFullScreen() {
    val window = requireActivity().window
    val windowInsetsController = WindowCompat.getInsetsController(window,window.decorView)

    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
}

fun Fragment.hideFullScreen() {
    val window = requireActivity().window
    val windowInsetsController = WindowCompat.getInsetsController(window,window.decorView)

    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

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
