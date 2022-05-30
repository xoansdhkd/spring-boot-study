package com.example.test.service;

import com.example.test.domain.user.User;
import com.example.test.domain.user.UserRepository;
import com.example.test.web.dto.accounts.SignupRequestDto;
import com.example.test.web.dto.accounts.SignupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountsService {

    private final UserRepository userRepository;
    @Transactional
    public SignupResponseDto signup(SignupRequestDto req) {
        Optional<User> user = userRepository.findByUserName(req.getUserName());
        System.out.println(user);
        if (!user.isEmpty()) {
            return SignupResponseDto.builder()
                    .resCode(0)
                    .message("이미 존재하는 회원입니다.")
                    .build();
        }

        userRepository.save(req.toEntity());

        return SignupResponseDto.builder()
                .resCode(1)
                .message("회원 가입 성공")
                .build();
    }
}
