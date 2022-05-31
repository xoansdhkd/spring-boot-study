package com.example.test.web.controller;

import com.example.test.service.AccountsService;
import com.example.test.web.dto.ResponseDto;
import com.example.test.web.dto.accounts.AccountsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountsController {

    private final AccountsService accountsService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseDto signup(@RequestBody AccountsRequestDto req) {
        return accountsService.signup(req);
    }

    @PostMapping("/signin")
    public ResponseDto signin(@RequestBody AccountsRequestDto req) {
        return accountsService.signin(req);
    }

}
