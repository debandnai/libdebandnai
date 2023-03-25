package com.movie.myapplication.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.movie.myapplication.R
import com.movie.myapplication.adapter.PopularMovierAdapter
import com.movie.myapplication.adapter.TrandingrMovierAdapter
import com.movie.myapplication.data.viewModel.AuthViewModel
import com.movie.myapplication.data.viewModel.HomeViewModel
import com.movie.myapplication.databinding.FragmentHomeBinding
import com.movie.myapplication.databinding.FragmentLoginBinding
import com.movie.myapplication.interface_package.AdapterItemClickListener
import com.movie.myapplication.network.ResponseState
import com.movie.myapplication.ui.activity.AuthenticationActivity
import com.movie.myapplication.ui.base.BaseFragment
import com.movie.myapplication.utils.MethodClass


class HomeFragment :BaseFragment(), AdapterItemClickListener {
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding
    private var loader: Dialog? = null
    private var popularMovierAdapter: PopularMovierAdapter? = null
    private var trandingrMovierAdapter: TrandingrMovierAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel=viewModel
        binding.lifecycleOwner =this
        initialise()
        viewModel.getPopularMovieList()
        observer()
        return binding.root
    }


    private fun initialise() {
        context?.let { loader = MethodClass.custom_loader(it, getString(R.string.please_wait))}

    }
    private fun observer() {
        viewModel.productListResponseLiveData.observe(viewLifecycleOwner){
            when(it){
                is ResponseState.Loading->{
                    loader?.show()

                }
                is ResponseState.Success->{
                    popularMovierAdapter =
                        it.data?.movie_results?.let { it1 -> PopularMovierAdapter(it1, this@HomeFragment) }
                    binding.rvPopularMovie.adapter = popularMovierAdapter
                    binding.rvPopularMovie.isFocusable = false
                    viewModel.getTrandingMovieList()
                    viewModel.showHideTitle.value=true
                }
                is ResponseState.Error->{
                    loader?.dismiss()
                    viewModel.showHideTitle.value=true
                }
            }
        }

        viewModel.trandingtListResponseLiveData.observe(viewLifecycleOwner){
            when(it){
                is ResponseState.Loading->{}
                is ResponseState.Success->{
                    loader?.dismiss()
                    it.data?.movie_results?.let { it1 ->
                        val imageList = ArrayList<SlideModel>()

                        trandingrMovierAdapter = TrandingrMovierAdapter(it1, this@HomeFragment)
                        for (image in it1){
                            imageList.add(SlideModel("https://image.tmdb.org/t/p/original"+image.posterPath,image.title))
                        }

                        binding.imageSlider.setImageList(imageList,ScaleTypes.FIT)
                    }
                    binding.rvTrandingMovie.adapter = trandingrMovierAdapter
                    binding.rvTrandingMovie.isFocusable = false
                }
                is ResponseState.Error->{
                    loader?.dismiss()
                }
                else -> {

                }
            }
        }


    }

    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {

    }
}