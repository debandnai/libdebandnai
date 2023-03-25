package ie.healthylunch.app.fragment.students

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.orhanobut.dialogplus.DialogPlus
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.SchoolClassListAdapter
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.StudentRepository
import ie.healthylunch.app.data.viewModel.EditStudentProfileViewModel
import ie.healthylunch.app.databinding.FragmentEditStudentProfileBinding
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.Constants

var param1: String? = null
var param2: String? = null

class EditStudentProfileFragment :
    BaseFragment<EditStudentProfileViewModel, StudentRepository>() {
    lateinit var binding: FragmentEditStudentProfileBinding

    companion object {
        @SuppressLint("StaticFieldLeak")
        var schoolClassBottomDialog: DialogPlus? = null

        @SuppressLint("StaticFieldLeak")
        var schoolClassListAdapter: SchoolClassListAdapter? = null

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_student_profile,
            container,
            false
        )

        binding.context = this
        binding.viewModel = viewModel
        viewModel.editStudentProfileFragment = this
        binding.lifecycleOwner = requireActivity()

        return binding.root


    }

    override fun onResume() {
        super.onResume()
        DashBoardActivity.dashBoardToolbar.visibility = View.GONE

    }

      
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.studentFirstName.value = arguments?.getString("studentFirstName")
        viewModel.studentLastName.value = arguments?.getString("studentLastName")
        viewModel.studentId.value = arguments?.getInt("studentId")
        viewModel.studentSchoolName.value = arguments?.getString("schoolName")
        viewModel.userType.value = arguments?.getString("userType")
        viewModel.schoolType = arguments?.getInt("schoolType")

        context?.let { context ->
            if (viewModel.schoolType == 2 && viewModel.userType.value.equals(
                    Constants.STUDENT,
                    true
                )
            )
                binding.classTv.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            else
                binding.classTv.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(context, R.drawable.wiz_select_down),
                    null
                )
        }


        //Student Details Response api call
        viewModel.studentDetailsApiCall(requireActivity())

        //get Student Details Response
        viewModel.getStudentDetailsResponse(requireActivity())

        //get Student Class List Response
        viewModel.getStudentClassListResponse(requireActivity())

        //get Save Student Details Response
        viewModel.getSaveStudentDetailsResponse(requireActivity(), binding.saveStudent)
    }


    override fun getViewModel() = EditStudentProfileViewModel::class.java
    override fun getRepository() =
        StudentRepository(remoteDataSource.buildApi(ApiInterface::class.java))

}