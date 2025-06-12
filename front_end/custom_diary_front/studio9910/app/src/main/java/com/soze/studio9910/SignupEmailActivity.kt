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

        // EditText에 포커스 주기
        emailEditText.requestFocus()
    }

    override fun setupSpecificListeners() {
        // 이메일 입력 감지
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                
                // 이전 체크 작업 취소
                validationRunnable?.let { handler.removeCallbacks(it) }
                
                // 이메일이 변경되면 상태 초기화
                if (email != currentEmail) {
                    isEmailAvailable = false
                    currentEmail = email
                }
                
                validateEmail(email)
            }
        })

        // EditText 포커스 변경 시 언더라인 색상 변경
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
                updateNextButton(false) // 서버 검증 대기 중
                
                // 500ms 후 중복 체크 실행
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
        // 서버 응답 시뮬레이션
        handler.postDelayed({
            loadingIndicator.visibility = View.GONE
            
            if (email == currentEmail) { // 현재 입력된 이메일과 같을 때만 처리
                if (ValidationUtils.checkEmailAvailability(email)) {
                    // 사용 가능한 이메일
                    isEmailAvailable = true
                    emailValidIcon.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                    UIUtils.updateUnderlineColor(this, emailUnderline, UIUtils.UnderlineState.FOCUSED)
                    isEmailValid = true
                } else {
                    // 이미 사용중인 이메일
                    isEmailAvailable = false
                    emailValidIcon.visibility = View.GONE
                    errorText.text = "이미 사용중인 이메일입니다"
                    errorText.visibility = View.VISIBLE
                    UIUtils.updateUnderlineColor(this, emailUnderline, UIUtils.UnderlineState.ERROR)
                    isEmailValid = false
                }
                updateNextButton(isValidForNext())
            }
        }, 1000) // 서버 응답 시뮬레이션을 위한 1초 딜레이
    }

    override fun onDestroy() {
        super.onDestroy()
        // 핸들러 콜백 제거
        validationRunnable?.let { handler.removeCallbacks(it) }
    }
} 