package com.soze.studio9910

import android.os.Bundle
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

class SignupPasswordConfirmActivity : BaseSignupActivity() {

    private lateinit var passwordConfirmEditText: EditText
    private lateinit var passwordToggle: ImageView
    private lateinit var errorText: TextView
    private lateinit var passwordUnderline: View

    private var isPasswordMatching = false
    private var isPasswordVisible = false

    override fun getLayoutResId(): Int = R.layout.activity_signup_password_confirm

    override fun getNextActivityClass(): Class<*> = SignupProfileActivity::class.java

    override fun isValidForNext(): Boolean =
        isPasswordMatching && passwordConfirmEditText.text.isNotEmpty()

    override fun initSpecificViews() {
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText)
        passwordToggle = findViewById(R.id.passwordToggle)
        errorText = findViewById(R.id.errorMessage)
        passwordUnderline = findViewById(R.id.passwordUnderline)

        passwordConfirmEditText.requestFocus()
    }

    override fun setupSpecificListeners() {
        passwordToggle.setOnClickListener { togglePasswordVisibility() }

        passwordConfirmEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePasswordMatch(s.toString())
            }
        })

        passwordConfirmEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && errorText.visibility != View.VISIBLE) {
                UIUtils.updateUnderlineColor(
                    this,
                    passwordUnderline,
                    if (isPasswordMatching && passwordConfirmEditText.text.isNotEmpty())
                        UIUtils.UnderlineState.SUCCESS
                    else
                        UIUtils.UnderlineState.FOCUSED
                )
            } else if (errorText.visibility != View.VISIBLE) {
                UIUtils.updateUnderlineColor(
                    this,
                    passwordUnderline,
                    if (isPasswordMatching && passwordConfirmEditText.text.isNotEmpty())
                        UIUtils.UnderlineState.SUCCESS
                    else
                        UIUtils.UnderlineState.NORMAL
                )
            }
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordConfirmEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            passwordToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            passwordConfirmEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            passwordToggle.setImageResource(R.drawable.ic_visibility)
        }
        passwordConfirmEditText.setSelection(passwordConfirmEditText.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun validatePasswordMatch(confirmPassword: String) {
        val originalPassword = userPassword.orEmpty()

        when {
            confirmPassword.isEmpty() -> {
                errorText.visibility = View.GONE
                UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.NORMAL)
                isPasswordMatching = false
                updateNextButton(false)
            }

            ValidationUtils.doPasswordsMatch(originalPassword, confirmPassword) -> {
                errorText.visibility = View.GONE
                UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.SUCCESS)
                isPasswordMatching = true
                updateNextButton(true)
            }

            else -> {
                errorText.text = "비밀번호가 일치하지 않습니다"
                errorText.visibility = View.VISIBLE
                UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.ERROR)
                isPasswordMatching = false
                updateNextButton(false)
            }
        }
    }

    override fun addSpecificExtras(intent: android.content.Intent) {
        // password는 이미 이전 단계에서 받아온 값이므로 재전달만 해줌
        intent.putExtra("password", userPassword)
    }
}
