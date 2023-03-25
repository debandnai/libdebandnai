package ie.healthylunch.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.MenuAdapter
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.DashBoardRepository
import ie.healthylunch.app.data.viewModel.DashBoardViewModel
import ie.healthylunch.app.databinding.ActivityDashboardBinding
import ie.healthylunch.app.databinding.ExitAppDialogBinding
import ie.healthylunch.app.fragment.DashBoardFragment
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_DISABLE_TAG
import ie.healthylunch.app.utils.Constants.Companion.CALENDAR_END_DATE_TAG
import ie.healthylunch.app.utils.Constants.Companion.CURRENT_DAY
import ie.healthylunch.app.utils.Constants.Companion.CURRENT_ORDER_DAY
import ie.healthylunch.app.utils.Constants.Companion.DASHBOARD_ID
import ie.healthylunch.app.utils.Constants.Companion.FROM
import ie.healthylunch.app.utils.Constants.Companion.SIDE_DRAWER
import ie.healthylunch.app.utils.Constants.Companion.STATUS_EIGHT
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FIVE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_FOUR
import ie.healthylunch.app.utils.Constants.Companion.STATUS_NINE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_SEVEN
import ie.healthylunch.app.utils.Constants.Companion.STATUS_SIX
import ie.healthylunch.app.utils.Constants.Companion.STATUS_THREE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.sideMenu.views.SlideMenuDrawerLayout
import ie.healthylunch.app.utils.sideMenu.views.SlideMenuMenuView
import ie.healthylunch.app.utils.sideMenu.views.SlideMenuModelClass
import ie.healthylunch.app.utils.sideMenu.widgets.SlideMenuDrawerToggle
import java.util.*

var dashBoardFragment: DashBoardFragment? = null

