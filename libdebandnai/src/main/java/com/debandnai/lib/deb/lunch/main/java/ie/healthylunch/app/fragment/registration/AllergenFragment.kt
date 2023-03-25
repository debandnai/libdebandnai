package ie.healthylunch.app.fragment.registration

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.JsonArray
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.AllergenListAdapter
import ie.healthylunch.app.adapter.CulturalConsiderationListAdapter
import ie.healthylunch.app.adapter.NutritionalConsiderationListAdapter
import ie.healthylunch.app.data.model.allergenListModel.DataItem
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository

import ie.healthylunch.app.data.viewModel.AllergenViewModel
import ie.healthylunch.app.databinding.FragmentAllergenBinding
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.EDIT_ALLERGEN
import ie.healthylunch.app.utils.Constants.Companion.PAGE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID_TAG
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID_TAG_2
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_NAME
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE_TAG
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE_TAG_2


class AllergenFragment : BaseFragment<AllergenViewModel, StudentRepository>(),
        AdapterItemOnclickListener {


    private var allergenListAdapter:AllergenListAdapter? =null
    var culturalConsiderationListAdapter: CulturalConsiderationListAdapter? =null
    var nutritionalConsiderationListAdapter: NutritionalConsiderationListAdapter? =null
    var allergenAlertmsg: String? =null

    lateinit var fragmentAllergenBinding: FragmentAllergenBinding



      
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        setUpViewModel()


        fragmentAllergenBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_allergen,
                container,
                false
        )
        fragmentAllergenBinding.context = this
        fragmentAllergenBinding.viewModel = viewModel
        viewModel.allergenFragment = this
        fragmentAllergenBinding.lifecycleOwner = this
        init()
        viewOnClick()

        return fragmentAllergenBinding.root
    }
      
    private fun init() {

        activity?.let { act ->
            allergenListAdapter =
                    AllergenListAdapter(act, ArrayList(), this@AllergenFragment)
            fragmentAllergenBinding?.allergenRv?.adapter = allergenListAdapter
            view?.isFocusable = false

            //Cultural Consideration
            culturalConsiderationListAdapter =
                    CulturalConsiderationListAdapter(act, ArrayList(), this@AllergenFragment)
            fragmentAllergenBinding?.rvCulturalConsideration?.adapter = culturalConsiderationListAdapter
            view?.isFocusable = false

            //Nutritional Consideration-
            nutritionalConsiderationListAdapter =
                    NutritionalConsiderationListAdapter(act, ArrayList(), this@AllergenFragment)
            fragmentAllergenBinding?.rvNutritionalConsideration?.adapter = nutritionalConsiderationListAdapter
            view?.isFocusable = false

            allergenListApiCall()


            //get Allergen List Response
            getAllergenListResponse(act)

            //get Save Allergen Response
            getSaveAllergenResponse()

        }
    }
    private fun viewOnClick() {
        fragmentAllergenBinding.rlContinue.setOnClickListener {
            saveAllergenApiCall()
        }

        fragmentAllergenBinding.rlTopNew.setOnClickListener {
            (activity as? RegistrationActivity)?.finish()
        }
    }


    fun saveAllergenApiCall() {
        activity?.let { act->
            viewModel.continueButtonEnabled.value = false
            MethodClass.showProgressDialog(act)
            viewModel.selectedAllergenList = JsonArray()
            val jsonObject = MethodClass.getCommonJsonObject(act)
            setAllergensJsonArray(viewModel.selectedAlergenList)
            setAllergensJsonArray(viewModel.nutritionalArrayList)
            setAllergensJsonArray(viewModel.culturalAllergenList)
            //jsonObject.addProperty("student_id", StudentId.value)
            jsonObject.addProperty("student_id", Constants.STUDENT_ID)
            jsonObject.addProperty("user_type", Constants.USER_TYPE)
            jsonObject.add("allergen", viewModel.selectedAllergenList)

            val loginResponse: LoginResponse? =
                    UserPreferences.getAsObject<LoginResponse>(
                            act,
                            Constants.USER_DETAILS
                    )

            loginResponse?.response?.raws?.data?.token?.let {token->
                viewModel.saveAllergensList(jsonObject, token)
            }


        }
    }
      
    fun getSaveAllergenResponse() {
        activity?.let { act->
            viewModel.saveAllergenListLiveData.observe(viewLifecycleOwner) { response ->


                when (response) {
                    is Resource.Success -> {
                        Toast.makeText(requireActivity(),
                            response.value.response.raws.successMessage, Toast.LENGTH_SHORT).show()

                        val bundle = Bundle()
                        bundle.putString(STUDENT_NAME, viewModel.studentFirstName.value)
                        bundle.putString(PAGE, viewModel.page.value)
                        val navHostFragment =
                                act.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                        val navController = navHostFragment.navController

                        if (viewModel.page.value?.equals(EDIT_ALLERGEN,true) == true && (viewModel.selectedAlergenList[STATUS_ZERO].isMapped== STATUS_ONE)) {
                            CustomDialog.showOkTypeDialog(act,response.value.response.raws.successMessage,
                                    object :  DialogOkListener{
                                        override fun okOnClick(dialog: Dialog, activity: Activity) {
                                            dialog.dismiss()
                                            (activity as? RegistrationActivity)?.finish()
                                        }
                                    })

                        }
                        else if (viewModel.selectedAlergenList[STATUS_ZERO].isMapped== STATUS_ONE){
                            navController.navigate(
                                    R.id.action_allergenFragment_to_addStudentSuccessFragment,
                                    bundle
                            )
                        }
                        else{
                            navController.navigate(
                                    R.id.action_allergenFragment_to_allergenProductRemoveConfirmation,
                                    bundle
                            )
                        }
                        viewModel.continueButtonEnabled.value = true

                        MethodClass.hideProgressDialog(act)


                    }
                    is Resource.Failure -> {

                        if (response.errorBody != null) {
                            response.errorString?.let { _ ->

                                response.errorString.let { it1 ->
                                    if (response.errorCode == 401)
                                        AppController.getInstance()
                                                .refreshTokenResponse(
                                                        act,
                                                        this,
                                                        Constants.SAVE_ALLERGENS_LIST,
                                                        Constants.REFRESH_TOKEN
                                                )
                                    else {
                                        MethodClass.showErrorDialog(
                                                act,
                                                it1,
                                                response.errorCode
                                        )
                                    }
                                }

                            }


                        }

                        viewModel. continueButtonEnabled.value = true

                        MethodClass.hideProgressDialog(act)
                    }
                    else -> {}
                }

            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.page.value = arguments?.getString(PAGE)
        viewModel.studentId.value = arguments?.getInt(STUDENT_ID_TAG)
        viewModel.userType.value = arguments?.getString(USER_TYPE_TAG)
        viewModel.studentFirstName.value = arguments?.getString(STUDENT_NAME)
        viewModel.isFromEditAllergenPage.value = viewModel.page.value?.equals(EDIT_ALLERGEN,true)==true
    }
    fun allergenListApiCall() {
        activity?.let {act->
            MethodClass.showProgressDialog(act)
            val jsonObject = MethodClass.getCommonJsonObject(act)
            //jsonObject.addProperty("student_id", StudentId.value)
            jsonObject.addProperty(STUDENT_ID_TAG_2, STUDENT_ID)
            jsonObject.addProperty(USER_TYPE_TAG_2, USER_TYPE)


            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    activity,
                    Constants.USER_DETAILS
                )

            loginResponse?.response?.raws?.data?.token?.let { token ->
                viewModel.allergenList(jsonObject, token)
            }
        }


    }
    override fun getViewModel() = AllergenViewModel::class.java
    override fun getRepository() =
            StudentRepository(remoteDataSource.buildApi(ApiInterface::class.java))
    @SuppressLint("NotifyDataSetChanged")
      
    fun getAllergenListResponse(activity: Activity) {
        viewModel.allergenListResponse?.observe(viewLifecycleOwner) { response->
            when (response) {
                is Resource.Success -> {



                    //get the selected items list. Here selectedIDs is the list of Integer. Because it holds the ids
                    val selectedIDs =
                            if (allergenListAdapter?.allergenList?.isNotEmpty() == true) {//If adapter list has already values
                                allergenListAdapter?.allergenList!!.filter { allergenList -> allergenList.isMapped == STATUS_ONE }
                                        .map { allergenList -> allergenList.id }.toMutableList()
                            } else {// If adapter list has no value(st time)
                                response.value.response.raws?.data?.allergen!!.filter { allergenList -> allergenList.isMapped == STATUS_ONE }
                                        .map { allergenList -> allergenList.id }.toMutableList()
                            }


                    //For 1st time when adapter list has no value, then by default the 1st item will be selected
                    if (selectedIDs.isEmpty()) {
                        selectedIDs.add(STATUS_ZERO)
                    }

                    //add the selected ids to json array list
                    viewModel.selectedAllergenList = JsonArray()
                    for (i in selectedIDs.indices)
                        if (selectedIDs[i] != STATUS_ZERO)
                            viewModel.selectedAllergenList.add(selectedIDs[i].toString())

                    //create 1st item first item (No Allergen item) of the  list
                    val dataItem = DataItem()
                    dataItem.id = STATUS_ZERO
                    dataItem.isMapped = STATUS_ZERO
                    dataItem.allergenName = "No Allergen"

                    //add all items after adding first item
                    val array = ArrayList<DataItem>()
                    array += dataItem //set the first item (No Allergen item) to list
                    array += ArrayList<DataItem>(response.value.response.raws?.data?.allergen!!)

                    //getting the final filtered list
                    viewModel.allergenList = array.map { allergenList ->
                        allergenList.isMapped = if (selectedIDs.contains(allergenList.id))
                            STATUS_ONE
                        else
                            STATUS_ZERO

                        allergenList
                    }.toMutableList()


                    if(!viewModel.allergenList.isNullOrEmpty()) {
                        fragmentAllergenBinding?.rlBottom?.isVisible=true
                        viewModel.selectedAlergenList= viewModel.allergenList   as ArrayList<DataItem>
                        allergenListAdapter?.allergenList = viewModel.allergenList

                        //refreshing the adapter
                        allergenListAdapter?.notifyDataSetChanged()
                    }
                    else{
                        fragmentAllergenBinding?.rlBottom?.isVisible=false
                    }
                    if(!response.value.response.raws?.data?.cultural.isNullOrEmpty()) {
                        fragmentAllergenBinding?.rlCulturalConsideration?.isVisible=true
                        viewModel.culturalAllergenList= response.value.response.raws?.data?.cultural!!   as ArrayList<DataItem>
                        //refreshing the adapter
                        culturalConsiderationListAdapter?.allergenList = response.value.response.raws.data?.cultural!!
                        culturalConsiderationListAdapter?.notifyDataSetChanged()
                    }
                    else{
                        fragmentAllergenBinding?.rlCulturalConsideration?.isVisible=false
                    }



                    if(!response.value.response.raws?.data?.nutritional.isNullOrEmpty()){
                        fragmentAllergenBinding?.rlNutritionalConsideration?.isVisible=true
                        viewModel.nutritionalArrayList= response.value.response.raws?.data?.nutritional!!   as ArrayList<DataItem>

                        nutritionalConsiderationListAdapter?.allergenList =
                                response.value.response.raws?.data.nutritional

                        //refreshing the adapter
                        nutritionalConsiderationListAdapter?.notifyDataSetChanged()
                    }
                    else{
                        fragmentAllergenBinding?.rlNutritionalConsideration?.isVisible=false
                    }
                    MethodClass.hideProgressDialog(activity)
                }
                is Resource.Failure -> {

                    if (response.errorBody != null) {
                        response.errorString?.let { _ ->

                            response.errorString.let { error ->
                                if (response.errorCode == 401)
                                    AppController.getInstance()
                                            .refreshTokenResponse(
                                                    activity,
                                                    this@AllergenFragment,
                                                    Constants.ALLERGEN_LIST,
                                                    Constants.REFRESH_TOKEN
                                            )
                                else {
                                    MethodClass.showErrorDialog(
                                            activity,
                                            error,
                                            response.errorCode
                                    )
                                }
                            }
                        }


                    }
                    viewModel._allergenListResponse.value = null
                    viewModel.allergenListResponse = viewModel._allergenListResponse
                    MethodClass.hideProgressDialog(activity)
                }
                else -> {}
            }

        }

    }
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        if (tag.equals(Constants.ALLERGEN_LIST,true)){
            viewModel.selectedAlergenList = arrayList as ArrayList<DataItem>
        }
        else if (tag.equals(Constants.NUTRITIONAL_CONSIDERATION,true)){
            viewModel.nutritionalArrayList = arrayList as ArrayList<DataItem>
            if (viewModel.nutritionalArrayList[position].isMapped== STATUS_ONE) {
                allergenAlertmsg =
                        getString(R.string.you_will_be_emailed_all_relevant_nutritional_information_of_our_products_in_accordance_with_your_selection)
                activity?.let { act->
                    CustomDialog.allergenTypeDialog(
                            act,
                            STATUS_ONE,
                            allergenAlertmsg
                    )
                }
            }
        }
        else {
            viewModel.culturalAllergenList = arrayList as ArrayList<DataItem>
            if (viewModel.culturalAllergenList[position].isMapped==STATUS_ONE) {
                allergenAlertmsg=getString(R.string.all_chicken_turkey_relevent_products_are_halal_certified)
                activity?.let { act->
                    CustomDialog.allergenTypeDialog(
                            act,
                            STATUS_ZERO,
                            allergenAlertmsg
                    )
                }
            }
        }
    }

    private fun setAllergensJsonArray(dataItem: MutableList<DataItem>) {
        for (i in dataItem.indices) {

            if (dataItem[i].isMapped == Constants.STATUS_ONE && dataItem[i].id!= Constants.STATUS_ZERO) {
                viewModel.selectedAllergenList.add(dataItem[i].id.toString())
            }
        }

    }
}
