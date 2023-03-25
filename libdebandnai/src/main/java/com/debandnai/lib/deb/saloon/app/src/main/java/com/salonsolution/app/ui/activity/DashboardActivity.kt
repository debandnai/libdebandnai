package com.salonsolution.app.ui.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.salonsolution.app.R
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.databinding.ActivityDashboardBinding
import com.salonsolution.app.databinding.NavDrawerHeaderBinding
import com.salonsolution.app.interfaces.PositiveNegativeCallBack
import com.salonsolution.app.ui.base.BaseActivity
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.SettingsUtils
import com.salonsolution.app.utils.UtilsCommon
import com.salonsolution.app.utils.UtilsCommon.userLogout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navViewHeaderBinding: NavDrawerHeaderBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var navController: NavController
    private var pressedTime: Long = 0
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private var notificationType:String? = ""
    private var from:String? = ""

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    @Inject
    lateinit var userSharedPref: UserSharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        //navigation component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        //for top level destination we use appBarConfiguration
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.dashboardFragment,
//                R.id.myProfileFragment
//            ),
//            binding.drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.drawerNavView.setupWithNavController(navController)
        binding.bottomNavigationView.setupWithNavController(navController)
        navViewHeaderBinding = NavDrawerHeaderBinding.bind(binding.drawerNavView.getHeaderView(0))

        binding.viewModel = dashboardViewModel
        binding.lifecycleOwner = this
        navViewHeaderBinding.viewModel = dashboardViewModel
        navViewHeaderBinding.lifecycleOwner = this
        getIntentData(intent)
        initView()
        addBackPressCallBack()
        setObserver()

    }

    private fun setObserver() {
        dashboardViewModel.isMainMenu.observe(this) {
            val menu = binding.drawerNavView.menu
            if (it) {
                menu.setGroupVisible(R.id.groupMain, true)
                menu.setGroupVisible(R.id.groupProfile, false)
            } else {
                menu.setGroupVisible(R.id.groupMain, false)
                menu.setGroupVisible(R.id.groupProfile, true)
            }

        }

        dashboardViewModel.profileDetailsLiveData.observe(this) {

            when (it) {
                is ResponseState.Loading -> {
                    // loadingPopup?.show()
                    //loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    //loadingPopup?.dismiss()
                    it.data?.response?.data?.let { data ->
                        userSharedPref.setProfileDetails(it.data.response?.data)
                        dashboardViewModel.userProfileImg.value = data.image
                        dashboardViewModel.userProfileName.value = data.firstName
                        dashboardViewModel.userFullName.value = getString(R.string.full_name,data.firstName,data.lastName)
                    }
                }
                is ResponseState.Error -> {
                    // Log.d(mTag, it.toString())
                    //loadingPopup?.dismiss()

                }
            }
        }
        dashboardViewModel.notificationCountLiveData.observe(this) {

            when (it) {
                is ResponseState.Loading -> {
                    // loadingPopup?.show()
                    //loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    //loadingPopup?.dismiss()
                    it.data?.response?.data?.let { data ->
                        dashboardViewModel.notificationCount.value =
                            UtilsCommon.getNotificationCount(data.notificationCount)
                    }
                }
                is ResponseState.Error -> {
                    // Log.d(mTag, it.toString())
                    //loadingPopup?.dismiss()

                }
            }
        }

        dashboardViewModel.cartCountLiveData.observe(this) {

            when (it) {
                is ResponseState.Loading -> {
                    // loadingPopup?.show()
                    //loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    //loadingPopup?.dismiss()
                    it.data?.response?.data?.let { data ->
                        dashboardViewModel.cartCount.value = data.cartCount
                    }
                }
                is ResponseState.Error -> {
                    // Log.d(mTag, it.toString())
                    //loadingPopup?.dismiss()

                }
            }
        }

        dashboardViewModel.logoutLiveData.observe(this) {

            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    userLogout()
                }
                is ResponseState.Error -> {
                    Log.d(mTag, it.errorMessage.toString())
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        dashboardViewModel.profileDeleteLiveData.observe(this) {

            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    userLogout()
                }
                is ResponseState.Error -> {
                    Log.d(mTag, it.toString())
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                }
            }
        }

        dashboardViewModel.cartCount.observe(this) {
            setCartCountBadge(it ?: 0)
        }

        dashboardViewModel.isCurrencyChanged.observe(this) {
            if (it)
                updateCurrencyView()
        }

    }

    private fun initView() {
        loadingPopup = LoadingPopup(this)
        errorPopUp = ErrorPopUp(binding.root.context)
        with(binding) {

            //set nav view width
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            drawerNavView.layoutParams.width = ((screenWidth * 0.8).toInt())
            drawerNavView.requestLayout()

            includeToolbar.includeCustomToolbar.ivDrawerIcon.setOnClickListener {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }

            //language
            if (SettingsUtils.getLanguage() == Constants.ENGLISH_CODE) {
                includeToolbar.includeCustomToolbar.ivLanguage.setImageResource(R.drawable.english_flag)
            } else {
                includeToolbar.includeCustomToolbar.ivLanguage.setImageResource(R.drawable.portuguese_flag)
            }

            includeToolbar.includeCustomToolbar.ivLanguage.setOnClickListener {
                navController.navigate(R.id.languageChangeDialogFragment)
            }

            //currency
            updateCurrencyView()

            includeToolbar.includeCustomToolbar.ivCurrency.setOnClickListener {
                navController.navigate(R.id.currencyDialogFragment)
            }

            includeToolbar.includeCustomToolbar.ivBackArrow.setOnClickListener {
                navController.popBackStack()
            }

            includeToolbar.includeCustomToolbar.ivProfile.setOnClickListener {
                navController.navigate(R.id.myProfileFragment)
            }
            includeToolbar.includeCustomToolbar.ivNotification.setOnClickListener {
                navController.navigate(R.id.action_global_notificationListFragment)
            }

            drawerNavView.setNavigationItemSelectedListener { dest ->
                when (dest.itemId) {
                    R.id.menuProfile -> {
                        dashboardViewModel.isMainMenu.value = false
                    }
//                    R.id.menuMyProfile -> {
//                        dashboardViewModel.isMainMenu.value = false
//                        drawerLayout.closeDrawers()
//                        startActivity(Intent(this@DashboardActivity, MyProfileActivity::class.java))
//                    }
//                    R.id.menuChangePassword ->{
//                        dashboardViewModel.isMainMenu.value = false
//                        drawerLayout.closeDrawers()
//                        startActivity(Intent(this@DashboardActivity, ChangePasswordActivity::class.java))
//                    }
                    else -> {
                        dashboardViewModel.isMainMenu.value = true
                        dest.onNavDestinationSelected(navController)
                        drawerLayout.closeDrawers()
                    }
                }
                //NavigationUI.onNavDestinationSelected(dest, navController)
                true
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.myProfileFragment, R.id.changePasswordFragment -> {
                        dashboardViewModel.isMainMenu.value = false
                    }
                    else -> dashboardViewModel.isMainMenu.value = true
                }
            }

            navViewHeaderBinding.ibBack.setOnClickListener {
                dashboardViewModel.isMainMenu.value = true
                navController.currentDestination?.id?.let { id ->
                    binding.drawerNavView.setCheckedItem(id)
                }

            }
            navDrawerFooterLayout.tvSignOut.setOnClickListener {
                drawerLayout.closeDrawers()
                showSignOutPopup()
            }

            navDrawerFooterLayout.tvDeleteProfile.setOnClickListener {
                drawerLayout.closeDrawers()
                showDeleteProfilePopup()
            }
            includeToolbar.appBarLayout

            dashboardViewModel.userFullName.value = getString(R.string.full_name,userSharedPref.getLoginData().customerFName,userSharedPref.getLoginData().customerLName)

            loadData()

            manageIntentData()

        }
    }

    private fun manageIntentData() {
        if(!from.isNullOrEmpty() || !notificationType.isNullOrEmpty())
        {
            navController.navigate(R.id.notificationListFragment)
        }
    }

    private fun showDeleteProfilePopup() {
        CustomPopup.showMessageDialogYesNo(
            mContext = this@DashboardActivity,
            description = getString(R.string.do_you_want_to_delete_your_account_question),
            positiveNegativeCallBack = object :
                PositiveNegativeCallBack {
                override fun onPositiveButtonClick() {
                    dashboardViewModel.profileDelete(requestBodyHelper.getProfileDeleteRequest())
                }

                override fun onNegativeButtonClick() {

                }
            })
    }

    private fun showSignOutPopup() {
        CustomPopup.showMessageDialogYesNo(
            mContext = this@DashboardActivity,
            description = getString(R.string.do_you_want_to_sign_out_question),
            positiveNegativeCallBack = object :
                PositiveNegativeCallBack {
                override fun onPositiveButtonClick() {
                    dashboardViewModel.logout(requestBodyHelper.getLogoutRequest())
                }

                override fun onNegativeButtonClick() {

                }
            })
    }

    fun manageNavigationView(
        isToolbarMenuShow: Boolean,
        isTopLevelDestination: Boolean,
        isShowBottomNav: Boolean,
        isShowToolbar: Boolean,
        isCartPage: Boolean = false
    ) {
        dashboardViewModel.toolbarTitle.value = navController.currentDestination?.label.toString()
        dashboardViewModel.isToolbarMenuShow.value = isToolbarMenuShow
        dashboardViewModel.isTopLevelDestination.value = isTopLevelDestination
        dashboardViewModel.isShowBottomNav.value = isShowBottomNav
        dashboardViewModel.isShowToolbar.value = isShowToolbar
        dashboardViewModel.isCartPage.value = isCartPage

        //drawer layout
        val lockMode =
            if (isTopLevelDestination) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        binding.drawerLayout.setDrawerLockMode(lockMode)

        //appbar layout
        val lp = binding.includeToolbar.appBarLayout.layoutParams
        lp.height = if (isShowToolbar) CoordinatorLayout.LayoutParams.WRAP_CONTENT else 0
        binding.includeToolbar.appBarLayout.layoutParams = lp
        //refresh data
        if (isShowBottomNav)
            loadData()
    }

