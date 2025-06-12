package com.soze.studio9910

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class SignupTermsActivity : AppCompatActivity() {

    private var allChecked = false
    private var requiredChecked = false
    private var optionalChecked = false
    private var requiredItemsChecked = mutableListOf(false, false, false)
    private var optionalItemsChecked = mutableListOf(false, false, false)

    // 필수 약관 텍스트
    private val requiredTerms = listOf(
        "서비스 이용약관 동의 (필수)",
        "개인정보 수집 및 이용 동의 (필수)",
        "위치기반 서비스 이용약관 동의 (필수)"
    )

    // 선택 약관 텍스트
    private val optionalTerms = listOf(
        "마케팅 정보 수신 동의 (선택)",
        "이벤트 및 프로모션 알림 동의 (선택)",
        "제3자 정보 제공 동의 (선택)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_terms)

        // 툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // 모든 체크 아이콘과 텍스트 찾기
        val allAgreeCheck = findViewById<ImageView>(R.id.allAgreeCheck)
        val requiredCheck = findViewById<ImageView>(R.id.requiredCheck)
        val optionalCheck = findViewById<ImageView>(R.id.optionalCheck)
        val nextButton = findViewById<MaterialButton>(R.id.nextButton)

        val allAgreeText = findViewById<TextView>(R.id.allAgreeText)
        val requiredText = findViewById<TextView>(R.id.requiredTitle)
        val optionalText = findViewById<TextView>(R.id.optionalTitle)

        // 하위 약관 텍스트 설정 및 초기 상태 설정
        for (i in 0..2) {
            val requiredItem = findViewById<View>(resources.getIdentifier("required_item_${i+1}", "id", packageName))
            val optionalItem = findViewById<View>(resources.getIdentifier("optional_item_${i+1}", "id", packageName))

            requiredItem?.findViewById<TextView>(R.id.termsText)?.text = requiredTerms[i]
            optionalItem?.findViewById<TextView>(R.id.termsText)?.text = optionalTerms[i]
            
            // 초기 체크 상태 설정
            requiredItem?.findViewById<ImageView>(R.id.checkIcon)?.let { 
                updateCheckIcon(it, requiredItemsChecked[i])
            }
            optionalItem?.findViewById<ImageView>(R.id.checkIcon)?.let { 
                updateCheckIcon(it, optionalItemsChecked[i])
            }
        }

        // 초기 상태 설정
        updateCheckIcon(allAgreeCheck, allChecked)
        updateCheckIcon(requiredCheck, requiredChecked)
        updateCheckIcon(optionalCheck, optionalChecked)
        updateNextButton()

        // 전체 동의 클릭 리스너
        val allAgreeClickListener = View.OnClickListener {
            allChecked = !allChecked
            updateCheckIcon(allAgreeCheck, allChecked)
            
            // 모든 하위 항목 상태 동기화
            requiredChecked = allChecked
            optionalChecked = allChecked
            requiredItemsChecked = MutableList(3) { allChecked }
            optionalItemsChecked = MutableList(3) { allChecked }
            
            // 체크박스 상태 업데이트
            updateCheckIcon(requiredCheck, requiredChecked)
            updateCheckIcon(optionalCheck, optionalChecked)
            
            // 하위 항목들의 체크박스 상태 업데이트
            for (i in 0..2) {
                val requiredItem = findViewById<View>(resources.getIdentifier("required_item_${i+1}", "id", packageName))
                val optionalItem = findViewById<View>(resources.getIdentifier("optional_item_${i+1}", "id", packageName))
                
                requiredItem?.findViewById<ImageView>(R.id.checkIcon)?.let { 
                    updateCheckIcon(it, allChecked)
                }
                optionalItem?.findViewById<ImageView>(R.id.checkIcon)?.let { 
                    updateCheckIcon(it, allChecked)
                }
            }
            
            updateNextButton()
        }

        // 필수 동의 클릭 리스너
        val requiredClickListener = View.OnClickListener {
            requiredChecked = !requiredChecked
            updateCheckIcon(requiredCheck, requiredChecked)
            
            // 필수 하위 항목 상태 동기화
            requiredItemsChecked = MutableList(3) { requiredChecked }
            
            // 하위 항목들의 체크박스 상태 업데이트
            for (i in 0..2) {
                val requiredItem = findViewById<View>(resources.getIdentifier("required_item_${i+1}", "id", packageName))
                requiredItem?.findViewById<ImageView>(R.id.checkIcon)?.let { 
                    updateCheckIcon(it, requiredChecked)
                }
            }
            
            // 전체 동의 상태 업데이트
            updateAllAgreeState()
            updateNextButton()
        }

        // 선택 동의 클릭 리스너
        val optionalClickListener = View.OnClickListener {
            optionalChecked = !optionalChecked
            updateCheckIcon(optionalCheck, optionalChecked)
            
            // 선택 하위 항목 상태 동기화
            optionalItemsChecked = MutableList(3) { optionalChecked }
            
            // 하위 항목들의 체크박스 상태 업데이트
            for (i in 0..2) {
                val optionalItem = findViewById<View>(resources.getIdentifier("optional_item_${i+1}", "id", packageName))
                optionalItem?.findViewById<ImageView>(R.id.checkIcon)?.let { 
                    updateCheckIcon(it, optionalChecked)
                }
            }
            
            // 전체 동의 상태 업데이트
            updateAllAgreeState()
        }

        // 클릭 리스너 설정
        allAgreeCheck.setOnClickListener(allAgreeClickListener)
        allAgreeText.setOnClickListener(allAgreeClickListener)
        
        requiredCheck.setOnClickListener(requiredClickListener)
        requiredText.setOnClickListener(requiredClickListener)
        
        optionalCheck.setOnClickListener(optionalClickListener)
        optionalText.setOnClickListener(optionalClickListener)

        // 하위 항목 클릭 리스너 설정
        for (i in 0..2) {
            val requiredItem = findViewById<View>(resources.getIdentifier("required_item_${i+1}", "id", packageName))
            val optionalItem = findViewById<View>(resources.getIdentifier("optional_item_${i+1}", "id", packageName))

            val requiredItemCheck = requiredItem?.findViewById<ImageView>(R.id.checkIcon)
            val requiredItemText = requiredItem?.findViewById<TextView>(R.id.termsText)
            val optionalItemCheck = optionalItem?.findViewById<ImageView>(R.id.checkIcon)
            val optionalItemText = optionalItem?.findViewById<TextView>(R.id.termsText)

            val requiredListener = View.OnClickListener {
                requiredItemsChecked[i] = !requiredItemsChecked[i]
                updateCheckIcon(requiredItemCheck!!, requiredItemsChecked[i])
                
                // 필수 동의 상태 업데이트
                requiredChecked = requiredItemsChecked.all { it }
                updateCheckIcon(requiredCheck, requiredChecked)
                
                // 전체 동의 상태 업데이트
                updateAllAgreeState()
                updateNextButton()
            }
            requiredItemCheck?.setOnClickListener(requiredListener)
            requiredItemText?.setOnClickListener(requiredListener)

            val optionalListener = View.OnClickListener {
                optionalItemsChecked[i] = !optionalItemsChecked[i]
                updateCheckIcon(optionalItemCheck!!, optionalItemsChecked[i])
                
                // 선택 동의 상태 업데이트
                optionalChecked = optionalItemsChecked.all { it }
                updateCheckIcon(optionalCheck, optionalChecked)
                
                // 전체 동의 상태 업데이트
                updateAllAgreeState()
            }
            optionalItemCheck?.setOnClickListener(optionalListener)
            optionalItemText?.setOnClickListener(optionalListener)
        }

        // 다음 버튼 클릭 리스너
        nextButton.setOnClickListener {
            if (requiredItemsChecked.all { it }) {
                // 다음 화면으로 이동 (이메일 입력 화면)
                val intent = Intent(this, SignupEmailActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private fun updateAllAgreeState() {
        allChecked = requiredItemsChecked.all { it } && optionalItemsChecked.all { it }
        val allAgreeCheck = findViewById<ImageView>(R.id.allAgreeCheck)
        updateCheckIcon(allAgreeCheck, allChecked)
    }

    private fun updateCheckIcon(imageView: ImageView, isChecked: Boolean) {
        imageView.setColorFilter(
            ContextCompat.getColor(
                this,
                if (isChecked) R.color.checked_color else R.color.unchecked_color
            )
        )
    }

    private fun updateNextButton() {
        val nextButton = findViewById<MaterialButton>(R.id.nextButton)
        val allRequiredChecked = requiredItemsChecked.all { it }
        
        if (allRequiredChecked) {
            nextButton.isEnabled = true
            nextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.checked_color))
        } else {
            nextButton.isEnabled = false
            nextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.unchecked_color))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
} 