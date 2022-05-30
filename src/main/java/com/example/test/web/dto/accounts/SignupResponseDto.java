package com.example.test.web.dto.accounts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupResponseDto {

    private Integer resCode;
    private String message;

    @Builder
    public SignupResponseDto(Integer resCode, String message) {
        this.resCode = resCode;
        this.message = message;
    }
}
