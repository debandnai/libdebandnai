package com.salonsolution.app.ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private val navArgs: DatePickerFragmentArgs by navArgs()

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    companion object {
        const val SELECTED_DATE = "selected_date"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH)
//        val day =  c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), this, navArgs.year, navArgs.month, navArgs.day)
        //sets today's date as minimum date and all the past dates are disabled.
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance(Locale(SettingsUtils.getLanguage()))
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            SELECTED_DATE,
            calendar.time
        )
    }
}