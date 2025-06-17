package org.example.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class TermAgreementDTO {
    @NotNull
    private Long termId;
    private boolean agreed;
}
