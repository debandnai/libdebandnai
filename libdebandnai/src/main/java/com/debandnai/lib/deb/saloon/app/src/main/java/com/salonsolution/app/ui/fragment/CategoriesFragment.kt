package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salonsolution.app.R
import com.salonsolution.app.adapter.CategoriesListAdapter
import com.salonsolution.app.data.model.CategoryListModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.CategoriseViewModel
import com.salonsolution.app.databinding.FragmentCategoriseBinding
import com.salonsolution.app.interfaces.CategoriesItemClickListener
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CategoriesFragment : BaseFragment(), CategoriesItemClickListener {

    private lateinit var binding: FragmentCategoriseBinding
    private lateinit var categoriseViewModel: CategoriseViewModel
    private lateinit var categoriesListAdapter: CategoriesListAdapter
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categorise, container, false)
        categoriseViewModel = ViewModelProvider(this)[CategoriseViewModel::class.java]
        binding.viewModel = categoriseViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = true,
            isShowBottomNav = true,
            isShowToolbar = true
        )


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
    }

    private fun setObserver() {
        categoriseViewModel.categoriesResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    categoriseViewModel.currentCategoriesList = it.data?.response?.data
                    categoriesListAdapter.submitList(it.data?.response?.data?.categoryListModel)

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

        with(binding) {
            categoriesListAdapter = CategoriesListAdapter(this@CategoriesFragment)
            rvCategoriesList.run {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = categoriesListAdapter
            }

            includeSearchLayout.edtSearch.isFocusable = false
            includeSearchLayout.edtSearch.setOnClickListener {
                gotoSearch()
            }

            loadData()

        }
    }

    private fun loadData() {
        categoriseViewModel.allCategories(requestBodyHelper.getAllCategoriesRequest())
    }

    private fun gotoSearch() {
        val extra = FragmentNavigatorExtras(
            binding.includeSearchLayout.llSearch to binding.root.context.getString(R.string.transition_name_to)
        )
        findNavController().navigate(R.id.action_global_searchFragment, null, null, extra)
    }

    override fun onItemClick(position: Int, categoryListModel: CategoryListModel) {
        categoriseViewModel.currentCategoriesList?.let {
            val action = CategoriesFragmentDirections.actionCategoriseFragmentToServiceListFragment(
                categoriesList = it,
                categoryId = categoryListModel.id ?: -1
            )
            findNavController().navigate(action)
        }

    }

    override fun onRefreshDataEvent() {
        super.onRefreshDataEvent()
        if (this::categoriseViewModel.isInitialized)
            loadData()
    }

}