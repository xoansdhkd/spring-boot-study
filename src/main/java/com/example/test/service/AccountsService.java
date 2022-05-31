package com.example.test.service;

import com.example.test.domain.user.Role;
import com.example.test.domain.user.User;
import com.example.test.domain.user.UserRepository;
import com.example.test.web.dto.ResponseDto;
import com.example.test.web.dto.accounts.AccountsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public ResponseDto signup(AccountsRequestDto req) {
        Optional<User> user = userRepository.findByUserName(req.getUserName());
        if (!user.isEmpty()) {
            return ResponseDto.builder()
                    .resCode(0)
                    .message("이미 존재하는 회원입니다.")
                    .build();
        }

        User newUser = User.builder()
                .userName(req.getUserName())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .phone(req.getPhone())
                .role(Role.USER)
                .build();

        userRepository.save(newUser);

        return ResponseDto.builder()
                .resCode(1)
                .message("회원 가입 성공")
                .build();
    }

    @Transactional
    public ResponseDto signin(AccountsRequestDto req) {
        Optional<User> user = userRepository.findByUserName(req.getUserName());

        if(user.isEmpty()
                || !user.get().getUserName().equals(req.getUserName())
                || !passwordEncoder.matches(req.getPassword(), user.get().getPassword())) {
            return ResponseDto.builder()
                    .resCode(0)
                    .message("아이디 혹은 비밀번호를 확인해주세요.")
                    .build();
        }

        return ResponseDto.builder()
                .resCode(1)
                .message("로그인 성공")
                .build();
    }
}
