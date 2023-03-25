package ie.healthylunch.app.fragment.students

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
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
import ie.healthylunch.app.data.viewModel.EditStudentAllergenViewModel
import ie.healthylunch.app.databinding.AllergenTypeDialogBinding
import ie.healthylunch.app.databinding.DashBoardBottomCalenderHelpDialogBinding
import ie.healthylunch.app.databinding.FragmentEditStudentAllergenBinding
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.ALLERGEN_LIST
import ie.healthylunch.app.utils.Constants.Companion.NUTRITIONAL_CONSIDERATION
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.CustomDialog.Companion.allergenTypeDialog
import androidx.navigation.fragment.findNavController
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditStudentAllergenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditStudentAllergenFragment :
    BaseFragment<EditStudentAllergenViewModel, StudentRepository>(), AdapterItemOnclickListener {
    private var allergenListAdapter:AllergenListAdapter? =null
    var culturalConsiderationListAdapter: CulturalConsiderationListAdapter? =null
    var nutritionalConsiderationListAdapter: NutritionalConsiderationListAdapter? =null
    var allergenAlertmsg: String? =null

    var binding: FragmentEditStudentAllergenBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

      
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpViewModel()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_student_allergen,
            container,
            false
        )
        binding?.context = this
        binding?.viewModel = viewModel
        viewModel.editStudentAllergenFragment = this
        binding?.lifecycleOwner = requireActivity()
        init()
        observer()
        return binding?.root

    }

    private fun init() {

        activity?.let { act->
            allergenListAdapter =
                AllergenListAdapter(act, ArrayList(), this@EditStudentAllergenFragment)
            binding?.rvStudentAllergen?.adapter = allergenListAdapter
            view?.isFocusable = false

            //Cultural Consideration
            culturalConsiderationListAdapter =
                CulturalConsiderationListAdapter(act, ArrayList(), this@EditStudentAllergenFragment)
            binding?.rvCulturalConsideration?.adapter = culturalConsiderationListAdapter
            view?.isFocusable = false

            //Nutritional Consideration-
            nutritionalConsiderationListAdapter =
                NutritionalConsiderationListAdapter(act, ArrayList(), this@EditStudentAllergenFragment)
            binding?.rvNutritionalConsideration?.adapter = nutritionalConsiderationListAdapter
            view?.isFocusable = false


        }

    }



    @SuppressLint("NotifyDataSetChanged")
      
    private fun observer() {
        activity?.let { act->
            viewModel.allergenListResponse?.observe(viewLifecycleOwner) { response->


                when (response) {
                    is Resource.Success -> {



                        //get the selected items list. Here selectedIDs is the list of Integer. Because it holds the ids
                        val selectedIDs =
                            if (allergenListAdapter?.allergenList?.isNotEmpty() == true) {//If adapter list has already values
                                allergenListAdapter?.allergenList?.filter { allergenList -> allergenList.isMapped == STATUS_ONE }
                                    ?.map { allergenList -> allergenList.id }?.toMutableList()
                            } else {// If adapter list has no value(st time)
                                response.value.response.raws?.data?.allergen!!.filter { allergenList -> allergenList.isMapped == STATUS_ONE }
                                    .map { allergenList -> allergenList.id }.toMutableList()
                            }


                        //For 1st time when adapter list has no value, then by default the 1st item will be selected
                        if (selectedIDs?.isEmpty() == true) {
                            selectedIDs.add(STATUS_ZERO)
                        }

                        //add the selected ids to json array list
                        viewModel.selectedAllergenList = JsonArray()
                        if (selectedIDs != null) {
                            for (i in selectedIDs.indices)
                                if (selectedIDs[i] != STATUS_ZERO)
                                    viewModel.selectedAllergenList.add(selectedIDs[i].toString())
                        }

                        //create 1st item first item (No Allergen item) of the  list
                        val dataItem = DataItem()
                        dataItem.id = STATUS_ZERO
                        dataItem.isMapped = STATUS_ZERO
                        dataItem.allergenName = getString(R.string.no_allergen)

                        //add all items after adding first item
                        val array = ArrayList<DataItem>()
                        array += dataItem //set the first item (No Allergen item) to list
                        array += ArrayList<DataItem>(response.value.response.raws?.data?.allergen!!)

                        //getting the final filtered list
                        viewModel.allergenList = array.map { allergenList ->
                            allergenList.isMapped = if (selectedIDs?.contains(allergenList.id) == true)
                                STATUS_ONE
                            else
                                STATUS_ZERO

                            allergenList
                        }.toMutableList()


                        if(!viewModel.allergenList.isNullOrEmpty()) {
                            binding?.rlAllergens?.isVisible=true
                            viewModel.selectedAlergenList= viewModel.allergenList   as ArrayList<DataItem>


                            allergenListAdapter?.allergenList = viewModel.allergenList

                            //refreshing the adapter
                            allergenListAdapter?.notifyDataSetChanged()
                        }
                        else{
                            binding?.rlAllergens?.isVisible=false
                        }
                        if(!response.value.response.raws?.data?.cultural.isNullOrEmpty()) {
                            binding?.rlCulturalConsideration?.isVisible=true
                            viewModel.culturalAllergenList= response.value.response.raws?.data?.cultural!!   as ArrayList<DataItem>

                            culturalConsiderationListAdapter?.allergenList =
                                response.value.response.raws.data.cultural

                            //refreshing the adapter
                            culturalConsiderationListAdapter?.notifyDataSetChanged()
                        }
                        else{
                            binding?.rlCulturalConsideration?.isVisible=false
                        }



                        if(!response.value.response.raws?.data?.nutritional.isNullOrEmpty()){
                            binding?.rlNutritionalConsideration?.isVisible=true
                            viewModel.nutritionalArrayList= response.value.response.raws?.data?.nutritional!!   as ArrayList<DataItem>

                            nutritionalConsiderationListAdapter?.allergenList =
                                response.value.response.raws.data.nutritional

                            //refreshing the adapter
                            nutritionalConsiderationListAdapter?.notifyDataSetChanged()
                        }
                        else{
                            binding?.rlNutritionalConsideration?.isVisible=false
                        }
                        MethodClass.hideProgressDialog(act)
                    }
                    is Resource.Failure -> {

                        if (response.errorBody != null) {
                            MethodClass.hideProgressDialog(act)
                            response.errorString?.let { error ->
                                if (response.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            act,
                                            viewModel.editStudentAllergenFragment,
                                            Constants.ALLERGEN_LIST,
                                            Constants.REFRESH_TOKEN
                                        )
                                else {
                                    MethodClass.showErrorDialog(
                                        act,
                                        error,
                                        response.errorCode
                                    )
                                }


                            }


                        }
                        MethodClass.hideProgressDialog(act)
                        viewModel._allergenListResponse.value = null
                        viewModel.allergenListResponse = viewModel._allergenListResponse
                    }
                    else -> {}
                }

            }
        }

    }



    override fun onResume() {
        super.onResume()
        DashBoardActivity.dashBoardToolbar.visibility = View.GONE

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditStudentAllergenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditStudentAllergenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

      
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.studentId.value = arguments?.getInt("studentId")
        viewModel.userType.value = arguments?.getString("userType")
        viewModel.studentName.value = arguments?.getString("studentName")
        viewModel.message.value = "Allergens for " + arguments?.getString("studentName")


        //allergen List Api Call
        viewModel.allergenListApiCall(requireActivity())

    }

    override fun getViewModel() = EditStudentAllergenViewModel::class.java
    override fun getRepository() =
        StudentRepository(remoteDataSource.buildApi(ApiInterface::class.java))








    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        //viewModel.selectedAllergenList = JsonArray()
        //val allergenList: ArrayList<DataItem> = arrayList as ArrayList<DataItem>
        if (tag.equals(ALLERGEN_LIST,true)){
            viewModel.selectedAlergenList = arrayList as ArrayList<DataItem>
            /*for (i in allergenList.indices) {

                if (allergenList[i].isMapped == STATUS_ONE) {
                    viewModel.selectedAllergenList.add(allergenList[i].id.toString())
                }

                if (allergenList[STATUS_ZERO].isMapped == STATUS_ONE) {
                    viewModel.selectedAllergenList = JsonArray()
                    break
                }
            }*/
        }
        else if (tag.equals(NUTRITIONAL_CONSIDERATION,true)){
            viewModel.nutritionalArrayList = arrayList as ArrayList<DataItem>
            if (viewModel.nutritionalArrayList[position].isMapped== STATUS_ONE) {
                allergenAlertmsg =
                    getString(R.string.you_will_be_emailed_all_relevant_nutritional_information_of_our_products_in_accordance_with_your_selection)
                activity?.let { act->allergenTypeDialog(act, STATUS_ONE,allergenAlertmsg)}
            }
        }
        else {
            //   val culturalAllergenList: ArrayList<DataItem> = arrayList as ArrayList<DataItem>
            viewModel.culturalAllergenList = arrayList as ArrayList<DataItem>
            if (viewModel.culturalAllergenList[position].isMapped==STATUS_ONE) {
                allergenAlertmsg=getString(R.string.all_chicken_turkey_relevent_products_are_halal_certified)
                activity?.let { act->allergenTypeDialog(act, STATUS_ZERO,allergenAlertmsg)}
            }
        }



    }

      
    fun save() {
        binding?.root?.context?.let { ctx ->
            viewModel.isSubmitEnabled.value = false
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    activity,
                    Constants.USER_DETAILS
                )


            viewModel.selectedAllergenList = JsonArray()
            viewModel.setAllergensJsonArray(viewModel.selectedAlergenList)
            viewModel.setAllergensJsonArray(viewModel.nutritionalArrayList)
            viewModel.setAllergensJsonArray(viewModel.culturalAllergenList)
            val jsonObject = MethodClass.getCommonJsonObject(ctx)

            //jsonObject.addProperty("student_id", StudentId.value)
            jsonObject.addProperty("student_id", Constants.STUDENT_ID)
            jsonObject.addProperty("user_type", viewModel.userType.value)
            jsonObject.add("allergen", viewModel.selectedAllergenList)

            MethodClass.showProgressDialog(ctx as DashBoardActivity)
            loginResponse?.response?.raws?.data?.token?.let { token ->
                viewModel.saveAllergenList(jsonObject, token)
            }

            viewModel.saveAllergenListResponse?.observe(viewLifecycleOwner) {


                when (it) {
                    is Resource.Success -> {
                        Toast.makeText(
                            activity,
                            it.value.response.raws.successMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController()
                            .navigate(
                                R.id.action_editStudentAllergenFragment_to_viewaddedstudentprofilelistFragment
                            )

                        MethodClass.hideProgressDialog(ctx)
                        viewModel.isSubmitEnabled.value = true
                        viewModel._saveAllergenListResponse.value = null
                        viewModel.saveAllergenListResponse = viewModel._saveAllergenListResponse


                    }
                    is Resource.Failure -> {

                        if (it.errorBody != null) {
                            MethodClass.hideProgressDialog(ctx)
                            it.errorString?.let { it1 ->

                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            ctx,
                                            this@EditStudentAllergenFragment,
                                            Constants.SAVE_ALLERGEN,
                                            Constants.REFRESH_TOKEN
                                        )
                                else {
                                    MethodClass.showErrorDialog(
                                        ctx,
                                        it1,
                                        it.errorCode
                                    )
                                }


                            }


                        }
                        MethodClass.hideProgressDialog(ctx)
                        viewModel.isSubmitEnabled.value = true
                        viewModel._saveAllergenListResponse.value = null
                        viewModel.saveAllergenListResponse = viewModel._saveAllergenListResponse
                    }
                    else -> {}
                }

            }

        }
    }
}