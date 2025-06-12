package com.soze.studio9910.utils

import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {
    
    /**
     * 이메일 유효성 검사
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * 비밀번호 길이 체크 (8-16자)
     */
    fun isValidPasswordLength(password: String): Boolean {
        return password.length in 8..16
    }
    
    /**
     * 비밀번호 특수문자와 소문자 포함 체크
     */
    fun hasSpecialCharAndLowerCase(password: String): Boolean {
        val hasSpecialChar = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]").matcher(password).find()
        val hasLowerCase = Pattern.compile("[a-z]").matcher(password).find()
        return hasSpecialChar && hasLowerCase
    }
    
    /**
     * 비밀번호 공백 미포함 체크
     */
    fun hasNoSpaces(password: String): Boolean {
        return !password.contains(" ")
    }
    
    /**
     * 전체 비밀번호 검증
     */
    fun isValidPassword(password: String): Boolean {
        return isValidPasswordLength(password) && 
               hasSpecialCharAndLowerCase(password) && 
               hasNoSpaces(password)
    }
    
    /**
     * 비밀번호 일치 확인
     */
    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword && password.isNotEmpty()
    }
    
    /**
     * 이메일 사용 가능 여부 체크 (서버 API 시뮬레이션)
     */
    fun checkEmailAvailability(email: String): Boolean {
        // 실제로는 서버 API를 호출해야 하지만, 여기서는 시뮬레이션
        return email != "test@example.com"
    }
} 