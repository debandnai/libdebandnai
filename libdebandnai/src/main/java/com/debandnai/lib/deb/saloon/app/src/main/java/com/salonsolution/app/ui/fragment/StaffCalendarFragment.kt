package com.salonsolution.app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.StaffTimeSlotListAdapter
import com.salonsolution.app.data.model.TimeSlotModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.data.viewModel.StaffCalendarViewModel
import com.salonsolution.app.databinding.FragmentStaffCalendarBinding
import com.salonsolution.app.databinding.StaffCalendarWeekDaysItemBinding
import com.salonsolution.app.interfaces.StaffCalendarTimeSlotClickListener
import com.salonsolution.app.interfaces.PositiveNegativeCallBack
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.SettingsUtils
import com.salonsolution.app.utils.UtilsCommon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class StaffCalendarFragment : BaseFragment(), StaffCalendarTimeSlotClickListener {

    private lateinit var binding: FragmentStaffCalendarBinding
    private lateinit var staffCalendarViewModel: StaffCalendarViewModel
    private lateinit var staffTimeSlotListAdapter: StaffTimeSlotListAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private val navArgs: StaffCalendarFragmentArgs by navArgs()
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    @Inject
    lateinit var appSettingsPref: AppSettingsPref


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_staff_calendar, container, false)
        staffCalendarViewModel = ViewModelProvider(this)[StaffCalendarViewModel::class.java]
        binding.viewModel = staffCalendarViewModel
        binding.lifecycleOwner = this
        staffCalendarViewModel.setCalendar(SettingsUtils.getLanguage())


        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = false,
            isShowBottomNav = false,
            isShowToolbar = true
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDestinationResult()
        getIntentData()
        initView()
        setObserver()
    }

    private fun getIntentData() {
        staffCalendarViewModel.serviceId = navArgs.serviceId
        staffCalendarViewModel.serviceTime = navArgs.serviceTime
        staffCalendarViewModel.staffId = navArgs.staffId
    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        with(binding) {

            btAddToCart.setOnClickListener {
                if (staffCalendarViewModel.slotBookingData.size > 0) {
                    addToCart()
                }else{
                    errorPopUp?.showMessageDialog(root.context.getString(R.string.please_select_time_slot))
                }
            }
            staffTimeSlotListAdapter =
                StaffTimeSlotListAdapter(this@StaffCalendarFragment)
            rvTimeSlotList.run {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = staffTimeSlotListAdapter
            }

            tvMonth.setOnClickListener {
                openDatePickerDialog()
            }


        }

    }

    private fun openDatePickerDialog() {
        val c = staffCalendarViewModel.currentCalendar
        val action =
            StaffCalendarFragmentDirections.actionStaffCalendarFragmentToDatePickerFragment(
                day = c.get(Calendar.DAY_OF_MONTH),
                month = c.get(Calendar.MONTH),
                year = c.get(Calendar.YEAR)
            )
        findNavController().navigate(action)
    }

    private fun gotoProductList() {
        val action =
            StaffCalendarFragmentDirections.actionStaffCalendarFragmentToProductListFragment(
                serviceId = staffCalendarViewModel.serviceId,
                staffId = staffCalendarViewModel.staffId
            )
        findNavController().navigate(action)
    }

    private fun gotoCart() {
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.categoriseFragment, true)
            .setLaunchSingleTop(true)
            .build()
        val action = StaffCalendarFragmentDirections.actionGlobalCartFragment()
        findNavController().navigate(action,navOptions)

    }
    private fun loadData(startDate: String, endDate: String) {
        staffCalendarViewModel.staffCalendar(
            requestBodyHelper.getStaffCalendarRequest(
                staffCalendarViewModel.serviceId,
                staffCalendarViewModel.staffId,
                startDate,
                endDate
            )
        )
    }

    private fun setObserver() {
        staffCalendarViewModel.staffCalendarResponseLiveData.observe(viewLifecycleOwner) {

            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.data?.response?.data.toString())
                    it.data?.response?.data?.let { data ->
                        //clear previous selected slot
                        staffCalendarViewModel.slotBookingData.clear()
                        //calculate time slots
                        staffCalendarViewModel.calculateTimeSlots(data)
                    }

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        staffCalendarViewModel.weekDaysListLiveData.observe(viewLifecycleOwner) {
            Log.d("tag", "WeekDays: $it")
            if (!it.isNullOrEmpty()) {
                loadData(it.first(), it.last())
                setWeekDays(it)
            }

        }

        staffCalendarViewModel.calculateTimeSlotsState.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                }
            }
        }

        staffCalendarViewModel.timeSlotListLiveData.observe(viewLifecycleOwner) {
            Log.d("tag", "$it")
            staffTimeSlotListAdapter.submitList(it.map { list ->
                list.copy()
            })
        }

        staffCalendarViewModel.manageSlotsSelectionLiveData.observe(viewLifecycleOwner) {

            when (it) {
                is ResponseState.Loading -> {
                    //  loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    //loadingPopup?.dismiss()
                }
                is ResponseState.Error -> {
                    // loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    if (it.errorCode == 0) {
                        errorPopUp?.showMessageDialog(context?.getString(R.string.this_slot_is_not_available))
                    }
                }
            }
        }

        staffCalendarViewModel.addToCartResponseLiveData.observe(viewLifecycleOwner) {

            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    staffCalendarViewModel.clearUpdateState()
                    staffCalendarViewModel.slotBookingData.clear()
                    showSuccessfullyUpdatePopup()

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    staffCalendarViewModel.clearUpdateState()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun setWeekDays(list: ArrayList<String>) {
        //remove previous views
        binding.llWeekList.removeAllViews()

        for (item in list) {
            val weekDay = UtilsCommon.getWeekDayWithName(item, staffCalendarViewModel.localeCode)
            val inflater: LayoutInflater =
                LayoutInflater.from(binding.root.context).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val listItemBinding: StaffCalendarWeekDaysItemBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.staff_calendar_week_days_item,
                    null,
                    false
                )
            listItemBinding.tvDate.text = weekDay.first
            listItemBinding.tvName.text = weekDay.second

            //setting weight 1 to the subItem root layout
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.weight = 1F
            listItemBinding.root.layoutParams = lp

            //add child view to the linear layout
            binding.llWeekList.addView(listItemBinding.root)
        }
    }

    private var onClickJob: Job? = null
    override fun onClick(
        slotTimePosition: Int,
        slotDatePosition: Int,
        timeSlotModel: TimeSlotModel
    ) {
        Log.d(
            "tag",
            "slotTimePosition: $slotTimePosition; slotDatePosition: $slotDatePosition,timeSlotModel: $timeSlotModel "
        )
//        if (staffCalendarViewModel.isCalculating)
//            return
        onClickJob?.cancel()
        onClickJob = lifecycleScope.launch {
            delay(100)
            staffCalendarViewModel.manageTimeSlotsSelection(
                slotTimePosition,
                slotDatePosition,
                timeSlotModel
            )
            Log.d("clickDebounced", "Hit")
        }


    }

    private fun addToCart() {
        val date = staffCalendarViewModel.slotBookingData[0].timeSlotModel.date
        val startTime = staffCalendarViewModel.slotBookingData.first().timeSlotModel.time

        //end time
        val calendar = Calendar.getInstance(Locale(staffCalendarViewModel.localeCode))
        val endDate = UtilsCommon.getDateFromTime(
            staffCalendarViewModel.slotBookingData.last().timeSlotModel.time ?: "24:00",
            staffCalendarViewModel.localeCode
        )
        calendar.time = endDate
        calendar.add(Calendar.MINUTE, staffCalendarViewModel.timeSlotInterval)
        val endTime = UtilsCommon.getTimeFromDate(calendar.time, staffCalendarViewModel.localeCode)

        staffCalendarViewModel.addToCart(
            requestBodyHelper.getAddToCartRequest(
                staffCalendarViewModel.serviceId,
                staffCalendarViewModel.staffId,
                date,
                startTime,
                endTime
            )
        )

//        for (slot in staffCalendarViewModel.slotBookingData) {
//            Log.d("mTag", "Selected: ${slot.timeSlotModel.date} - ${slot.timeSlotModel.time}")
//        }
//
//        staffCalendarViewModel.timeSlotListLiveData.value?.let {
//            for (slot in it) {
//                Log.d("mTag", "Selected: ${slot.time} - ${slot.timeSlotList}")
//            }
//        }

    }

    private fun getDestinationResult() {
        val currentBackStackEntry = findNavController().currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Date>(DatePickerFragment.SELECTED_DATE)
            ?.observe(currentBackStackEntry) {
                val calendar = Calendar.getInstance(Locale(staffCalendarViewModel.localeCode))
                if (it.before(calendar.time)) {
                    errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.past_date_not_allowed))
                } else {
                    staffCalendarViewModel.setDateToCalendar(it)
                }
            }

    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if (
            this::staffCalendarViewModel.isInitialized &&
            (staffCalendarViewModel.weekDaysListLiveData.value?.size ?: 0) > 0
        )
            try {
                loadData(
                    staffCalendarViewModel.weekDaysListLiveData.value?.first() ?: "",
                    staffCalendarViewModel.weekDaysListLiveData.value?.last() ?: ""
                )
            } catch (e:Exception){
                Log.d("tag","Exception: ${e.message}")
            }

    }

    private fun showSuccessfullyUpdatePopup() {
        CustomPopup.showSuccessfullyAddedToCartPopup(
            mContext = binding.root.context,
            description = binding.root.context.getString(R.string.successfully_added_to_cart),
            positiveButtonText = binding.root.context.getString(R.string.go_to_cart),
            negativeButtonText = binding.root.context.getString(R.string.select_product),
            positiveNegativeCallBack = object :
                PositiveNegativeCallBack {
                override fun onPositiveButtonClick() {
                    gotoCart()
                }

                override fun onNegativeButtonClick() {
                    gotoProductList()
                }
            })

    }

    override fun onDestroy() {
        super.onDestroy()
        //clear selected slot
        staffCalendarViewModel.slotBookingData.clear()
    }


}