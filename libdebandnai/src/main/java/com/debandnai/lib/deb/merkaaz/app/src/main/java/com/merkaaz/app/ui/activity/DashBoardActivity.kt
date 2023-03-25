package com.merkaaz.app.ui.activity

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.data.model.CartCountModel
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.data.model.UserDetails
import com.merkaaz.app.data.viewModel.DashboardViewModel
import com.merkaaz.app.databinding.ActivityDashBoardBinding
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.Constants.CATEGORY_ID_TAG
import com.merkaaz.app.utils.Constants.FROM
import com.merkaaz.app.utils.Constants.MY_ORDER
import com.merkaaz.app.utils.Constants.ORDER_DETAILS
import com.merkaaz.app.utils.Constants.PRODUCT_DETAILS
import com.merkaaz.app.utils.Constants.PRODUCT_DETAILS_VW_MORE
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


var dashBoardActivity: DashBoardActivity? = null

@AndroidEntryPoint
class DashBoardActivity : BaseActivity() {
    lateinit var binding: ActivityDashBoardBinding
    private  var navController: NavController?= null
    private lateinit var dashboardViewModel: DashboardViewModel
    private var loader: Dialog? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board)
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        binding.viewModel = dashboardViewModel
//        binding.inclDashboard.inclToolbarLayout.viewModel = dashboardViewModel
//        binding.inclHeaderLayout.viewModel = dashboardViewModel
        binding.lifecycleOwner = this
        init()
        setData()
        onViewClick()
        observeData()
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        //call user details api and cart count
        dashboardViewModel.getUserProfile()
        Log.d(TAG, "onResume:dash $loginModel.isActive")

    }


    private fun init() {
        dashBoardActivity = this
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))

        check_user_active(loginModel)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        //get screen width
        val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
            if (navController != null) {
                binding.inclDashboard.bottomNavigationView.setupWithNavController(navController!!)
            }


        //set nav view width
        binding.navView.layoutParams.width = ((screenWidth * 0.9).toInt())
        binding.navView.requestLayout()

        if (intent.extras != null && intent.hasExtra(FROM)) {
            if (Objects.equals(intent.getStringExtra(FROM), PRODUCT_DETAILS) || Objects.equals(intent.getStringExtra(FROM), ORDER_DETAILS)) {
                goToCart()
            }else if (Objects.equals(intent.getStringExtra(FROM), MY_ORDER)) {
                goToMyOrder()
            }else if (Objects.equals(intent.getStringExtra(FROM), PRODUCT_DETAILS_VW_MORE)) {
                intent.getStringExtra(Constants.PRODUCTID)?.let {
                    dashboardViewModel.parentCat_ID = it
                    produc_list()
                }
            }
            //navController.navigate(R.id.ordersFragment)
        }
        binding.inclDashboard.bottomNavigationView.setOnItemSelectedListener {
            clearSearchText()
            binding.inclHeaderLayout.rgOptions.clearCheck()
            when (it.itemId) {
                R.id.myCartFragment -> {
                    goToCart()
                    true
                }
                R.id.ordersFragment -> {
                    goToMyOrder()
                    //navController.navigate(R.id.ordersFragment, null)
                    true
                }
                R.id.productsFragment -> {
                    navController?.popBackStack(R.id.productsFragment, true)
                    dashboardViewModel.parentCat_ID=""
                    dashboardViewModel.num_of_tabs = 1
                    produc_list()
                    true
                }
                R.id.homeFragment -> {
                    navController?.navigate(R.id.homeFragment, null)
                    true
                }


                else -> {
                    // binding.inclDashboard.bottomNavigationView.setupWithNavController(navController)
                    false
                }
            }

        }
    }

    private fun setData() {
        //set flag icon dynamically
        if (sharedPreff.getlanguage_code().equals(Constants.ENGLISH_CODE, true))
            binding.inclDashboard.inclToolbarLayout.ivLanguageChange.setImageResource(R.drawable.english_flag)
        else
            binding.inclDashboard.inclToolbarLayout.ivLanguageChange.setImageResource(R.drawable.flag_icon_portuguese)

    }

    private fun onViewClick() {
        binding.inclHeaderLayout.rlNotification.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.inclHeaderLayout.tvShopLocationTxt.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.inclDashboard.inclToolbarLayout.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            setUserProfileData()
        }

        binding.inclDashboard.inclToolbarLayout.rlNotification.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }
        binding.inclDashboard.inclToolbarLayout.rlMyAccount.setOnClickListener {
            navController?.navigate(R.id.myAccountFragment)
        }
        binding.inclDashboard.inclToolbarLayout.ivLanguageChange.setOnClickListener {
            MethodClass.showBottomSheetDialog(this, "home")
        }
        binding.inclDashboard.inclSearchLayout.edtSearch.setOnEditorActionListener(object  :
            TextView.OnEditorActionListener {
            override fun onEditorAction(txt: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (txt?.text.toString().trim().isNotEmpty()) {

                        dashboardViewModel.search_text.value = txt?.text.toString().trim()
                        MethodClass.hideSoftKeyboard(this@DashBoardActivity)

                       // if(navController.currentDestination?.id!=R.id.productsFragment) {
                            navController?.popBackStack(R.id.productsFragment, true)
                            val bundle = Bundle()
                            bundle.putInt(Constants.PRODUCT_CATEGORY, Constants.Search_ID)
                            navController?.navigate(R.id.productsFragment, bundle)
                        //}



                    }else{
                        MethodClass.custom_msg_dialog(this@DashBoardActivity, resources.getString(R.string.show_search))?.show()
                    }
                    return true
                }
                return false
            }

        })

        binding.inclDashboard.inclSearchLayout.edtSearch.addTextChangedListener(object :
            TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                if (s.isNotEmpty()){
                    binding.inclDashboard.inclSearchLayout.imgSearchIcon.setBackgroundResource(R.drawable.ic_baseline_clear_24)
                    binding.inclDashboard.inclSearchLayout.imgSearchIcon.isClickable=true
                }
                else
                {
                    binding.inclDashboard.inclSearchLayout.imgSearchIcon.setBackgroundResource(R.drawable.ic_search)
                    binding.inclDashboard.inclSearchLayout.imgSearchIcon.isClickable=false
                }

            }
        })

        binding.inclDashboard.inclSearchLayout.imgSearchIcon.setOnClickListener{
            clearSearchText()
        }
        /////////////////////////////////////////
        binding.inclHeaderLayout.rbHome.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (navController?.currentDestination?.id != R.id.homeFragment) {
                val bundle = Bundle()
                navController?.navigate(R.id.homeFragment, bundle)
                clearSearchText()
            }
        }

        binding.inclHeaderLayout.rbManageProfitLoss.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ManageProfitLossActivity::class.java))
            clearSearchText()
        }

        binding.inclHeaderLayout.rbShopByCategory.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            navController?.navigate(R.id.shopByCategoryFragment, null)
            deselect_cart_icon_bottom_navigation()
            clearSearchText()
        }

        binding.inclHeaderLayout.rbFeaturedProducts.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            navController?.popBackStack(R.id.productsFragment, true)

            if(navController?.currentDestination?.id==R.id.productsFragment){
                navController?.popBackStack(R.id.productsFragment, true)
            }
            featured_product()
            clearSearchText()
        }

        binding.inclHeaderLayout.rbBestSeller.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if(navController?.currentDestination?.id==R.id.productsFragment){
                navController?.popBackStack(R.id.productsFragment, true)
            }

                val bundle = Bundle()
                bundle.putInt(Constants.PRODUCT_CATEGORY, Constants.BEST_SELLER_ID)
                navController?.navigate(R.id.productsFragment, bundle)

            clearSearchText()
        }

        binding.inclHeaderLayout.rbTermsCondition.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            val bundle = Bundle()
            navController?.navigate(R.id.termsOfUseFragment, bundle)
            clearSearchText()
        }

        binding.inclHeaderLayout.rbPrivacy.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            val bundle = Bundle()
            navController?.navigate(R.id.privacyPolicyFragment, bundle)
            clearSearchText()
        }

        binding.inclHeaderLayout.tvProfileEdit.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, UserUpdateProfileActivity::class.java))
            clearSearchText()
        }

        binding.inclHeaderLayout.tvLocationEdit.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            //get Login Model
            val loginModel: LoginModel = sharedPreff.get_logindata()

            val intent = Intent(this@DashBoardActivity, MapsActivity::class.java)
            intent.putExtra(Constants.ADDRESS1, loginModel.address1)
            intent.putExtra(Constants.ADDRESS2, loginModel.address2)
            intent.putExtra(Constants.REFERENCE_POINT, loginModel.referencePoint)
            startActivity(intent)

        }

        binding.inclDashboard.inclToolbarLayout.imgHelp.setOnClickListener {
            startActivity(Intent(this@DashBoardActivity, CustomerServiceActivity::class.java))
        }

    }

    fun produc_list() {
        dashboardViewModel.num_of_tabs = 1
        val bundle = Bundle()
        bundle.putInt(Constants.PRODUCT_CATEGORY, Constants.PRODUCT_ID)
        bundle.putString(Constants.CATEGORY_ID_TAG, dashboardViewModel.parentCat_ID)
//                    bundle.putString(Constants.CATEGORY_ID_TAG, "0")
        navController?.navigate(R.id.productsFragment, bundle)
    }

    fun featured_product() {
        val bundle = Bundle()
        bundle.putInt(Constants.PRODUCT_CATEGORY, Constants.FEATURED_PRODUCT_ID)
        navController?.navigate(R.id.productsFragment, bundle)
    }

    private fun goToCart() {
        if (loginModel.isActive == 1) {
            navController?.navigate(R.id.myCartFragment, null)
        } else {
            MethodClass.custom_msg_dialog(
                this@DashBoardActivity,
                resources.getString(R.string.you_are_not_an_approved_user)
            )?.show()
        }
    }

    private fun goToMyOrder() {
        if (loginModel.isActive == 1) {
          /*  navController.navigate(R.id.ordersFragment, null)*/
            startActivity(
                Intent(this, MyOrderActivity::class.java)

                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        } else {
            MethodClass.custom_msg_dialog(
                this@DashBoardActivity,
                resources.getString(R.string.you_are_not_an_approved_user)
            )?.show()
        }
    }


    fun setUserProfileData() {
        //set ph number
        dashboardViewModel.mobileNo.value =
            "${Constants.ANGOLA_COUNTRY_CODE} ${sharedPreff.getPhone()}"

        //set address
        val addressList = MethodClass.getAddressList(
            this,
            sharedPreff.get_logindata().address1,
            sharedPreff.get_logindata().address2,
            sharedPreff.get_logindata().referencePoint
        )
        dashboardViewModel.userLocation.value = TextUtils.join(", ", addressList)


        //set user name
        dashboardViewModel.userName.value = sharedPreff.get_logindata().vendorName

        //set user image
        dashboardViewModel.image.value = sharedPreff.get_logindata().profileImage

    }

    private fun enableLayoutBehaviour() {
        val param: CoordinatorLayout.LayoutParams =
            binding.inclDashboard.navHostFragment.layoutParams as CoordinatorLayout.LayoutParams
        param.behavior = AppBarLayout.ScrollingViewBehavior()


    }

    private fun disableLayoutBehaviour() {
        val param: CoordinatorLayout.LayoutParams =
            binding.inclDashboard.navHostFragment.layoutParams as CoordinatorLayout.LayoutParams
        param.behavior = null
    }

    private fun observeData() {
        //For user details
        dashboardViewModel.userDetailsLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
//                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->

//                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val userUpdateData = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<UserDetails>() {}.type
                            val userDetails: UserDetails =
                                gson.fromJson(userUpdateData, dataType)
                            loginModel.phone = userDetails.phone
                            loginModel.vendorName = userDetails.customerName
                            loginModel.shopName = userDetails.shopName
                            loginModel.email = userDetails.email
                            loginModel.taxId = userDetails.taxId
                            loginModel.address1 = userDetails.address1
                            loginModel.address2 = userDetails.address2
                            loginModel.referencePoint = userDetails.referencePoint
                            loginModel.latitude = userDetails.latitude
                            loginModel.longitude = userDetails.longitude
                            loginModel.profileImage = userDetails.profileImage
                            loginModel.isActive = userDetails.isActive
                            sharedPreff.store_logindata(loginModel)
                            setUserProfileData()
                            check_user_active(loginModel)
                        }

                    }

                }
                is Response.Error -> {
//                    loader?.dismiss()
//                    MethodClass.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
            }
        }

        //For cart count
        dashboardViewModel.cartCountLiveData.observe(this) {
            when (it) {
                is Response.Loading -> {
//                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->
//                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val cartCountData = data.response?.data
                            val gson = Gson()
                            val dataType = object : TypeToken<CartCountModel>() {}.type
                            val cartCount: CartCountModel =
                                gson.fromJson(cartCountData, dataType)

                            showHideNotificationBatch(cartCount.prodCount ?: 0)

                        }

                    }

                }
                is Response.Error -> {
//                    loader?.dismiss()
//                    MethodClass.showErrorMsg(binding.root.context, it.errorCode, it.errorMessage)
                }
            }
        }

