package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.ADD_ANOTHER_STUDENT
import ie.healthylunch.app.utils.Constants.Companion.USER_NAME

class AddStudentSuccessViewModel : ViewModel() {
    var studentName: MutableLiveData<String> = MutableLiveData("")


    fun addAnotherStudentClick(activity: Activity) {
        activity as RegistrationActivity
        ADD_ANOTHER_STUDENT = true
        Constants.HAS_STUDENT_ADDED = true
        activity.startActivity(
            Intent(activity, RegistrationActivity::class.java)
                .putExtra("for", "add_student")
                .putExtra("email", USER_NAME)
        )
    }

    fun placeOrderClick(view: View) {
        ADD_ANOTHER_STUDENT = false
        Constants.HAS_STUDENT_ADDED = true
        val intent = Intent(view.context, DashBoardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        view.context.startActivity(intent)
    }
}