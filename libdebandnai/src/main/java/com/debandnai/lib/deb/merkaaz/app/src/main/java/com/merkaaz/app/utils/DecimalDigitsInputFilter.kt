package com.merkaaz.app.utils

import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import com.merkaaz.app.adapter.TAG
import java.util.regex.Matcher
import java.util.regex.Pattern

class DecimalDigitsInputFilter(val maxValue:Long,val pointerLength:Int) :
/*InputFilter {
private val mPattern: Pattern
override fun filter(
    source: CharSequence,
    start: Int,
    end: Int,
    dest: Spanned,
    dstart: Int,
    dend: Int
): CharSequence? {
    val matcher: Matcher = mPattern.matcher(dest)

    var a=if (!matcher.matches()) "" else null
    Log.d(TAG, "filter: aaaa JJ "+start+" "+end+" "+dest+" "+dstart+" "+dend)
    return a
}

init {
    Log.d(TAG, ":aaaaaa digitsBeforeZero "+digitsBeforeZero)
    Log.d(TAG, ":aaaaaa digitsAfterZero "+digitsAfterZero)
    mPattern =
        Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
}*/

    InputFilter{

    /**
     * Maximum number
     *//*
    val MAX_VALUE = 100000000
    *//**
     * Number of digits after decimal point
     *//*
    val PONTINT_LENGTH = 2*/
    val  p = Pattern.compile("[0-9]*")   //Other than numbers

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
//                    val mPattern = Pattern.compile("^[0-9]+((\\.[0-9]{0,2})?)||(\\.)?")
//                    val matcher = dest?.let { mPattern.matcher(it) }
//                    if (matcher?.matches()==true) {
//                        return null
//                    }
//                    return ""

        val oldtext = dest.toString()
        println(oldtext)
        //Verify delete etc
        if ("" == source.toString()) {
            return null
        }
        //Verify non numeric or decimal points
        val m = source?.let { p.matcher(it) }
        if (oldtext.contains(".")) {
            //Only numbers can be entered when a decimal point already exists
            if (m?.matches()==false) {
                return null
            }
        } else {
            //If no decimal point is entered, you can enter decimal point and number
            if (m?.matches()==false && !source.equals(".")) {
                return null
            }
        }
        //Verify the size of the input amount
        if (source.toString() != "" && source.toString()!=".") {

            val dold = (oldtext + source.toString()).toDouble()
            if (dold > maxValue) {
                Log.d("inputfilter",
                    "The maximum amount entered cannot be greater than MAX_VALUE"
                )
                return dest!!.subSequence(dstart, dend)
            } else if (dold == maxValue.toDouble()) {
                if (source.toString().equals(".")) {
                    Log.d(
                        "inputfilter",
                        "The maximum amount entered cannot be greater than MAX_VALUE"
                    )
                    return dest!!.subSequence(dstart, dend)
                }
            }
        }
        //Verify decimal accuracy is correct
        if (oldtext.contains(".")) {
            val index = oldtext.indexOf( ".")
            val len = dend -index
            //Decimal places can only be 2
            if (len > pointerLength) {
                val newText = dest?.subSequence( dstart, dend)
                return newText
            }
        }
        return "${dest!!.subSequence(dstart, dend)}${source.toString()}"

    }

}
