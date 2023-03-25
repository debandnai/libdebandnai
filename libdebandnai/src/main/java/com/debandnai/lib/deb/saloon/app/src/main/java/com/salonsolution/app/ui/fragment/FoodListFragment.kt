package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.FoodListPagingAdapter
import com.salonsolution.app.adapter.PaginationLoadStateAdapter
import com.salonsolution.app.data.model.FoodList
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.FoodListViewModel
import com.salonsolution.app.databinding.FragmentFoodListBinding
import com.salonsolution.app.interfaces.FoodListClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
class FoodListFragment : BaseFragment(), FoodListClickListener {
    private lateinit var binding: FragmentFoodListBinding
    private lateinit var foodListViewModel: FoodListViewModel
    private lateinit var foodListPagingAdapter: FoodListPagingAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"
    private val navArgs: FoodListFragmentArgs by navArgs()
    // private val dashboardViewModel: DashboardViewModel by activityViewModels()

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_food_list, container, false)

        foodListViewModel = ViewModelProvider(this)[FoodListViewModel::class.java]
        binding.viewModel = foodListViewModel
        binding.lifecycleOwner = this
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
        handleLoadStateOfAdapter()
    }


    private fun getIntentData() {
        foodListViewModel.serviceId = navArgs.serviceId
        foodListViewModel.staffId = navArgs.staffId
    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        context?.let {
            with(binding) {

                foodListPagingAdapter =
                    FoodListPagingAdapter(this@FoodListFragment)

                binding.rvServiceList.run {
                    setHasFixedSize(true)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = foodListPagingAdapter.withLoadStateFooter(
                        footer = PaginationLoadStateAdapter { foodListPagingAdapter.retry() }
                    )
                }

                ivSortBy.setOnClickListener {
                    val action =
                        FoodDetailsFragmentDirections.actionGlobalServiceListSortDialogFragment(
                            foodListViewModel.sortBy.value?.first ?: Constants.PRICE,
                            foodListViewModel.sortBy.value?.second ?: 2

                        )
                    findNavController().navigate(action)
                }

                btDontNeed.setOnClickListener {
                    doNotNeedFood()
                }

                btGoToCart.setOnClickListener {
                    gotoCart()
                }

                loadData()

            }
        }

    }

    private fun gotoCart() {
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.categoriseFragment, true)
            .setLaunchSingleTop(true)
            .build()
        val action = FoodListFragmentDirections.actionGlobalCartFragment()
        findNavController().navigate(action, navOptions)
    }

    private fun setObserver() {

        foodListViewModel.foodListLiveData.observe(viewLifecycleOwner) {
            foodListPagingAdapter.submitData(lifecycle, it)
        }
        foodListViewModel.sortBy.observe(viewLifecycleOwner) {
            foodListPagingAdapter.submitData(lifecycle, PagingData.empty())
            loadData()
        }

        foodListViewModel.foodAddResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    foodListPagingAdapter.refresh()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }

        foodListViewModel.foodUpdateResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    foodListPagingAdapter.refresh()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
        foodListViewModel.doNotNeedResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    foodListViewModel.clearUpdateState()
                    gotoCart()
                }
                is ResponseState.Error -> {
                    foodListViewModel.clearUpdateState()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun loadData() {
        foodListViewModel.getFoodList(
            requestBodyHelper.getFoodListRequest(
                foodListViewModel.sortBy.value?.first,
                foodListViewModel.sortBy.value?.second,
                foodListViewModel.serviceId,
                foodListViewModel.staffId
            )
        )


    }

    private fun handleLoadStateOfAdapter() {
        foodListPagingAdapter.addLoadStateListener { loadStates ->

            when (loadStates.refresh) {
                is LoadState.Loading -> {
                    Log.d("tag", "ServiceList Refresh Loading")
                    showLoading()
                }
                is LoadState.NotLoading -> {
                    Log.d("tag", "ServiceList Refresh Not Loading")
                    hideLoader()

                }
                is LoadState.Error -> {
                    Log.d("tag", "ServiceList Refresh Not Error")
                    hideLoader()
                    val loadStateError = loadStates.refresh as LoadState.Error
                    if (loadStateError.error is HttpException) {
                        handleApiError(
                            false,
                            (loadStateError.error as HttpException).code(),
//                            (loadStateError.error as HttpException).message()
                        binding.root.context.getString(R.string.something_went_wrong)
                        )
                    } else {
                        //no network
                        // loadingError = true
                        handleApiError(true, 0, "")

                    }

                }
            }

            val displayEmptyMessage =
                (loadStates.append.endOfPaginationReached && foodListPagingAdapter.itemCount == 0)
            Log.d("tag", "ServiceList ${loadStates.append.endOfPaginationReached}")
            Log.d("tag", "ServiceList itemCount: ${foodListPagingAdapter.itemCount}")
            if (displayEmptyMessage) {
                //Toast.makeText(binding.root.context, "No services", Toast.LENGTH_LONG).show()
                Log.d("tag", "No services")
            } else {
                Log.d("tag", "Data present")
            }
        }

    }

    private fun hideLoader() {
        loadingPopup?.dismiss()
    }

    private fun showLoading() {
        loadingPopup?.show()
    }

    private fun getDestinationResult() {
        val currentBackStackEntry = findNavController().currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Pair<String, Int>>(ListSortDialogFragment.SORT_BY)
            ?.observe(currentBackStackEntry) {
                foodListViewModel.sortBy.value = it
            }

    }

    override fun onItemClick(position: Int, foodList: FoodList) {
        foodList.id?.let {
            val action = FoodListFragmentDirections.actionFoodListFragmentToFoodDetailsFragment(
                foodId = it,
                serviceId = foodListViewModel.serviceId,
                staffId = foodListViewModel.staffId
            )
            findNavController().navigate(action)
        }
    }

    override fun onAddClick(position: Int, foodList: FoodList) {
        foodList.id?.let {
            foodListViewModel.addFood(
                requestBodyHelper.getFoodAddRequest(
                    foodListViewModel.serviceId,
                    foodListViewModel.staffId,
                    it
                )
            )
        }

    }

    override fun onUpdateClick(position: Int, foodList: FoodList) {
        foodList.id?.let {
            foodListViewModel.updateFood(
                requestBodyHelper.getFoodUpdateRequest(
                    foodListViewModel.serviceId,
                    foodListViewModel.staffId,
                    it
                )
            )
        }
    }

    override fun onRemovedClick(position: Int, foodList: FoodList) {
        foodList.id?.let {
            foodListViewModel.updateFood(
                requestBodyHelper.getFoodRemovedRequest(
                    foodListViewModel.serviceId,
                    foodListViewModel.staffId,
                    it
                )
            )
        }
    }


    private fun doNotNeedFood() {
//        "type":"1/2/" //1-> Product, 2 -> Food
        foodListViewModel.doNotNeed(
            requestBodyHelper.getDoNotNeedRequest(
                "2",
                foodListViewModel.serviceId,
                foodListViewModel.staffId
            )
        )
    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if (this::foodListViewModel.isInitialized)
            loadData()
    }


}