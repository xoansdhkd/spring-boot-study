package com.example.test.web;

import com.example.test.service.AccountsService;
import com.example.test.web.dto.accounts.SignupRequestDto;
import com.example.test.web.dto.accounts.SignupResponseDto;
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
    public SignupResponseDto save(@RequestBody SignupRequestDto req) {
        return accountsService.signup(req);
    }
}
