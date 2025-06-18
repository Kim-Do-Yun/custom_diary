package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.soze.studio9910.api.LoginRequest
import com.soze.studio9910.api.RetrofitClient
import com.soze.studio9910.databinding.LoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private var isAutoLoginChecked = false
    private val firebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private val TEST_ACCOUNTS = mapOf(
            "test@example.com" to "password123",
            "user@test.com" to "test1234",
            "admin@studio9910.com" to "admin9910"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (validateLogin(email, password)) {
                saveAuthToken(email, isTest = true)
                handleLoginSuccess()
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        authResult.user?.getIdToken(true)?.addOnSuccessListener { result ->
                            val idToken = result.token ?: ""
                            sendLoginRequestToServer(idToken)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Firebase 로그인 실패", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupTermsActivity::class.java))
        }

        setupAutoLoginCheckbox()
        printTestAccounts()
    }

    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val autoLogin = prefs.getBoolean("auto_login", false)
        val token = prefs.getString("access_token", null)
        if (autoLogin && token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun sendLoginRequestToServer(idToken: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(idToken))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        saveAuthToken(body.firebaseUid, isTest = false)
                        handleLoginSuccess()
                    } else {
                        Toast.makeText(this@LoginActivity, "서버 응답 오류: 데이터 없음", Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "서버 로그인 실패", Toast.LENGTH_SHORT).show()
                    firebaseAuth.signOut()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "서버 요청 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut()
            }
        }
    }

    private fun setupAutoLoginCheckbox() {
        val autoLoginCheckIcon = findViewById<ImageView>(R.id.autoLoginCheckIcon)
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        isAutoLoginChecked = sharedPreferences.getBoolean("auto_login", false)
        updateAutoLoginIcon(autoLoginCheckIcon)
        val clickListener = {
            isAutoLoginChecked = !isAutoLoginChecked
            updateAutoLoginIcon(autoLoginCheckIcon)
            sharedPreferences.edit().putBoolean("auto_login", isAutoLoginChecked).apply()
        }
        autoLoginCheckIcon.setOnClickListener { clickListener() }
        findViewById<LinearLayout>(R.id.optionLayout).setOnClickListener { clickListener() }
    }

    private fun updateAutoLoginIcon(iconView: ImageView) {
        if (isAutoLoginChecked) {
            iconView.setImageResource(R.drawable.ic_check_circle)
            iconView.setColorFilter(resources.getColor(android.R.color.holo_orange_light, null))
        } else {
            iconView.setImageResource(R.drawable.ic_check_circle_outline)
            iconView.setColorFilter(resources.getColor(android.R.color.darker_gray, null))
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        return TEST_ACCOUNTS[email] == password
    }

    private fun saveAuthToken(token: String, isTest: Boolean) {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit().apply {
            putString("access_token", token)
            if (isTest) putString("email", token)
            putBoolean("auto_login", isAutoLoginChecked)
            apply()
        }
    }

    private fun handleLoginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun printTestAccounts() {
        println("=== 테스트 계정 정보 ===")
        TEST_ACCOUNTS.forEach { (email, password) ->
            println("이메일: $email\n비밀번호: $password")
        }
    }
}
