package com.example.test.web.dto.accounts;

import com.example.test.domain.user.Role;
import com.example.test.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    private String userName;
    private String password;
    private String name;
    private String phone;

    @Builder
    public SignupRequestDto(String userName, String password, String name, String phone) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public User toEntity() {
        return User.builder()
                .userName(userName)
                .password(password)
                .name(name)
                .phone(phone)
                .role(Role.USER)
                .build();
    }
}