class DashBoardActivity : BaseActivity<DashBoardViewModel, DashBoardRepository>(),
    SlideMenuMenuView.OnMenuClickListener, DialogYesNoListener {
    lateinit var activityDashboardBinding: ActivityDashboardBinding
    var isSwipeRefreshLoader: Boolean = false
    var currentOrderDate = STATUS_ONE.toString()

    //Drawer
    private lateinit var rlWallet: LinearLayout
    private lateinit var notificationLayout: RelativeLayout
    private lateinit var rlHelp: RelativeLayout
    private lateinit var mMenuAdapter: MenuAdapter
    private lateinit var mViewHolder: ViewHolder
    private lateinit var rowItems: ArrayList<SlideMenuModelClass>


    private val firstViewMenuNameList = arrayOf(
        "Guest", "Dashboard", "Menu",
        "Wallet", "Calendar", "Students", "Feedback", "About", "Help", "Logout"
    )

    fun updateWalletBalance(walletBalance: String) {
        walletBalanceTv?.text = walletBalance
    }

    fun updateNotificationCount(notificationCount: Int) {
        if (notificationCount == 0)
            notificationCountTv?.visibility = View.INVISIBLE
        else {
            notificationCountTv?.text =
                notificationCount.toString()
            notificationCountTv?.visibility = View.VISIBLE
        }
    }

    fun notificationCountVisibility(visibility: Int) {
        notificationCountTv?.visibility = visibility
    }

    private val firstViewMenuImageList = arrayOf(
        R.drawable.profile_new,
        R.drawable.dashboard_new,
        R.drawable.menu_new,
        R.drawable.wallet_new,
        R.drawable.calendar_new,
        R.drawable.students_new,
        R.drawable.feedback_new,
        R.drawable.about_new,
        R.drawable.ic_help_red,
        R.drawable.logout_new
    )

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var dashBoardToolbar: Toolbar

        @SuppressLint("StaticFieldLeak")
        lateinit var dashBoardNotificationLayout: RelativeLayout

        @SuppressLint("StaticFieldLeak")
        lateinit var dashBoardHelpLayout: RelativeLayout

        @SuppressLint("StaticFieldLeak")
        lateinit var dashBoardWalletLayout: LinearLayout

    }

    private val secondViewMenuNameList = arrayOf("", "My Profile", "Change Password")
    private val secondViewMenuImageList =
        arrayOf(R.drawable.memu_back_arrow, R.drawable.profile_new, R.drawable.change_password_new)
    var drawerFirstView = true

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var walletBalanceTv: TextView? = null
    private var notificationCountTv: TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_dashboard)
        setUpViewModel()
        window.statusBarColor = ContextCompat.getColor(this@DashBoardActivity, R.color.dashboard_background_color)

        activityDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)

        getIntentValue()

        //Initialize all the xml views
        xmlInit()

        //click action of all  views
        viewOnclick()

    }


    private fun getIntentValue() {
        //get data from intent
        if (intent.extras != null) {
            if (!intent.getStringExtra(CALENDAR_DISABLE_TAG).isNullOrBlank())
                viewModel.calendarDisableTime.value = intent.getStringExtra(CALENDAR_DISABLE_TAG)
        }
    }



    private fun xmlInit() {
        //after coming this page means the user is logged in
        UserPreferences.saveAsObject(this, IsLogin(1), Constants.LOGIN_CHECK)

        // Initialize viewHolder
        mViewHolder = ViewHolder(this)

        walletBalanceTv = findViewById(R.id.balance)
        notificationCountTv = findViewById(R.id.notificationCountTv)

        notificationLayout = findViewById(R.id.notificationLayout)
        rlHelp = findViewById(R.id.rl_help)
        rlWallet = findViewById(R.id.rl_wallet)

        dashBoardToolbar = mViewHolder.toolbar
        dashBoardNotificationLayout = notificationLayout
        dashBoardHelpLayout = rlHelp
        dashBoardWalletLayout = rlWallet

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Handle toolbar actions
        handleToolbar()

        mMenuAdapter = MenuAdapter(this, ArrayList())

        mViewHolder.slideMenuMenuView.setOnMenuClickListener(this)
        mViewHolder.slideMenuMenuView.adapter = mMenuAdapter

        mViewHolder.slideMenuMenuView.setFooterView(R.layout.footer_layout)

        // Handle menu actions
        handleMenu()

        // Handle drawer actions
        handleDrawer()


        val swipeRefreshColor = ContextCompat.getColor(this, R.color.selected_date_color)
        activityDashboardBinding.swipeRefreshLayout.setColorSchemeColors(swipeRefreshColor)
        activityDashboardBinding.swipeRefreshLayout.setOnRefreshListener {
            isSwipeRefreshLoader = true
            CURRENT_ORDER_DAY = currentOrderDate
            dashBoardFragment?.getStudentList()
        }

        //add call back for back pressed
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    private fun viewOnclick() {
        notificationLayout.setOnClickListener {
            val intent = Intent(Intent(this, NotificationActivity::class.java))
            startActivity(intent)
        }

        rlHelp.setOnClickListener {
            val intent = Intent(Intent(this, HelpActivity::class.java))
            startActivity(intent)
        }
        rlWallet.setOnClickListener {
            DASHBOARD_ID = STATUS_ZERO
            val intent = Intent(Intent(this, WalletViewActivity::class.java))
            startActivity(intent)
        }

        mViewHolder.headMenu.setOnClickListener {
            try {

                drawerFirstView = true

                mViewHolder.slideMenuDrawerLayout.openDrawer()

                setFirstViewOfNavigationDrawer()
            } catch (e: Exception) {
                Log.e("error", e.message.toString())
            }

        }

        //setDrawerListener is used here for sliding the side drawer without clicking side drawer menu icon
        //after opening side drawer the profile name will be updated
        mViewHolder.slideMenuDrawerLayout.setDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                enableDisableSwiperefresh(false)
                drawerFirstView = true
                setFirstViewOfNavigationDrawer()
            }

            override fun onDrawerClosed(drawerView: View) {
                if (navController.currentDestination?.id == R.id.dashBoardFragment) {
                    enableDisableSwiperefresh(true)
                }
            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })
    }


    //Side Drawer
    private class ViewHolder(activity: Activity) {
        var slideMenuDrawerLayout: SlideMenuDrawerLayout = activity.findViewById(R.id.drawer)
        var slideMenuMenuView: SlideMenuMenuView =
            slideMenuDrawerLayout.menuView as SlideMenuMenuView
        var toolbar: Toolbar = activity.findViewById(R.id.toolbar)
        var headMenu: ImageView = activity.findViewById(R.id.headMenu)

    }

    private fun handleMenu() {
        rowItems = ArrayList()
        for (i in firstViewMenuNameList.indices) {
            val item = SlideMenuModelClass(
                firstViewMenuImageList[i],
                firstViewMenuNameList[i]
            )
            rowItems.add(item)
        }

        mMenuAdapter.options = rowItems
        mMenuAdapter.notifyDataSetChanged()

        Log.e("handleMenu: ", mMenuAdapter.options.size.toString())
    }

    private fun handleToolbar() {
        setSupportActionBar(mViewHolder.toolbar)
    }

    private fun handleDrawer() {
        val slideMenuDrawerToggle = SlideMenuDrawerToggle(
            this,
            mViewHolder.slideMenuDrawerLayout,
            mViewHolder.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        mViewHolder.slideMenuDrawerLayout.setDrawerListener(slideMenuDrawerToggle)
        slideMenuDrawerToggle.setDrawerIndicatorEnabled(false)
        mViewHolder.slideMenuDrawerLayout.closeDrawer()
        slideMenuDrawerToggle.syncState()

    }


    override fun onFooterClicked() {
        CustomDialog.showYesNoTypeDialog(
            this,
            getString(R.string.do_you_want_to_delete_your_account_),
            object : DialogYesNoListener {

                override fun yesOnClick(dialog: Dialog, activity: Activity) {
                    dialog.dismiss()
                    AppController.getInstance().userDeleteAccountApi(this@DashBoardActivity)
                }

                override fun noOnClick(dialog: Dialog, activity: Activity) {
                    dialog.dismiss()
                }

            })

    }

    override fun onHeaderClicked() {
    }

    override fun onOptionClicked(position: Int, objectClicked: Any?) {
        mMenuAdapter.setViewSelected(position, true)
        when (position) {
            STATUS_ZERO -> {
                if (drawerFirstView) {
                    setSecondViewOfNavigationDrawer()
                } else {
                    drawerFirstView = true
                    setFirstViewOfNavigationDrawer()
                }


            }
            STATUS_ONE -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                if (!drawerFirstView)
                    startActivity(Intent(this, ParentProfileViewActivity::class.java))
                else {

                    navController.navigate(R.id.dashBoardFragment)
                }

            }
            STATUS_TWO -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                if (!drawerFirstView)
                    startActivity(Intent(this, ChangePasswordActivity::class.java))
                else {

                    Constants.ORDER_DATE = ""
                    CURRENT_DAY = STATUS_ONE
                    val intent = Intent(this, MenuTemplateActivity::class.java)
                    intent.putExtra(FROM, SIDE_DRAWER)
                    startActivity(intent)
                }
            }
            STATUS_THREE -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                if (drawerFirstView) {
                    DASHBOARD_ID = STATUS_ZERO
                    startActivity(Intent(this, WalletViewActivity::class.java))

                }
            }

            STATUS_FOUR -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                if (drawerFirstView) {
                    var intent = Intent(this@DashBoardActivity, CalendarActivity::class.java)
                    intent.putExtra(CALENDAR_DISABLE_TAG, viewModel.calendarDisableTime.value)
                    startActivity(intent)
                }
            }
            STATUS_FIVE -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                navController.navigate(R.id.viewaddedstudentprofilelistFragment)

            }
            STATUS_SIX -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                startActivity(Intent(this, FeedbackActivity::class.java))
            }
            STATUS_SEVEN -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                navController.navigate(R.id.aboutUsFragment)
            }
            STATUS_EIGHT -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                startActivity(Intent(this, HelpActivity::class.java))
            }
            STATUS_NINE -> {
                mViewHolder.slideMenuDrawerLayout.closeDrawer()
                if (drawerFirstView) {
                    CustomDialog.showYesNoTypeDialog(
                        this,
                        this.resources.getString(R.string.do_you_want_to_log_out_),
                        this
                    )
                }
            }
        }

        if (navController.currentDestination?.id != R.id.dashBoardFragment) {
            enableDisableSwiperefresh(false)
        }
    }


    override fun onLoginButtonClicked(position: Int, objectClicked: Any?) {
    }

    private fun setFirstViewOfNavigationDrawer() {
        rowItems = ArrayList()
        for (i in firstViewMenuNameList.indices) {
            val item = SlideMenuModelClass(
                firstViewMenuImageList[i],
                firstViewMenuNameList[i]
            )
            rowItems.add(item)
        }
        mViewHolder = ViewHolder(this)

        mViewHolder.slideMenuMenuView.setFooterView(SlideMenuMenuView.DEFAULT_LAYOUT_ATTRIBUTE_VALUE)

        //get login response object from shared preference
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )
        rowItems[0].menuName = loginResponse?.response?.raws?.data?.displayName.toString()
        mViewHolder.slideMenuMenuView.setOnMenuClickListener(this@DashBoardActivity)
        mMenuAdapter.options = rowItems
        mMenuAdapter.notifyDataSetChanged()
    }

    private fun setSecondViewOfNavigationDrawer() {
        drawerFirstView = false

        rowItems = ArrayList()
        for (i in secondViewMenuNameList.indices) {
            val item = SlideMenuModelClass(
                secondViewMenuImageList[i],
                secondViewMenuNameList[i]
            )
            rowItems.add(item)
        }
        mViewHolder = ViewHolder(this@DashBoardActivity)
        mViewHolder.slideMenuMenuView.setFooterView(R.layout.footer_layout)
        rowItems[0].menuName = " "

        mViewHolder.slideMenuMenuView.setOnMenuClickListener(this@DashBoardActivity)
        mMenuAdapter.options = rowItems
        mMenuAdapter.notifyDataSetChanged()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!mViewHolder.slideMenuDrawerLayout.isDrawerOpen && navController.currentDestination?.id == R.id.dashBoardFragment) {
//            CustomDialog.showYesNoTypeDialog(
//                this,
//                this.resources.getString(R.string.do_you_want_to_exit_this_page_),
//                object : DialogYesNoListener {
//                    override fun yesOnClick(dialog: Dialog, activity: Activity) {
//                        dialog.dismiss()
//                        finishAffinity()
//                    }
//
//                    override fun noOnClick(dialog: Dialog, activity: Activity) {
//                        dialog.dismiss()
//                    }
//
//                })
                CustomDialog.exitAppDialog(this@DashBoardActivity,
                    object : DialogYesNoListener {
                        override fun yesOnClick(dialog: Dialog, activity: Activity) {
                            dialog.dismiss()
                            finishAffinity()
                        }

                        override fun noOnClick(dialog: Dialog, activity: Activity) {
                            dialog.dismiss()
                        }

                    })
            }
        }
    }


    override fun yesOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
        AppController.getInstance().logoutAppController(this, true)
    }

    override fun noOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
    }

    override fun getViewModel() = DashBoardViewModel::class.java
    override fun getRepository() =
        DashBoardRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter("Notification"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (navController.currentDestination?.id == R.id.dashBoardFragment) {
                dashBoardFragment?.notificationListApiCallWithoutLoader()
            }
        }
    }

    fun enableDisableSwiperefresh(state: Boolean) {
        activityDashboardBinding.swipeRefreshLayout.isEnabled = state
    }

    fun setEndDateForPullToRefresh(currentDate: String?) {
        currentDate?.let { currentdate -> currentOrderDate = currentdate }
    }
}
