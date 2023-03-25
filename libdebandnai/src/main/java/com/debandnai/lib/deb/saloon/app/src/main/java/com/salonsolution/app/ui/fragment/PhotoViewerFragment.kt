package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.salonsolution.app.R
import com.salonsolution.app.adapter.PhotoViewAdapter
import com.salonsolution.app.databinding.FragmentPhotoViewerBinding
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoViewerFragment : BaseFragment() {
    private lateinit var binding: FragmentPhotoViewerBinding
    private lateinit var photoViewAdapter: PhotoViewAdapter
    private val navArgs: PhotoViewerFragmentArgs by navArgs()
    companion object {
        const val CURRENT_POSITION = "current_position"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_viewer, container, false)
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = false,
            isShowBottomNav = false,
            isShowToolbar = false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding){

            val animation = TransitionInflater.from(root.context).inflateTransition(
                R.transition.image_view_shared_element_transition
            )
            sharedElementEnterTransition = animation
           // sharedElementReturnTransition = animation

            photoViewAdapter = PhotoViewAdapter(navArgs.images)
            photoViewPager.apply {
                clipToPadding = false
                setPadding(10, 0, 10, 0)
                offscreenPageLimit = 2
                adapter = photoViewAdapter
                registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentPositionOnPreviousDestination(position)
                    }

                })
            }
            photoViewPager.setCurrentItem(navArgs.position,false)
            //startPostponedEnterTransition()

           /* // adjust the shared element mapping when entering
            setEnterSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(
                        names: List<String?>, sharedElements: MutableMap<String?, View?>
                    ) {

                      val holder =  (photoViewPager[0] as? RecyclerView)?.findViewHolderForAdapterPosition(dashboardViewModel.photoViewPosition.value?:0)

                        val view = holder?.itemView?:return
                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = view.findViewById(R.id.photoView)
                    }
                })*/
        }
    }

    private fun setCurrentPositionOnPreviousDestination(position: Int) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            CURRENT_POSITION,
            position
        )
    }

}