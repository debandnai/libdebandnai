package ie.healthylunch.app.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.MenuTempListAdapter
import ie.healthylunch.app.adapter.StudentListPagerAdapter
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.menuTemplate.MenuTemplateListItem
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.MenuTemplateRepository
import ie.healthylunch.app.data.viewModel.DashBoardViewModel
import ie.healthylunch.app.data.viewModel.MenuTemplateViewModel
import ie.healthylunch.app.databinding.ActivityMenuTemplateBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.CURRENT_DAY
import ie.healthylunch.app.utils.Constants.Companion.DAY_NAME
import ie.healthylunch.app.utils.Constants.Companion.FROM
import ie.healthylunch.app.utils.Constants.Companion.SIDE_DRAWER
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.MethodClass.getDayNameOfIntegerDayValue
import java.util.Objects

class MenuTemplateActivity :
    BaseActivity<MenuTemplateViewModel, MenuTemplateRepository>(),
    AdapterItemOnclickListener {
    private var menuTempListAdapter: MenuTempListAdapter? = null
    lateinit var binding: ActivityMenuTemplateBinding
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    lateinit var loader : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_menu_template)

        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.userType.value = Constants.USER_TYPE

        viewModel.rlMainLayout = binding.rlMainLayout
        if (intent.extras !== null) {
            viewModel.from = intent.getStringExtra(FROM).toString()
        }
        init()
        onViewClick()
    }


    override fun getViewModel() = MenuTemplateViewModel::class.java
    override fun getRepository() =
        MenuTemplateRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    fun init() {
        loader = MethodClass.loaderDialog(binding.root.context)
        //register OnPageChangeCallback On StudentViewPager
        registerOnPageChangeCallbackOnStudentViewPager()

        //Student List Response()
        getStudentListResponse()

        //MenuTemplateResponse
        getMenuTemplateResponse()

        ///resultLauncher
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.let {
                        getStudentList()
                    }
                }

            }


        //call student list api
        getStudentList()
    }

    private fun onViewClick() {
        binding.nextBtn.setOnClickListener {
            goToNextPage(viewModel.selectedTempId)
        }

        binding.llFav.setOnClickListener{
            val intent=Intent(this@MenuTemplateActivity,ProductActivity::class.java)
            intent.putExtra(Constants.FROM_TAG, Constants.FAVORITES_ACTIVITY)
            startActivity(intent)
        }
    }


    private fun getStudentList() {
        loader.show()
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)
        viewModel.studentList(jsonObject, Constants.TOKEN)
    }


    fun getMenuTemplate() {

        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(this)
        jsonObject.addProperty("user_type", viewModel.userType.value)
        jsonObject.addProperty("student_id", Constants.SELECTED_STUDENT_ID)
        jsonObject.addProperty("order_date", Constants.ORDER_DATE)
        if(Objects.equals(viewModel.from,SIDE_DRAWER)){
            jsonObject.addProperty("week_day", STATUS_ONE)
        }
        else{
            jsonObject.addProperty("week_day", getDayNameOfIntegerDayValue(CURRENT_DAY))
        }
        CURRENT_DAY
        viewModel.menuTemplateList(jsonObject, Constants.TOKEN)
        Log.d("getMenuTemplateTest", "" + jsonObject)
    }



    fun getStudentListResponse() {
        if(!loader.isShowing)
            loader.show()
        binding.studentViewPager.offscreenPageLimit = 4
        viewModel.studentListResponse.observe(this) {

            when (it) {
                is Resource.Success -> {
//                    MethodClass.hideProgressDialog(this)
                    if (it.value.response.raws.data.isNotEmpty()) {
                        viewModel.studentList = it.value.response.raws.data

                        viewModel.studentName.value =
                            viewModel.studentList[Constants.SELECTED_STUDENT_POSITION].fName

                        val studentPagerAdapter =
                            StudentListPagerAdapter(this, viewModel.studentList)
                        binding.studentViewPager.adapter = studentPagerAdapter
                        binding.studentViewPager.isFocusable = false

                        binding.studentViewPager.currentItem =
                            Constants.SELECTED_STUDENT_POSITION

                    } else {
                        viewModel.studentList = ArrayList()
                        AppController.getInstance().isDeActivatedAllStudents = true
                        AppController.getInstance().logoutAppController(this, true)
                    }



                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.STUDENT_LIST,
                                        Constants.REFRESH_TOKEN
                                    )
                            } else {
                                it.errorString.let { it1 ->
                                    MethodClass.showErrorDialog(
                                        this,
                                        it1,
                                        it.errorCode
                                    )

                                }
                            }


                        }


                    }
