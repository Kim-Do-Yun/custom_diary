package com.soze.studio9910.utils

import android.content.Context
import android.graphics.PorterDuff
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.soze.studio9910.R

object UIUtils {
    
    /**
     * 조건부 UI 업데이트 (체크/X 아이콘과 텍스트 색상)
     */
    fun updateConditionUI(context: Context, icon: ImageView, text: TextView, isMet: Boolean) {
        if (isMet) {
            icon.setImageResource(R.drawable.ic_check)
            icon.setColorFilter(ContextCompat.getColor(context, R.color.successGreen), PorterDuff.Mode.SRC_IN)
            text.setTextColor(ContextCompat.getColor(context, R.color.successGreen))
        } else {
            icon.setImageResource(R.drawable.ic_close)
            icon.setColorFilter(ContextCompat.getColor(context, R.color.errorRed), PorterDuff.Mode.SRC_IN)
            text.setTextColor(ContextCompat.getColor(context, R.color.errorRed))
        }
    }
    
    /**
     * 언더라인 색상 업데이트
     */
    fun updateUnderlineColor(context: Context, underline: android.view.View, state: UnderlineState) {
        val colorRes = when (state) {
            UnderlineState.FOCUSED -> R.color.colorPrimary
            UnderlineState.ERROR -> R.color.errorRed
            UnderlineState.SUCCESS -> R.color.successGreen
            UnderlineState.NORMAL -> R.color.lightGray
        }
        underline.setBackgroundColor(ContextCompat.getColor(context, colorRes))
    }
    
    /**
     * 선택 가능한 아이템의 배경 업데이트 (장르, 그림체 등)
     */
    fun updateSelectableItemBackground(context: Context, view: android.view.View, isSelected: Boolean, selectedDrawableRes: Int, unselectedDrawableRes: Int) {
        val backgroundRes = if (isSelected) selectedDrawableRes else unselectedDrawableRes
        view.background = ContextCompat.getDrawable(context, backgroundRes)
    }
    
    /**
     * 선택 가능한 아이템의 텍스트 색상 업데이트
     */
    fun updateSelectableItemTextColor(context: Context, textView: TextView, isSelected: Boolean) {
        val colorRes = if (isSelected) R.color.colorPrimary else R.color.textGray
        textView.setTextColor(ContextCompat.getColor(context, colorRes))
    }
    
    enum class UnderlineState {
        FOCUSED, ERROR, SUCCESS, NORMAL
    }
} 