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

    override fun getLayoutResId(): Int = R.layout.activity_signup_profile

    override fun getNextActivityClass(): Class<*> = SignupGenreActivity::class.java

    // ✅ 생년월일도 입력 완료해야 next 버튼 활성화되도록 조정
    override fun isValidForNext(): Boolean =
        selectedGender != null && selectedBirthDate != null

    override fun initSpecificViews() {
        maleButton = findViewById(R.id.maleButton)
        femaleButton = findViewById(R.id.femaleButton)
        yearPicker = findViewById(R.id.yearPicker)
        monthPicker = findViewById(R.id.monthPicker)
        dayPicker = findViewById(R.id.dayPicker)

        setupNumberPickers()
    }

    override fun setupSpecificListeners() {
        maleButton.setOnClickListener { selectGender("male") }
        femaleButton.setOnClickListener { selectGender("female") }
    }

    override fun addSpecificExtras(intent: android.content.Intent) {
        val birthDate = "${yearPicker.value}-${String.format("%02d", monthPicker.value)}-${String.format("%02d", dayPicker.value)}"
        selectedBirthDate = birthDate  // ✅ 내부 상태에도 반영해줘야 Base 클래스에서 사용 가능
        intent.putExtra("selectedGender", selectedGender)
        intent.putExtra("selectedBirthDate", selectedBirthDate)
    }

    private fun setupNumberPickers() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        yearPicker.minValue = 1950
        yearPicker.maxValue = currentYear
        yearPicker.value = 1990
        yearPicker.wrapSelectorWheel = false

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = 1
        monthPicker.wrapSelectorWheel = false

        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = 1
        dayPicker.wrapSelectorWheel = false

        val updateBirthDate = {
            val birth = "${yearPicker.value}-${String.format("%02d", monthPicker.value)}-${String.format("%02d", dayPicker.value)}"
            selectedBirthDate = birth
            updateNextButton(isValidForNext())
        }

        yearPicker.setOnValueChangedListener { _, _, newVal ->
            updateDayPicker(newVal, monthPicker.value)
            updateBirthDate()
        }
        monthPicker.setOnValueChangedListener { _, _, newVal ->
            updateDayPicker(yearPicker.value, newVal)
            updateBirthDate()
        }
        dayPicker.setOnValueChangedListener { _, _, _ ->
            updateBirthDate()
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
        if (currentDay > daysInMonth) {
            dayPicker.value = daysInMonth
        }
    }

    private fun isLeapYear(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    private fun selectGender(gender: String) {
        selectedGender = when (gender) {
            "male" -> "M"
            "female" -> "F"
            else -> "O" // 예외 처리
        }

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
