package com.example.test.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseDto {

    // res 샘플
    private Integer resCode;
    private String message;

    @Builder
    public ResponseDto(Integer resCode, String message) {
        this.resCode = resCode;
        this.message = message;
    }
}
