package com.soze.studio9910

import android.widget.TextView
import android.widget.Toast
import com.soze.studio9910.utils.UIUtils

class SignupGenreActivity : BaseSignupActivity() {

    private lateinit var selectedCountText: TextView
    
    private val currentSelectedGenres = mutableSetOf<String>()
    private val maxSelection = 5

    override fun getLayoutResId(): Int = R.layout.activity_signup_genre

    override fun getNextActivityClass(): Class<*> = SignupArtStyleActivity::class.java

    override fun isValidForNext(): Boolean = currentSelectedGenres.isNotEmpty()

    override fun initSpecificViews() {
        selectedCountText = findViewById(R.id.selectedCountText)
        updateSelectedCount()
    }

    override fun setupSpecificListeners() {
        // 장르 버튼들 설정
        setupGenreButtons()
    }

    override fun addSpecificExtras(intent: android.content.Intent) {
        intent.putStringArrayListExtra("selectedGenres", ArrayList(currentSelectedGenres))
    }
    
    private fun setupGenreButtons() {
        // 로맨스 카테고리
        val romanceGenres = listOf(
            R.id.genre_romance, R.id.genre_pure, R.id.genre_romance_fantasy,
            R.id.genre_sweet, R.id.genre_campus, R.id.genre_office,
            R.id.genre_age_gap, R.id.genre_reunion, R.id.genre_first_love, R.id.genre_contract
        )
        
        // 액션/판타지 카테고리
        val actionGenres = listOf(
            R.id.genre_action, R.id.genre_fantasy, R.id.genre_martial_arts,
            R.id.genre_sf, R.id.genre_adventure, R.id.genre_ability,
            R.id.genre_magic, R.id.genre_isekai, R.id.genre_regression, R.id.genre_reincarnation
        )
        
        // 드라마/일상 카테고리
        val dramaGenres = listOf(
            R.id.genre_drama, R.id.genre_daily, R.id.genre_healing,
            R.id.genre_growth, R.id.genre_family, R.id.genre_friendship,
            R.id.genre_school, R.id.genre_youth, R.id.genre_touching, R.id.genre_human
        )
        
        // 스릴러/공포 카테고리
        val thrillerGenres = listOf(
            R.id.genre_thriller, R.id.genre_horror, R.id.genre_mystery,
            R.id.genre_suspense, R.id.genre_crime, R.id.genre_psychology,
            R.id.genre_revenge, R.id.genre_survival, R.id.genre_zombie, R.id.genre_detective
        )
        
        // 모든 장르 버튼에 클릭 리스너 설정
        val allGenres = romanceGenres + actionGenres + dramaGenres + thrillerGenres
        
        allGenres.forEach { genreId ->
            findViewById<TextView>(genreId).setOnClickListener { view ->
                toggleGenreSelection(view as TextView)
            }
        }
    }
    
    private fun toggleGenreSelection(genreButton: TextView) {
        val genreText = genreButton.text.toString()
        
        if (currentSelectedGenres.contains(genreText)) {
            // 선택 해제
            currentSelectedGenres.remove(genreText)
            UIUtils.updateSelectableItemBackground(this, genreButton, false, 
                R.drawable.genre_button_selected, R.drawable.genre_button_unselected)
            UIUtils.updateSelectableItemTextColor(this, genreButton, false)
        } else {
            // 선택 (최대 5개까지)
            if (currentSelectedGenres.size < maxSelection) {
                currentSelectedGenres.add(genreText)
                UIUtils.updateSelectableItemBackground(this, genreButton, true,
                    R.drawable.genre_button_selected, R.drawable.genre_button_unselected)
                UIUtils.updateSelectableItemTextColor(this, genreButton, true)
            } else {
                Toast.makeText(this, "최대 ${maxSelection}개까지 선택할 수 있습니다", Toast.LENGTH_SHORT).show()
            }
        }
        
        updateSelectedCount()
        updateNextButton(isValidForNext())
    }
    
    private fun updateSelectedCount() {
        selectedCountText.text = "${currentSelectedGenres.size}/$maxSelection"
    }
} 