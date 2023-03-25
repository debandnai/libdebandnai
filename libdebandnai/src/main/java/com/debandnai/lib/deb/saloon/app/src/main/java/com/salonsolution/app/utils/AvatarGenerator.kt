package com.salonsolution.app.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.res.ResourcesCompat
import com.salonsolution.app.R
import java.util.*

object AvatarGenerator {

    private fun getNameCharacters(name: String?): String {
        return if (name.isNullOrEmpty()) {
            "S"
        } else {
            val nameArray = name.split(' ')
            val char1 = nameArray[0].first()
            val char2 = if (nameArray.size > 1) nameArray.last().first() else ""
            "$char1$char2".uppercase()
        }
    }

    fun avatarImage(
        context: Context,
        width: Int,
        height: Int,
        avatarTextSizeRatio: Float?,
        name: String?,
        backgroundColor: Int? = null,
        avatarTextColor: Int? = null,
        avatarTextStyle: Int? = null,
    ): BitmapDrawable {

        val textSizeRatio = avatarTextSizeRatio ?: 0.5f
        val textStyle = avatarTextStyle.takeIf { (it ?: 0) <= 2 } ?: Typeface.NORMAL
        val font = ResourcesCompat.getFont(context, R.font.montserrat_semibold_600)
        val mTypeface = Typeface.create(font, textStyle)

        val backgroundPaint = Paint().apply {
            style = Paint.Style.FILL
            color = backgroundColor ?: Color.GRAY
        }

        val textPaint = Paint().apply {
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            color = avatarTextColor ?: Color.WHITE
            typeface = mTypeface
            textSize = textSizeRatio * width
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

//        canvas.drawRoundRect(
//            0F,
//            0F,
//            width.toFloat(),
//            height.toFloat(),
//            avatarBorderRadius,
//            avatarBorderRadius,
//            backgroundPaint
//        )
        //drawCircle
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            width / 2f,
            backgroundPaint
        )
        canvas.drawText(
            getNameCharacters(name?.trim()),
            width.toFloat() / 2,
            (canvas.height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2),
            textPaint
        )
        return BitmapDrawable(context.resources, bitmap)

    }


}