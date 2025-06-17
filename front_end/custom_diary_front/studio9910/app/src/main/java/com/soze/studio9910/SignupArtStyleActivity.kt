package com.soze.studio9910

import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.soze.studio9910.api.SignupRequest
import com.soze.studio9910.api.RetrofitClient
import com.soze.studio9910.utils.UIUtils
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.soze.studio9910.api.TermAgreementDTO

class SignupArtStyleActivity : BaseSignupActivity() {

    private var currentSelectedArtStyle: String? = null

    // 아트 스타일 이미지뷰들
    private lateinit var artStyle1: ImageView
    private lateinit var artStyle2: ImageView
    private lateinit var artStyle3: ImageView
    private lateinit var artStyle4: ImageView
    private lateinit var artStyle5: ImageView
    private lateinit var artStyle6: ImageView

    override fun getLayoutResId(): Int = R.layout.activity_signup_art_style
    override fun getNextActivityClass(): Class<*>? = null // 마지막 페이지이므로 특별 처리
    override fun isValidForNext(): Boolean = currentSelectedArtStyle != null

    override fun initSpecificViews() {
        artStyle1 = findViewById(R.id.artStyle1)
        artStyle2 = findViewById(R.id.artStyle2)
        artStyle3 = findViewById(R.id.artStyle3)
        artStyle4 = findViewById(R.id.artStyle4)
        artStyle5 = findViewById(R.id.artStyle5)
        artStyle6 = findViewById(R.id.artStyle6)
    }

    override fun setupSpecificListeners() {
        // 다음 버튼을 회원가입 완료로 변경하고 특별 처리
        nextButton.text = "회원가입 완료"
        nextButton.setOnClickListener {
            if (!isValidForNext()) return@setOnClickListener

            lifecycleScope.launch {
                Log.d("SignupDebug", "Firebase 회원 생성 시작")
                nextButton.isEnabled = false
                nextButton.text = "가입 중..."

                try {
                    val authResult = FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(userEmail!!, userPassword!!)
                        .await()
                    Log.d("SignupDebug", "Firebase 회원 생성 성공")

                    val firebaseUser = authResult.user
                    val idToken = firebaseUser?.getIdToken(true)?.await()?.token

                    Log.d("SignupDebug", "Firebase ID Token: $idToken")
                    Log.d("SignupDebug", "userEmail: $userEmail")
                    Log.d("SignupDebug", "userPassword: $userPassword")
                    Log.d("SignupDebug", "selectedGender: $selectedGender")
                    Log.d("SignupDebug", "selectedBirthDate: $selectedBirthDate")
                    Log.d("SignupDebug", "selectedGenres: $selectedGenres")
                    Log.d("SignupDebug", "currentSelectedArtStyle: $currentSelectedArtStyle")
                    Log.d("SignupDebug", "termAgreementMap: $termAgreementMap")

                    if (idToken == null ||
                        userPassword == null ||
                        selectedGender == null ||
                        selectedBirthDate == null ||
                        currentSelectedArtStyle == null ||
                        userEmail == null ||
                        selectedGenres == null ||
                        termAgreementMap == null
                    ) {
                        Log.e("SignupDebug", "필수 데이터 누락 - null 발생")
                        Toast.makeText(this@SignupArtStyleActivity, "입력 정보가 누락되었습니다.", Toast.LENGTH_LONG).show()
                        nextButton.isEnabled = true
                        nextButton.text = "회원가입 완료"
                        return@launch
                    }

                    val termAgreementList = termAgreementMap?.map { (termIdStr, agreed) ->
                        TermAgreementDTO(termIdStr.toLong(), agreed)
                    } ?: emptyList()
                    Log.d("SignupDebug", "TermAgreement 변환 완료: $termAgreementList")

                    val req = SignupRequest(
                        idToken = idToken,
                        password = userPassword!!,
                        terms = termAgreementList,
                        gender = selectedGender!!,
                        birthDate = selectedBirthDate!!,
                        genreNames = selectedGenres!!,
                        artStyleId = currentSelectedArtStyle!!
                    )
                    Log.d("SignupDebug", "최종 요청 생성됨: $req")

                    val response = RetrofitClient.apiService.signup(req)
                    Log.d("SignupDebug", "응답 코드: ${response.code()}")
                    Log.d("SignupDebug", "응답 body: ${response.body()}")
                    Log.d("SignupDebug", "응답 errorBody: ${response.errorBody()?.string()}")

                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        goToMainPage()
                    } else {
                        val code = response.code()
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(
                            this@SignupArtStyleActivity,
                            "회원가입 실패: HTTP $code\n${errorBody ?: "no content"}",
                            Toast.LENGTH_LONG
                        ).show()
                        nextButton.isEnabled = true
                        nextButton.text = "회원가입 완료"
                    }

                } catch (e: Exception) {
                    Log.e("SignupDebug", "예외 발생: ${e.message}", e)
                    Toast.makeText(
                        this@SignupArtStyleActivity,
                        "네트워크 에러: ${e.message ?: e.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                    nextButton.isEnabled = true
                    nextButton.text = "회원가입 완료"
                }
            }
        }

        // 아트 스타일 이미지 클릭 리스너
        artStyle1.setOnClickListener { selectArtStyle("style1", artStyle1) }
        artStyle2.setOnClickListener { selectArtStyle("style2", artStyle2) }
        artStyle3.setOnClickListener { selectArtStyle("style3", artStyle3) }
        artStyle4.setOnClickListener { selectArtStyle("style4", artStyle4) }
        artStyle5.setOnClickListener { selectArtStyle("style5", artStyle5) }
        artStyle6.setOnClickListener { selectArtStyle("style6", artStyle6) }
    }

    override fun addSpecificExtras(intent: android.content.Intent) {
        intent.putExtra("selectedArtStyle", currentSelectedArtStyle)
    }

    private fun selectArtStyle(styleId: String, selectedImageView: ImageView) {
        resetAllArtStyles()

        currentSelectedArtStyle = styleId
        selectedArtStyle = styleId // ✅ BaseSignupActivity에 저장

        UIUtils.updateSelectableItemBackground(
            this,
            selectedImageView,
            true,
            R.drawable.art_style_selected,
            R.drawable.art_style_unselected
        )
        updateNextButton(isValidForNext())
    }

    private fun resetAllArtStyles() {
        listOf(artStyle1, artStyle2, artStyle3, artStyle4, artStyle5, artStyle6)
            .forEach { artStyle ->
                UIUtils.updateSelectableItemBackground(
                    this,
                    artStyle,
                    false,
                    R.drawable.art_style_selected,
                    R.drawable.art_style_unselected
                )
            }
    }

    private fun goToMainPage() {
        val intent = android.content.Intent(this, MainPageActivity::class.java).apply {
            putExtra("email", userEmail)
            putExtra("password", userPassword)
            putStringArrayListExtra("selectedGenres", ArrayList(selectedGenres)) // ✅ 변환
            putExtra("selectedArtStyle", currentSelectedArtStyle)
        }
        startActivity(intent)
        finishAffinity()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