//        // Header_text
//        dashboardViewModel.header_text.observe(this){
//            println("Header....$it")
//        }

        dashboardViewModel.isShowToolbar.observe(this) {
            if (it) enableLayoutBehaviour()
            else disableLayoutBehaviour()
        }

        dashboardViewModel.isSetCollapsingToolbar.observe(this) { shouldCollapse ->
            if (shouldCollapse) {
                val appBarLayoutLP =
                    binding.inclDashboard.toolbar.layoutParams as AppBarLayout.LayoutParams
                appBarLayoutLP.scrollFlags =
                    AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                binding.inclDashboard.toolbar.layoutParams = appBarLayoutLP
            } else {
                val appBarLayoutLP =
                    binding.inclDashboard.toolbar.layoutParams as AppBarLayout.LayoutParams
                appBarLayoutLP.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
                binding.inclDashboard.toolbar.layoutParams = appBarLayoutLP
            }
        }


    }

    fun check_user_active(loginModel: LoginModel) {
        if (loginModel.isActive?.equals(1) == true) {
            binding.inclDashboard.inclToolbarLayout.ivUnapproved.setBackgroundResource(R.drawable.green_circle_bg)
            binding.inclDashboard.inclToolbarLayout.ivUnapproved.setImageResource(R.drawable.ic_tick)
            dashboardViewModel.pendingapprovalStat.value = true
            dashboardViewModel.pendingapprovalStatForImageProfile.value=true
        }else{
            if (navController!=null) {
                dashboardViewModel.pendingapprovalStat.value =
                    navController?.currentDestination?.id != R.id.homeFragment

            }
            dashboardViewModel.pendingapprovalStatForImageProfile.value=false
            binding.inclDashboard.inclToolbarLayout.ivUnapproved.setBackgroundResource(R.drawable.orange_circle_bg)
            binding.inclDashboard.inclToolbarLayout.ivUnapproved.setImageResource(R.drawable.ic_cross_white)
        }
        dashboardViewModel.isUserApproved.value = loginModel.profileImage?.trim()?.isNotEmpty() == true
        println("login name...${loginModel.vendorName}")
        if (loginModel.vendorName?.isNotEmpty() == true) {
            /*binding.inclDashboard.inclToolbarLayout.tvUserName.text =
                loginModel.vendorName?.get(0).toString().uppercase()*/
            dashboardViewModel.userNameFirstChar.value=loginModel.vendorName?.get(0).toString().uppercase()
        }
        dashboardViewModel.image.value = loginModel.profileImage

    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else if (intent.extras != null && intent.hasExtra(FROM) ) {
            if (intent.getStringExtra(FROM).equals(PRODUCT_DETAILS) && intent.hasExtra(CATEGORY_ID_TAG)){
                startActivity(Intent(this@DashBoardActivity,ProductDetailsActivity::class.java).putExtra(Constants.CATEGORY_ID_TAG,intent.getStringExtra(Constants.CATEGORY_ID_TAG)))
            }
            finish()
        }
        else {
            super.onBackPressed()
            clearSearchText()
        }
        }

    fun showHideNotificationBatch(cartCount: Int) {
        println("cart_count..$cartCount")
        dashboardViewModel.total_cart_count=cartCount
        binding.inclDashboard.bottomNavigationView.getOrCreateBadge(R.id.myCartFragment).apply {
            backgroundColor = getColor(R.color.orange_color)
            badgeTextColor = getColor(R.color.white)
            maxCharacterCount = 3
            number = cartCount
            isVisible = cartCount > 0
        }
//        dashboardViewModel.isShow_bottom_navigation.value = cartCount>0
    }

    fun deselect_cart_icon_bottom_navigation(){
        binding.inclDashboard.bottomNavigationView.menu.getItem(3).isCheckable = false
    }

    private fun clearSearchText(){
        binding.inclDashboard.inclSearchLayout.edtSearch.setText("")
        dashboardViewModel.search_text.value=""
        binding.inclDashboard.inclSearchLayout.imgSearchIcon.setBackgroundResource(R.drawable.ic_search)
        binding.inclDashboard.inclSearchLayout.imgSearchIcon.isClickable=false
        binding.inclDashboard.inclSearchLayout.edtSearch.clearFocus()
        MethodClass.hideSoftKeyboard(this@DashBoardActivity)
    }






    /*private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun onReceive(context: Context, intent: Intent) {
            //call user details api and cart count
            dashboardViewModel.getUserProfile()


        }
    }*/

}

