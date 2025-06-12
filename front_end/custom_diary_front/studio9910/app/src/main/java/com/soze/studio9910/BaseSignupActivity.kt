package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseSignupActivity : AppCompatActivity() {

    protected lateinit var backButton: ImageView
    protected lateinit var nextButton: TextView
    
    // 다음 페이지로 전달할 데이터
    protected var userEmail: String? = null
    protected var userPassword: String? = null
    protected var selectedGenres: ArrayList<String>? = null
    protected var selectedArtStyle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        
        // 이전 화면에서 전달받은 데이터 받기
        receivePreviousData()
        
        initViews()
        setupCommonListeners()
        setupSpecificListeners()
    }

    // 각 액티비티에서 레이아웃 리소스 ID를 반환
    abstract fun getLayoutResId(): Int
    
    // 각 액티비티별 특정 리스너 설정
    abstract fun setupSpecificListeners()
    
    // 다음 페이지 이동 조건 체크
    abstract fun isValidForNext(): Boolean
    
    // 다음 페이지 클래스 반환
    abstract fun getNextActivityClass(): Class<*>?

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        nextButton = findViewById(R.id.nextButton)
        
        // 각 액티비티별 추가 뷰 초기화
        initSpecificViews()
    }
    
    // 각 액티비티별 뷰 초기화
    open fun initSpecificViews() {}

    private fun setupCommonListeners() {
        // 뒤로가기 버튼
        backButton.setOnClickListener { finish() }
        
        // 다음 버튼 클릭 감지
        nextButton.setOnClickListener {
            if (isValidForNext()) {
                goToNextStep()
            }
        }
    }

    private fun receivePreviousData() {
        userEmail = intent.getStringExtra("email")
        userPassword = intent.getStringExtra("password")
        selectedGenres = intent.getStringArrayListExtra("selectedGenres")
        selectedArtStyle = intent.getStringExtra("selectedArtStyle")
    }

    protected fun updateNextButton(isEnabled: Boolean) {
        if (isEnabled) {
            nextButton.background = ContextCompat.getDrawable(this, R.drawable.button_enabled_background)
            nextButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            nextButton.isClickable = true
            nextButton.isFocusable = true
        } else {
            nextButton.background = ContextCompat.getDrawable(this, R.drawable.button_disabled_background)
            nextButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            nextButton.isClickable = false
            nextButton.isFocusable = false
        }
    }

    private fun goToNextStep() {
        val nextClass = getNextActivityClass()
        
        if (nextClass != null) {
            val intent = Intent(this, nextClass).apply {
                // 각 액티비티별 추가 데이터를 먼저 설정
                addSpecificExtras(this)
                
                // 공통 데이터 설정 (위에서 설정된 값이 있으면 덮어쓰지 않도록 조건부로)
                if (!hasExtra("email")) putExtra("email", userEmail)
                if (!hasExtra("password")) putExtra("password", userPassword)
                if (!hasExtra("selectedGenres")) putStringArrayListExtra("selectedGenres", selectedGenres)
                if (!hasExtra("selectedArtStyle")) putExtra("selectedArtStyle", selectedArtStyle)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
    
    // 각 액티비티별 추가 인텐트 데이터 설정
    open fun addSpecificExtras(intent: Intent) {}

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
} 