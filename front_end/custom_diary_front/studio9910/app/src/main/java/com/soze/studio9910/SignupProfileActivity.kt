package com.soze.studio9910

import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.*

class SignupProfileActivity : BaseSignupActivity() {

    private lateinit var maleButton: TextView
    private lateinit var femaleButton: TextView
    private lateinit var yearPicker: NumberPicker
    private lateinit var monthPicker: NumberPicker
    private lateinit var dayPicker: NumberPicker
    
    private var selectedGender: String? = null

    override fun getLayoutResId(): Int = R.layout.activity_signup_profile

    override fun getNextActivityClass(): Class<*> = SignupGenreActivity::class.java

    override fun isValidForNext(): Boolean = selectedGender != null

    override fun initSpecificViews() {
        maleButton = findViewById(R.id.maleButton)
        femaleButton = findViewById(R.id.femaleButton)
        yearPicker = findViewById(R.id.yearPicker)
        monthPicker = findViewById(R.id.monthPicker)
        dayPicker = findViewById(R.id.dayPicker)
        
        setupNumberPickers()
    }

    override fun setupSpecificListeners() {
        // 성별 선택 버튼
        maleButton.setOnClickListener { selectGender("male") }
        femaleButton.setOnClickListener { selectGender("female") }
    }

    override fun addSpecificExtras(intent: android.content.Intent) {
        val birthDate = "${yearPicker.value}-${String.format("%02d", monthPicker.value)}-${String.format("%02d", dayPicker.value)}"
        intent.putExtra("gender", selectedGender)
        intent.putExtra("birthDate", birthDate)
    }

    private fun setupNumberPickers() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        // 연도 설정 (1950년부터 현재년도까지)
        yearPicker.minValue = 1950
        yearPicker.maxValue = currentYear
        yearPicker.value = 1990
        yearPicker.wrapSelectorWheel = false
        
        // 월 설정 (1월부터 12월까지)
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = 1
        monthPicker.wrapSelectorWheel = false
        
        // 일 설정 (1일부터 31일까지, 월에 따라 조정됨)
        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = 1
        dayPicker.wrapSelectorWheel = false
        
        // NumberPicker 리스너 설정
        yearPicker.setOnValueChangedListener { _, _, newVal ->
            updateDayPicker(newVal, monthPicker.value)
        }
        
        monthPicker.setOnValueChangedListener { _, _, newVal ->
            updateDayPicker(yearPicker.value, newVal)
        }
    }
    
    private fun updateDayPicker(year: Int, month: Int) {
        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 31
        }
        
        val currentDay = dayPicker.value
        dayPicker.maxValue = daysInMonth
        
        // 현재 선택된 일이 새로운 최대값보다 크면 조정
        if (currentDay > daysInMonth) {
            dayPicker.value = daysInMonth
        }
    }
    
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
    
    private fun selectGender(gender: String) {
        selectedGender = gender
        
        when (gender) {
            "male" -> {
                maleButton.background = ContextCompat.getDrawable(this, R.drawable.button_selected_background)
                maleButton.setTextColor(ContextCompat.getColor(this, R.color.white))
                femaleButton.background = ContextCompat.getDrawable(this, R.drawable.button_unselected_background)
                femaleButton.setTextColor(ContextCompat.getColor(this, R.color.textGray))
            }
            "female" -> {
                femaleButton.background = ContextCompat.getDrawable(this, R.drawable.button_selected_background)
                femaleButton.setTextColor(ContextCompat.getColor(this, R.color.white))
                maleButton.background = ContextCompat.getDrawable(this, R.drawable.button_unselected_background)
                maleButton.setTextColor(ContextCompat.getColor(this, R.color.textGray))
            }
        }
        
        updateNextButton(isValidForNext())
    }
} 