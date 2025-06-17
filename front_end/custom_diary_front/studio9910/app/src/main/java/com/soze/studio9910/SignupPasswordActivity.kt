package com.soze.studio9910

import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.soze.studio9910.utils.UIUtils
import com.soze.studio9910.utils.ValidationUtils

class SignupPasswordActivity : BaseSignupActivity() {

    private lateinit var passwordEditText: EditText
    private lateinit var passwordToggle: ImageView
    private lateinit var passwordUnderline: View

    private lateinit var condition1Icon: ImageView
    private lateinit var condition2Icon: ImageView
    private lateinit var condition3Icon: ImageView

    private lateinit var condition1Text: TextView
    private lateinit var condition2Text: TextView
    private lateinit var condition3Text: TextView

    private var isPasswordVisible = false
    private var condition1Met = false
    private var condition2Met = false
    private var condition3Met = false

    override fun getLayoutResId(): Int = R.layout.activity_signup_password

    override fun getNextActivityClass(): Class<*> = SignupPasswordConfirmActivity::class.java

    override fun isValidForNext(): Boolean = condition1Met && condition2Met && condition3Met

    override fun initSpecificViews() {
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordToggle = findViewById(R.id.passwordToggle)
        passwordUnderline = findViewById(R.id.passwordUnderline)

        condition1Icon = findViewById(R.id.condition1Icon)
        condition2Icon = findViewById(R.id.condition2Icon)
        condition3Icon = findViewById(R.id.condition3Icon)

        condition1Text = findViewById(R.id.condition1Text)
        condition2Text = findViewById(R.id.condition2Text)
        condition3Text = findViewById(R.id.condition3Text)

        passwordEditText.requestFocus()
    }

    override fun setupSpecificListeners() {
        passwordToggle.setOnClickListener { togglePasswordVisibility() }

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                validatePassword(password)
            }
        })

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            val state = if (hasFocus) UIUtils.UnderlineState.FOCUSED else UIUtils.UnderlineState.NORMAL
            UIUtils.updateUnderlineColor(this, passwordUnderline, state)
        }
    }

    override fun addSpecificExtras(intent: android.content.Intent) {
        intent.putExtra("password", passwordEditText.text.toString())
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            passwordToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            passwordToggle.setImageResource(R.drawable.ic_visibility)
        }
        passwordEditText.setSelection(passwordEditText.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun validatePassword(password: String) {
        condition1Met = ValidationUtils.isValidPasswordLength(password)
        condition2Met = ValidationUtils.hasSpecialCharAndLowerCase(password)
        condition3Met = ValidationUtils.hasNoSpaces(password)

        UIUtils.updateConditionUI(this, condition1Icon, condition1Text, condition1Met)
        UIUtils.updateConditionUI(this, condition2Icon, condition2Text, condition2Met)
        UIUtils.updateConditionUI(this, condition3Icon, condition3Text, condition3Met)

        updateNextButton(isValidForNext())
    }
}
