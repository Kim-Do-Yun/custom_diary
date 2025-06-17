package org.example.domain.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.user.dto.AuthResponseDTO;
import org.example.domain.user.dto.SignupResponseDTO;
import org.example.domain.user.entity.User;
import org.example.domain.user.dto.SignupRequestDTO;
import org.example.domain.user.dto.LoginRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<SignupResponseDTO> register(
            @Valid @RequestBody SignupRequestDTO req) {
        try {
            SignupResponseDTO res = userService.registerUser(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO req) {
        try {
            AuthResponseDTO res = userService.loginUser(req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}