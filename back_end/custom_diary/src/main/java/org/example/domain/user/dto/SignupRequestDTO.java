package org.example.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class SignupRequestDTO {
    @NotBlank
    private String idToken;

    @NotBlank
    private String password;

    @NotEmpty
    private List<TermAgreementDTO> terms;

    @NotBlank
    @Pattern(regexp = "[MFO]")  // 남(M), 여(F), 기타(O)
    private String gender;

    @NotNull
    private LocalDate birthDate;

    @NotEmpty
    private List<String> genreNames;

    @NotNull
    private String artStyleId;
}
