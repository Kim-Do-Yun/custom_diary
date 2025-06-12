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

    override fun isValidForNext(): Boolean = isPasswordMatching && passwordConfirmEditText.text.isNotEmpty()

    override fun initSpecificViews() {
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText)
        passwordToggle = findViewById(R.id.passwordToggle)
        errorText = findViewById(R.id.errorMessage)
        passwordUnderline = findViewById(R.id.passwordUnderline)
        
        // EditText에 포커스 주기
        passwordConfirmEditText.requestFocus()
    }

    override fun setupSpecificListeners() {
        // 비밀번호 표시/숨기기 토글
        passwordToggle.setOnClickListener { togglePasswordVisibility() }
        
        // 비밀번호 확인 입력 감지
        passwordConfirmEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val confirmPassword = s.toString()
                validatePasswordMatch(confirmPassword)
            }
        })
        
        // EditText 포커스 변경 시 언더라인 색상 변경
        passwordConfirmEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && errorText.visibility != View.VISIBLE) {
                if (isPasswordMatching && passwordConfirmEditText.text.isNotEmpty()) {
                    UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.SUCCESS)
                } else {
                    UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.FOCUSED)
                }
            } else if (errorText.visibility != View.VISIBLE) {
                if (isPasswordMatching && passwordConfirmEditText.text.isNotEmpty()) {
                    UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.SUCCESS)
                } else {
                    UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.NORMAL)
                }
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
        val originalPassword = userPassword ?: ""
        
        when {
            confirmPassword.isEmpty() -> {
                // 비어있을 때
                errorText.visibility = View.GONE
                UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.NORMAL)
                isPasswordMatching = false
                updateNextButton(false)
            }
            ValidationUtils.doPasswordsMatch(originalPassword, confirmPassword) -> {
                // 비밀번호 일치
                errorText.visibility = View.GONE
                UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.SUCCESS)
                isPasswordMatching = true
                updateNextButton(true)
            }
            else -> {
                // 비밀번호 불일치
                errorText.text = "비밀번호가 일치하지 않습니다"
                errorText.visibility = View.VISIBLE
                UIUtils.updateUnderlineColor(this, passwordUnderline, UIUtils.UnderlineState.ERROR)
                isPasswordMatching = false
                updateNextButton(false)
            }
        }
    }
} 