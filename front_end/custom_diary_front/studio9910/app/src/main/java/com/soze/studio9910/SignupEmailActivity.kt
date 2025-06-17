package com.soze.studio9910

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.soze.studio9910.utils.UIUtils
import com.soze.studio9910.utils.ValidationUtils

class SignupEmailActivity : BaseSignupActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var emailValidIcon: ImageView
    private lateinit var errorText: TextView
    private lateinit var emailUnderline: View
    private lateinit var loadingIndicator: ProgressBar

    private var isEmailValid = false
    private val handler = Handler(Looper.getMainLooper())
    private var validationRunnable: Runnable? = null

    private var isEmailAvailable = false
    private var currentEmail = ""

    override fun getLayoutResId(): Int = R.layout.activity_signup_email

    override fun getNextActivityClass(): Class<*> = SignupPasswordActivity::class.java

    override fun isValidForNext(): Boolean = isEmailValid && isEmailAvailable && emailEditText.text.isNotEmpty()

    override fun initSpecificViews() {
        emailEditText = findViewById(R.id.emailEditText)
        emailValidIcon = findViewById(R.id.emailValidIcon)
        errorText = findViewById(R.id.errorText)
        emailUnderline = findViewById(R.id.emailUnderline)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        emailEditText.requestFocus()
    }

    override fun setupSpecificListeners() {
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()

                validationRunnable?.let { handler.removeCallbacks(it) }

                if (email != currentEmail) {
                    isEmailAvailable = false
                    currentEmail = email
                }

                validateEmail(email)
            }
        })

        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && errorText.visibility != View.VISIBLE) {
                UIUtils.updateUnderlineColor(this@SignupEmailActivity, emailUnderline, UIUtils.UnderlineState.FOCUSED)
            } else if (errorText.visibility != View.VISIBLE) {
                UIUtils.updateUnderlineColor(this@SignupEmailActivity, emailUnderline, UIUtils.UnderlineState.NORMAL)
            }
        }
    }

    override fun addSpecificExtras(intent: android.content.Intent) {
        intent.putExtra("email", currentEmail)
    }

    private fun validateEmail(email: String) {
        when {
            email.isEmpty() -> {
                resetEmailValidationUI()
                isEmailValid = false
                updateNextButton(false)
            }
            ValidationUtils.isValidEmail(email) -> {
                showEmailValidationLoading()
                isEmailValid = true
                updateNextButton(false)

                validationRunnable = Runnable {
                    checkEmailAvailability(email)
                }
                handler.postDelayed(validationRunnable!!, 500)
            }
            else -> {
                showEmailValidationError(email)
                isEmailValid = false
                updateNextButton(false)
            }
        }
    }

    private fun resetEmailValidationUI() {
        emailValidIcon.visibility = View.GONE
        loadingIndicator.visibility = View.GONE
        errorText.visibility = View.GONE
        UIUtils.updateUnderlineColor(this, emailUnderline, UIUtils.UnderlineState.NORMAL)
    }

    private fun showEmailValidationLoading() {
        emailValidIcon.visibility = View.GONE
        loadingIndicator.visibility = View.VISIBLE
        errorText.visibility = View.GONE
        UIUtils.updateUnderlineColor(this, emailUnderline, UIUtils.UnderlineState.FOCUSED)
    }

    private fun showEmailValidationError(email: String) {
        emailValidIcon.visibility = View.GONE
        loadingIndicator.visibility = View.GONE
        if (email.contains("@")) {
            errorText.text = "올바른 이메일 주소를 입력해주세요"
            errorText.visibility = View.VISIBLE
            UIUtils.updateUnderlineColor(this, emailUnderline, UIUtils.UnderlineState.ERROR)
        } else {
            errorText.visibility = View.GONE
            UIUtils.updateUnderlineColor(this, emailUnderline, UIUtils.UnderlineState.NORMAL)
        }
    }

    private fun checkEmailAvailability(email: String) {
        handler.postDelayed({
            loadingIndicator.visibility = View.GONE

            if (email == currentEmail) {
                if (ValidationUtils.checkEmailAvailability(email)) {
                    isEmailAvailable = true
                    emailValidIcon.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                    UIUtils.updateUnderlineColor(this, emailUnderline, UIUtils.UnderlineState.FOCUSED)
                    isEmailValid = true
                } else {
                    isEmailAvailable = false
                    emailValidIcon.visibility = View.GONE
                    errorText.text = "이미 사용중인 이메일입니다"
                    errorText.visibility = View.VISIBLE
                    UIUtils.updateUnderlineColor(this, emailUnderline, UIUtils.UnderlineState.ERROR)
                    isEmailValid = false
                }
                updateNextButton(isValidForNext())
            }
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        validationRunnable?.let { handler.removeCallbacks(it) }
    }
}
