package org.example.domain.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.example.domain.gallery.service.FolderService;
import org.example.domain.user.dto.*;
import org.example.domain.user.entity.*;
import org.example.domain.user.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final TermRepository termRepo;
    private final UserTermRepository userTermRepo;
    private final GenreRepository genreRepo;
    private final UserGenreRepository userGenreRepo;
    private final ArtStyleRepository artStyleRepo;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final FirebaseAuth firebaseAuth;
    private final FolderService folderService;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    // style1 ~ style6 에 대한 매핑
    private static final Map<String, String> STYLE_PROMPT_MAP = Map.of(
            "style1", "로맨틱",
            "style2", "모던",
            "style3", "클래식",
            "style4", "미니멀",
            "style5", "판타지",
            "style6", "큐트"
    );

    @Transactional
    public SignupResponseDTO registerUser(SignupRequestDTO req) {
        // 1. Firebase 토큰 검증
        FirebaseToken decodedToken;
        try {
            decodedToken = firebaseAuth.verifyIdToken(req.getIdToken());
        } catch (FirebaseAuthException e) {
            log.error("Firebase 토큰 검증 실패: {}", e.getMessage());
            throw new RuntimeException("Firebase 토큰 검증 실패", e);
        }

        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();

        if (userRepo.existsByFirebaseUid(uid)) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        // 2. 사용자 저장
        User user = new User(
                uid,
                email,
                req.getGender(),
                passwordEncoder.encode(req.getPassword()),
                req.getBirthDate()
        );
        userRepo.save(user);

        // 2-1. 기본 폴더 생성
        folderService.initializeDefaultFolders(uid);

        // 3. 약관 동의 저장
        for (TermAgreementDTO t : req.getTerms()) {
            Term term = termRepo.findByTermId(t.getTermId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 약관 ID: " + t.getTermId()));

            UserTerm ut = new UserTerm();
            UserTermKey key = new UserTermKey();
            key.setUserId(uid);
            key.setTermId(term.getId());

            ut.setId(key);
            ut.setUser(user);
            ut.setTerm(term);
            ut.setAgreed(t.isAgreed());
            ut.setCreatedAt(LocalDateTime.now());

            userTermRepo.save(ut);
        }

        // 4. 장르 선택 저장
        for (String genreName : req.getGenreNames()) {
            Genre g = genreRepo.findByName(genreName)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 장르명: " + genreName));

            UserGenre ug = new UserGenre();
            UserGenreKey k2 = new UserGenreKey();
            k2.setUserId(uid);
            k2.setGenreId(g.getId());

            ug.setId(k2);
            ug.setUser(user);
            ug.setGenre(g);
            userGenreRepo.save(ug);
        }

        // 5. 그림체 스타일 저장
        String prompt = STYLE_PROMPT_MAP.getOrDefault(req.getArtStyleId(), "로맨틱");

        ArtStyle style = new ArtStyle();
        style.setUser(user);
        style.setPrompt(prompt);
        style.setCreatedAt(LocalDateTime.now());
        artStyleRepo.save(style);

        // 6. 응답 구성
        SignupResponseDTO res = new SignupResponseDTO();
        res.setFirebaseUid(uid);
        res.setEmail(email);
        res.setGender(user.getGender());
        res.setBirthDate(user.getBirthDate());
        res.setAgreedTermCodes(
                req.getTerms().stream()
                        .filter(TermAgreementDTO::isAgreed)
                        .map(TermAgreementDTO::getTermId)
                        .collect(Collectors.toList())
        );
        res.setGenreNames(
                userGenreRepo.findByUser(user).stream()
                        .map(ug -> ug.getGenre().getName())
                        .collect(Collectors.toList())
        );

        res.setArtStyleId(style.getId());

        return res;
    }


    @Transactional(readOnly = true)
    public AuthResponseDTO loginUser(LoginRequestDTO loginDTO) {
        // Firebase 토큰 검증
        FirebaseToken decodedToken;
        try {
            decodedToken = firebaseAuth.verifyIdToken(loginDTO.getIdToken());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Firebase 토큰 검증 실패", e);
        }

        String uid = decodedToken.getUid();
        User user = userRepo.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        AuthResponseDTO res = new AuthResponseDTO();
        res.setFirebaseUid(uid);
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setPassword(user.getPassword());
        res.setBirthDate(user.getBirthDate());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }
}
