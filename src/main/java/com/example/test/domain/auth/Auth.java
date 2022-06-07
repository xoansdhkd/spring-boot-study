package com.example.test.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "auths")
public class Auth {

    @Id
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String token;

    @Builder
    public Auth(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
