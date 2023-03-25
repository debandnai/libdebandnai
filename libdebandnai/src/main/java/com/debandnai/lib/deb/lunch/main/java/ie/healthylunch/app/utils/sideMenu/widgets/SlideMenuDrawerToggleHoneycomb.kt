package ie.healthylunch.app.utils.sideMenu.widgets
import android.app.ActionBar
import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ie.healthylunch.app.R
import java.lang.reflect.Method

internal object SlideMenuDrawerToggleHoneycomb {
    private const val TAG = "DrawerToggleHoneycomb"
    private val THEME_ATTRS = intArrayOf(
        R.attr.homeAsUpIndicator
    )

    fun setActionBarUpIndicator(
        info: SetIndicatorInfo?,
        activity: Activity,
        drawable: Drawable?,
        contentDescRes: Int
    ): SetIndicatorInfo {
        var info = info
        if (true || info == null) {
            info = SetIndicatorInfo(activity)
        }
        if (info.setHomeAsUpIndicator != null) {
            try {
                val actionBar = activity.actionBar
                info.setHomeAsUpIndicator!!.invoke(actionBar, drawable)
                info.setHomeActionContentDescription!!.invoke(actionBar, contentDescRes)
            } catch (e: Exception) {
                Log.w(TAG, "Couldn't set home-as-up indicator via JB-MR2 API", e)
            }
        } else if (info.upIndicatorView != null) {
            info.upIndicatorView!!.setImageDrawable(drawable)
        } else {
            Log.w(TAG, "Couldn't set home-as-up indicator")
        }
        return info
    }

    fun setActionBarDescription(
        info: SetIndicatorInfo?, activity: Activity,
        contentDescRes: Int
    ): SetIndicatorInfo {
        var info = info
        if (info == null) {
            info = SetIndicatorInfo(activity)
        }
        if (info.setHomeAsUpIndicator != null) {
            try {
                val actionBar = activity.actionBar
                info.setHomeActionContentDescription!!.invoke(actionBar, contentDescRes)
                if (Build.VERSION.SDK_INT <= 19) {
                    // For API 19 and earlier, we need to manually force the
                    // action bar to generate a new content description.
                    actionBar!!.subtitle = actionBar.subtitle
                }
            } catch (e: Exception) {
                Log.w(TAG, "Couldn't set content description via JB-MR2 API", e)
            }
        }
        return info
    }

    fun getThemeUpIndicator(activity: Activity): Drawable? {
        val a = activity.obtainStyledAttributes(THEME_ATTRS)
        val result = a.getDrawable(0)
        a.recycle()
        return result
    }

    internal class SetIndicatorInfo(activity: Activity) {
        var setHomeAsUpIndicator: Method? = null
        var setHomeActionContentDescription: Method? = null
        var upIndicatorView: ImageView? = null

        init {
            try {
                setHomeAsUpIndicator = ActionBar::class.java.getDeclaredMethod(
                    "setHomeAsUpIndicator",
                    Drawable::class.java
                )
                setHomeActionContentDescription = ActionBar::class.java.getDeclaredMethod(
                    "setHomeActionContentDescription", Integer.TYPE
                )

                // If we got the method we won't need the stuff below.

            } catch (e: NoSuchMethodException) {
                // Oh well. We'll use the other mechanism below instead.
            }
            val home = activity.findViewById<View>(R.id.home)
            if (home == null) {
                // Action bar doesn't have a known configuration, an OEM messed with things.

            }
            val parent = home!!.parent as ViewGroup
            val childCount = parent.childCount
            /*if (childCount != 2) {

                return
            }*/
            childCount.run { if (childCount != 2){
                // No idea which one will be the right one, an OEM messed with things.
                return@run
            } }
            val first = parent.getChildAt(0)
            val second = parent.getChildAt(1)
            val up = if (first.id == R.id.home) second else first
            if (up is ImageView) {
                // Jackpot! (Probably...)
                upIndicatorView = up
            }
        }
    }
}