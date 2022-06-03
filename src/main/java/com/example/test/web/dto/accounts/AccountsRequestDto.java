package com.example.test.web.dto.accounts;

import com.example.test.domain.user.Role;
import com.example.test.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountsRequestDto {

    private String username;
    private String password;
    private String name;
    private String phone;

    @Builder
    public AccountsRequestDto(String username, String password, String name, String phone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    @Builder
    public AccountsRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .name(name)
                .phone(phone)
                .role(Role.ROLE_USER)
                .build();
    }
}