//    fun changeLocale(langCode: String) {
//        appSettingsPref.setLanguage(langCode)
//       // recreate()
//    }

    private fun updateCurrencyView() {
        if (appSettingsPref.getCurrency() == 1) {
            binding.includeToolbar.includeCustomToolbar.ivCurrency.setImageResource(R.drawable.dollar)
        } else {
            binding.includeToolbar.includeCustomToolbar.ivCurrency.setImageResource(R.drawable.kwanzas)
        }
    }

    fun loadData() {
        dashboardViewModel.getProfileDetails(requestBodyHelper.getProfileDetailsRequest())
        dashboardViewModel.notificationCount(requestBodyHelper.getNotificationCountRequest())
        dashboardViewModel.cartCount(requestBodyHelper.getDashboardAllRequest())

    }

    private fun addBackPressCallBack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("tag", "back button click")
                backButtonClick()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun backButtonClick() {
        when (navController.currentDestination?.id) {
            R.id.homeFragment -> {
                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.please_click_back_again_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                pressedTime = System.currentTimeMillis()
            }
            else -> {
                navController.popBackStack()
            }
        }
    }

    private fun setCartCountBadge(count: Int) {
        binding.bottomNavigationView.getOrCreateBadge(R.id.cartFragment).apply {
            backgroundColor = ContextCompat.getColor(this@DashboardActivity, R.color.brand_color)
            badgeTextColor = getColor(R.color.white)
            maxCharacterCount = 2
            number = count
            isVisible = count > 0
        }
    }

    private fun getIntentData(intent: Intent) {
        Log.d("tag","Order Id: ${intent.getStringExtra(Constants.NOTIFICATION_TYPE)}")
        from = intent.getStringExtra(Constants.FROM)
        notificationType = intent.getStringExtra(Constants.NOTIFICATION_TYPE)

    }

}