//                    MethodClass.hideProgressDialog(this)
                    loader.dismiss()
                }

            }


        }
    }


    fun getMenuTemplateResponse() {
        if(!loader.isShowing)
            loader.show()
        viewModel.menuTemplateListResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    //get Menu id
                    viewModel.menuId = it.value.response.raws.data.menuId

                    CURRENT_DAY=MethodClass.getIntegerValueOfDay(it.value.response.raws.data.weekDay?.lowercase())
                    CURRENT_DAY
                    //show title
                    viewModel.title.value = "${getString(R.string.select_menu_for)} ${it.value.response.raws.data.weekDay}"

                    Constants.ORDER_DATE=it.value.response.raws.data.orderDate.toString()

                    //Menu Template list
                    val menuList: List<MenuTemplateListItem?>? =
                        it.value.response.raws.data.menuTemplateList

                    viewModel.isShowNextButton.value = false
                    if (menuList?.isNotEmpty() == true) {
                        for (i in menuList.indices) {
                            //check which position is selected previously
                            if (menuList[i]?.menuTemplateIsSelected == 1) {
                                viewModel.selectedTempId = menuList[i]?.menuTemplateId
                                viewModel.isShowNextButton.value = true
                                break
                            }

                        }

                        viewModel.isDataVisible.value = true

                    } else
                        viewModel.isDataVisible.value = false


                    /*menuTempListAdapter?.menuTempList = menuList
                    menuTempListAdapter?.notifyDataSetChanged()*/
                    binding.rvTempList.apply {
                        menuTempListAdapter = MenuTempListAdapter(
                            menuList,
                            this@MenuTemplateActivity,
                            this@MenuTemplateActivity,
                            viewModel.isXpLayoutVisible
                        )
                        this.adapter = menuTempListAdapter
                        this.isFocusable = false
                    }

//                    MethodClass.hideProgressDialog(this)
                    loader.dismiss()
                }
                is Resource.Failure -> {
                    viewModel.isDataVisible.value = false
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->
                            if (it.errorCode == 401) {
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        this,
                                        null,
                                        Constants.MENU_TEMPLATE_LIST,
                                        Constants.REFRESH_TOKEN
                                    )
                            } else
                                it.errorString.let { it1 ->
                                    MethodClass.showErrorDialog(
                                        this,
                                        it1,
                                        it.errorCode
                                    )
                                }

                        }


                    }

//                    MethodClass.hideProgressDialog(this)
                    loader.dismiss()
                }

            }


        }
    }

    private fun registerOnPageChangeCallbackOnStudentViewPager() {
        binding.studentViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {
                    viewModel.studentName.value =
                        viewModel.studentList[position].fName
                    viewModel.userType.value = viewModel.studentList[position].user_type

                } catch (e: Exception) {
                }
                Constants.SELECTED_STUDENT_ID =
                    viewModel.studentList[position].studentid

                //check if school type deis or private. schoolType == 1 is for private and schoolType == 2 is for deis
                //xp points only be visible for private school i.e. for schoolType == 1
                viewModel.isXpLayoutVisible =
                    viewModel.studentList[position].schoolType == 1 && viewModel.userType.value.equals(
                        Constants.STUDENT,
                        true
                    )

                Constants.SELECTED_STUDENT_POSITION = position
                DashBoardViewModel.selectedStudentPrePosition =
                    position// this is because in dashboard, selectedStudentPrePosition will be same as SELECTED_STUDENT_POSITION
                getMenuTemplate()


            }

        })

    }


    private fun goToNextPage(menuTemplateId: Int?) {

        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra("userType", viewModel.userType.value)
        intent.putExtra("menuTemplateId", menuTemplateId)
        intent.putExtra("studentName", viewModel.studentName.value)
        intent.putExtra("menuId", viewModel.menuId)
        intent.putExtra("from", viewModel.from)
        resultLauncher?.launch(intent)
        ///startActivity(intent)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        val menuTemplateItemList: List<MenuTemplateListItem> =
            arrayList as List<MenuTemplateListItem>

        goToNextPage(menuTemplateItemList[position].menuTemplateId)

    }

}


