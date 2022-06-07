package com.example.test.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.Charset;

@Getter
@NoArgsConstructor
public class ResponseDto {

    // res 샘플
    private Integer resCode;
    private String message;
    private Object data;

    @Builder
    public ResponseDto(Integer resCode, String message, Object data) {
        this.resCode = resCode;
        this.message = message;
        this.data = data;
    }

}
