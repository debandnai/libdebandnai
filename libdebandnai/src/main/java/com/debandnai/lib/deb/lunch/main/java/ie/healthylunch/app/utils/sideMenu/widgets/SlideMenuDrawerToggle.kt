package ie.healthylunch.app.utils.sideMenu.widgets

import android.R
import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ie.healthylunch.app.utils.sideMenu.views.SlideMenuDrawerLayout

class SlideMenuDrawerToggle internal constructor(
    activity: Activity, toolbar: Toolbar?,
    slideMenuDrawerLayout: SlideMenuDrawerLayout, slider: Any?,
    @StringRes openDrawerContentDescRes: Int,
    @StringRes closeDrawerContentDescRes: Int
) :
    DrawerLayout.DrawerListener {
    /**
     * Allows an implementing Activity to return an [SlideMenuDrawerToggle.Delegate] to use
     * with SlideMenuDrawerToggle.
     */
    interface DelegateProvider {
        /**
         * @return Delegate to use for ActionBarDrawableToggles, or null if the Activity
         * does not wish to override the default behavior.
         */
        val drawerToggleDelegate: Delegate?
    }

    interface Delegate {
        /**
         * Set the Action Bar's up indicator drawable and content description.
         *
         * @param upDrawable     - Drawable to set as up indicator
         * @param contentDescRes - Content description to set
         */
        fun setActionBarUpIndicator(upDrawable: Drawable?, @StringRes contentDescRes: Int)

        /**
         * Set the Action Bar's up indicator content description.
         *
         * @param contentDescRes - Content description to set
         */
        fun setActionBarDescription(@StringRes contentDescRes: Int)

        /**
         * Returns the drawable to be set as up button when DrawerToggle is disabled
         */
        val themeUpIndicator: Drawable?

        /**
         * Returns the context of ActionBar
         */
        val actionBarThemedContext: Context?

        /**
         * Returns whether navigation icon is visible or not.
         * Used to print warning messages in case developer forgets to set displayHomeAsUp to true
         */
        val isNavigationVisible: Boolean
    }

    private var mActivityImpl: Delegate? = null
    private val mSlideMenuDrawerLayout: SlideMenuDrawerLayout
    private var mSlider: DrawerToggle? = null
    private var mHomeAsUpIndicator: Drawable?
    private var mDrawerIndicatorEnabled = true
    private var mHasCustomUpIndicator = false
    private val mOpenDrawerContentDescRes: Int
    private val mCloseDrawerContentDescRes: Int
    /**
     * Returns the fallback listener for Navigation icon click events.
     *
     * @return The click listener which receives Navigation click events from Toolbar when
     * drawer indicator is disabled.
     * @see .setToolbarNavigationClickListener
     * @see .setDrawerIndicatorEnabled
     * @see .isDrawerIndicatorEnabled
     */
    /**
     * When DrawerToggle is constructed with a Toolbar, it sets the click listener on
     * the Navigation icon. If you want to listen for clicks on the Navigation icon when
     * DrawerToggle is disabled ([.setDrawerIndicatorEnabled], you should call this
     * method with your listener and DrawerToggle will forward click events to that listener
     * when drawer indicator is disabled.
     *
     * @see .setDrawerIndicatorEnabled
     */
    // used in toolbar mode when DrawerToggle is disabled
    var toolbarNavigationClickListener: View.OnClickListener? = null

    // If developer does not set displayHomeAsUp, DrawerToggle won't show up.
    // DrawerToggle logs a warning if this case is detected
    private var mWarnedForDisplayHomeAsUp = false

    /**
     * Construct a new SlideMenuDrawerToggle.
     *
     *
     *
     * The given [Activity] will be linked to the specified [androidx.core.widget.DrawerLayout] and
     * its Actionbar's Up button will be set to a custom drawable.
     *
     * This drawable shows a Hamburger icon when drawer is closed and an arrow when drawer
     * is open. It animates between these two states as the drawer opens.
     *
     *
     *
     * String resources must be provided to describe the open/close drawer actions for
     * accessibility services.
     *
     * @param activity                  The Activity hosting the drawer. Should have an ActionBar.
     * @param slideMenuDrawerLayout           The DrawerLayout to link to the given Activity's ActionBar
     * @param openDrawerContentDescRes  A String resource to describe the "open drawer" action
     * for accessibility
     * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
     * for accessibility
     */
    constructor(
        activity: Activity, slideMenuDrawerLayout: SlideMenuDrawerLayout,
        @StringRes openDrawerContentDescRes: Int,
        @StringRes closeDrawerContentDescRes: Int
    ) : this(
        activity, null, slideMenuDrawerLayout, null, openDrawerContentDescRes,
        closeDrawerContentDescRes
    ) {
    }

    /**
     * Construct a new SlideMenuDrawerToggle with a Toolbar.
     *
     *
     * The given [Activity] will be linked to the specified [androidx.core.widget.DrawerLayout] and
     * the Toolbar's navigation icon will be set to a custom drawable. Using this constructor
     * will set Toolbar's navigation click listener to toggle the drawer when it is clicked.
     *
     *
     * This drawable shows a Hamburger icon when drawer is closed and an arrow when drawer
     * is open. It animates between these two states as the drawer opens.
     *
     *
     * String resources must be provided to describe the open/close drawer actions for
     * accessibility services.
     *
     *
     * setting the Toolbar as the ActionBar of your activity.
     *
     * @param activity                  The Activity hosting the drawer.
     * @param toolbar                   The toolbar to use if you have an independent Toolbar.
     * @param slideMenuDrawerLayout           The DrawerLayout to link to the given Activity's ActionBar
     * @param openDrawerContentDescRes  A String resource to describe the "open drawer" action
     * for accessibility
     * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
     * for accessibility
     */
    constructor(
        activity: Activity, slideMenuDrawerLayout: SlideMenuDrawerLayout,
        toolbar: Toolbar?, @StringRes openDrawerContentDescRes: Int,
        @StringRes closeDrawerContentDescRes: Int
    ) : this(
        activity, toolbar, slideMenuDrawerLayout, null, openDrawerContentDescRes,
        closeDrawerContentDescRes
    ) {
    }

    /**
     * Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
     *
     *
     *
     * This should be called from your `Activity`'s
     * [onPostCreate][Activity.onPostCreate] method to synchronize after
     * the DrawerLayout's instance state has been restored, and any other time when the state
     * may have diverged in such a way that the SlideMenuDrawerToggle was not notified.
     * (For example, if you stop forwarding appropriate drawer events for a period of time.)
     */
    fun syncState() {
        if (mSlideMenuDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mSlider!!.position = 1f
        } else {
            mSlider!!.position = 0f
        }
        if (mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(
                mSlider as Drawable?,
                if (mSlideMenuDrawerLayout.isDrawerOpen(GravityCompat.START)) mCloseDrawerContentDescRes else mOpenDrawerContentDescRes
            )
        }
    }

    /**
     * This method should always be called by your `Activity`'s
     * [ onConfigurationChanged][Activity.onConfigurationChanged]
     * method.
     *
     * @param newConfig The new configuration
     */
    fun onConfigurationChanged(newConfig: Configuration?) {
        // Reload drawables that can change with configuration
        if (!mHasCustomUpIndicator) {
            mHomeAsUpIndicator = themeUpIndicator!!
        }
        syncState()
    }

    /**
     * This method should be called by your `Activity`'s
     * [onOptionsItemSelected][Activity.onOptionsItemSelected] method.
     * If it returns true, your `onOptionsItemSelected` method should return true and
     * skip further processing.
     *
     * @param item the MenuItem instance representing the selected menu item
     * @return true if the event was handled and further processing should not occur
     */
    fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null && item.itemId == R.id.home && mDrawerIndicatorEnabled) {
            toggle()
            return true
        }
        return false
    }

    private fun toggle() {
        if (mSlideMenuDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mSlideMenuDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mSlideMenuDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * Set the up indicator to display when the drawer indicator is not
     * enabled.
     *
     *
     * If you pass `null` to this method, the default drawable from
     * the theme will be used.
     *
     * @param indicator A drawable to use for the up indicator, or null to use
     * the theme's default
     * @see .setDrawerIndicatorEnabled
     */
    fun setHomeAsUpIndicator(indicator: Drawable?) {
        if (indicator == null) {
            mHomeAsUpIndicator = themeUpIndicator!!
            mHasCustomUpIndicator = false
        } else {
            mHomeAsUpIndicator = indicator
            mHasCustomUpIndicator = true
        }
        if (!mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(mHomeAsUpIndicator, 0)
        }
    }

    /**
     * Set the up indicator to display when the drawer indicator is not
     * enabled.
     *
     *
     * If you pass 0 to this method, the default drawable from the theme will
     * be used.
     *
     * @param resId Resource ID of a drawable to use for the up indicator, or 0
     * to use the theme's default
     * @see .setDrawerIndicatorEnabled
     */
    fun setHomeAsUpIndicator(resId: Int) {
        var indicator: Drawable? = null
        if (resId != 0) {
            indicator = mSlideMenuDrawerLayout.getResources().getDrawable(resId)
        }
        setHomeAsUpIndicator(indicator)
    }
    /**
     * @return true if the enhanced drawer indicator is enabled, false otherwise
     * @see .setDrawerIndicatorEnabled
     */
    /**
     * Enable or disable the drawer indicator. The indicator defaults to enabled.
     *
     *
     *
     * When the indicator is disabled, the `ActionBar` will revert to displaying
     * the home-as-up indicator provided by the `Activity`'s theme in the
     * `android.R.attr.homeAsUpIndicator` attribute instead of the animated
     * drawer glyph.
     *
     * @param enable true to enable, false to disable
     */
    var isDrawerIndicatorEnabled: Boolean
        get() = mDrawerIndicatorEnabled
        set(enable) {
            if (enable != mDrawerIndicatorEnabled) {
                if (enable) {
                    setActionBarUpIndicator(
                        mSlider as Drawable?,
                        if (mSlideMenuDrawerLayout.isDrawerOpen(GravityCompat.START)) mCloseDrawerContentDescRes else mOpenDrawerContentDescRes
                    )
                } else {
                    setActionBarUpIndicator(mHomeAsUpIndicator, 0)
                }
                mDrawerIndicatorEnabled = enable
            }
        }


    @JvmName("setDrawerIndicatorEnabled1")
    fun setDrawerIndicatorEnabled(enable: Boolean) {
        if (enable != mDrawerIndicatorEnabled) {
            if (enable) {
                setActionBarUpIndicator(
                    mSlider as Drawable?,
                    if (mSlideMenuDrawerLayout.isDrawerOpen(GravityCompat.START)) mCloseDrawerContentDescRes else mOpenDrawerContentDescRes
                )
            } else {
                setActionBarUpIndicator(mHomeAsUpIndicator, 0)
            }
            mDrawerIndicatorEnabled = enable
        }
    }

    /**
     * [DrawerListener] callback method. If you do not use your
     * SlideMenuDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView  The child view that was moved
     * @param slideOffset The new offset of this drawer within its range, from 0-1
     */
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        mSlider!!.position = Math.min(1f, Math.max(0f, slideOffset))
    }

    /**
     * [DrawerListener] callback method. If you do not use your
     * SlideMenuDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now open
     */
    override fun onDrawerOpened(drawerView: View) {
        mSlider!!.position = 1f
        if (mDrawerIndicatorEnabled) {
            setActionBarDescription(mCloseDrawerContentDescRes)
        }
    }

    /**
     * [DrawerListener] callback method. If you do not use your
     * SlideMenuDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now closed
     */
    override fun onDrawerClosed(drawerView: View) {
        mSlider!!.position = 0f
        if (mDrawerIndicatorEnabled) {
            setActionBarDescription(mOpenDrawerContentDescRes)
        }
    }

    /**
     * [DrawerListener] callback method. If you do not use your
     * SlideMenuDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param newState The new drawer motion state
     */
    override fun onDrawerStateChanged(newState: Int) {}
    fun setActionBarUpIndicator(upDrawable: Drawable?, contentDescRes: Int) {
        if (!mWarnedForDisplayHomeAsUp && !mActivityImpl!!.isNavigationVisible) {
            Log.w(
                "SlideMenuDrawerToggle", "DrawerToggle may not show up because NavigationIcon"
                        + " is not visible. You may need to call "
                        + "actionbar.setDisplayHomeAsUpEnabled(true);"
            )
            mWarnedForDisplayHomeAsUp = true
        }
        mActivityImpl!!.setActionBarUpIndicator(upDrawable, contentDescRes)
    }

    fun setActionBarDescription(contentDescRes: Int) {
        mActivityImpl!!.setActionBarDescription(contentDescRes)
    }

    val themeUpIndicator: Drawable?
        get() = mActivityImpl!!.themeUpIndicator

    internal class DrawerArrowDrawableToggle(
        private val mActivity: Activity,
        themedContext: Context?
    ) :
        DrawerArrowDrawable(themedContext), DrawerToggle {
        override var position: Float
            get() = progress
            set(position) {
                if (position == 1f) {
                    setVerticalMirror(true)
                } else if (position == 0f) {
                    setVerticalMirror(false)
                }
                progress = position
            }

    }

    /**
     * Interface for toggle drawables. Can be public in the future
     */
    internal interface DrawerToggle {
        var position: Float
    }

    /**
     * Delegate if SDK version is between honeycomb and JBMR2
     */
    private class HoneycombDelegate(val mActivity: Activity) :
        Delegate {
        var mSetIndicatorInfo: SlideMenuDrawerToggleHoneycomb.SetIndicatorInfo? = null
        override val themeUpIndicator: Drawable
            get() = SlideMenuDrawerToggleHoneycomb.getThemeUpIndicator(mActivity)!!
        override val actionBarThemedContext: Context?
            get() {
                val actionBar = mActivity.actionBar
                val context: Context
                context = if (actionBar != null) {
                    actionBar.themedContext
                } else {
                    mActivity
                }
                return context
            }
        override val isNavigationVisible: Boolean
            get() {
                val actionBar = mActivity.actionBar
                return (actionBar != null
                        && actionBar.displayOptions and ActionBar.DISPLAY_HOME_AS_UP != 0)
            }

        override fun setActionBarUpIndicator(themeImage: Drawable?, contentDescRes: Int) {
            mActivity.actionBar!!.setDisplayShowHomeEnabled(true)
            mSetIndicatorInfo = SlideMenuDrawerToggleHoneycomb.setActionBarUpIndicator(
                mSetIndicatorInfo, mActivity, themeImage, contentDescRes
            )
            mActivity.actionBar!!.setDisplayShowHomeEnabled(false)
        }

        override fun setActionBarDescription(contentDescRes: Int) {
            mSetIndicatorInfo = SlideMenuDrawerToggleHoneycomb.setActionBarDescription(
                mSetIndicatorInfo, mActivity, contentDescRes
            )
        }
    }

    /**
     * Delegate if SDK version is JB MR2 or newer
     */
    private class JellybeanMr2Delegate(val mActivity: Activity) :
        Delegate {
        override val themeUpIndicator: Drawable
            get() {
                val a = actionBarThemedContext!!.obtainStyledAttributes(
                    null,
                    intArrayOf(R.attr.homeAsUpIndicator),
                    R.attr.actionBarStyle,
                    0
                )
                val result = a.getDrawable(0)
                a.recycle()
                return result!!
            }
        override val actionBarThemedContext: Context?
            get() {
                val actionBar = mActivity.actionBar
                val context: Context
                context = if (actionBar != null) {
                    actionBar.themedContext
                } else {
                    mActivity
                }
                return context
            }
        override val isNavigationVisible: Boolean
            get() {
                val actionBar = mActivity.actionBar
                return actionBar != null &&
                        actionBar.displayOptions and ActionBar.DISPLAY_HOME_AS_UP != 0
            }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        override fun setActionBarUpIndicator(drawable: Drawable?, contentDescRes: Int) {
            val actionBar = mActivity.actionBar
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(drawable)
                actionBar.setHomeActionContentDescription(contentDescRes)
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        override fun setActionBarDescription(contentDescRes: Int) {
            val actionBar = mActivity.actionBar
            actionBar?.setHomeActionContentDescription(contentDescRes)
        }
    }

    /**
     * Used when DrawerToggle is initialized with a Toolbar
     */
    internal class ToolbarCompatDelegate(val mToolbar: Toolbar) :
        Delegate {
        override val themeUpIndicator: Drawable?
        val mDefaultContentDescription: CharSequence?
        override fun setActionBarUpIndicator(
            upDrawable: Drawable?,
            @StringRes contentDescRes: Int
        ) {
            mToolbar.navigationIcon = upDrawable
            setActionBarDescription(contentDescRes)
        }

        override fun setActionBarDescription(@StringRes contentDescRes: Int) {
            if (contentDescRes == 0) {
                mToolbar.navigationContentDescription = mDefaultContentDescription
            } else {
                mToolbar.setNavigationContentDescription(contentDescRes)
            }
        }

        override val actionBarThemedContext: Context?
            get() = mToolbar.context
        override val isNavigationVisible: Boolean
            get() = true

        init {
            themeUpIndicator = mToolbar.navigationIcon
            mDefaultContentDescription = mToolbar.navigationContentDescription
        }
    }

    /**
     * Fallback delegate
     */
    internal class DummyDelegate(val mActivity: Activity) : Delegate {
        override fun setActionBarUpIndicator(
            upDrawable: Drawable?,
            @StringRes contentDescRes: Int
        ) {
        }

        override fun setActionBarDescription(@StringRes contentDescRes: Int) {}
        override val themeUpIndicator: Drawable?
            get() = null
        override val actionBarThemedContext: Context?
            get() = mActivity
        override val isNavigationVisible: Boolean
            get() = true

    }

    init {
        if (toolbar != null) {
            mActivityImpl = ToolbarCompatDelegate(toolbar)
            toolbar.setNavigationOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (mDrawerIndicatorEnabled) {
                        toggle()
                    } else if (toolbarNavigationClickListener != null) {
                        toolbarNavigationClickListener!!.onClick(v)
                    }
                }
            })
        } else if (activity is DelegateProvider) { // Allow the Activity to provide an impl
            mActivityImpl = (activity as DelegateProvider).drawerToggleDelegate
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mActivityImpl = JellybeanMr2Delegate(activity)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mActivityImpl = HoneycombDelegate(activity)
        } else {
            mActivityImpl = DummyDelegate(activity)
        }
        mSlideMenuDrawerLayout = slideMenuDrawerLayout
        mOpenDrawerContentDescRes = openDrawerContentDescRes
        mCloseDrawerContentDescRes = closeDrawerContentDescRes
        mSlider = if (slider == null) {
            DrawerArrowDrawableToggle(
                activity,
                mActivityImpl!!.actionBarThemedContext
            )
        } else ({
            slider
        }) as DrawerToggle?
        mHomeAsUpIndicator = themeUpIndicator
    }
}