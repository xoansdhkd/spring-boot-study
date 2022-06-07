package com.example.test.config.security.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.test.config.security.jwt.JwtTokenProvider;
import com.example.test.domain.auth.Auth;
import com.example.test.domain.auth.AuthRepository;
import com.example.test.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class ApiLoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private AuthRepository authRepository;
    private JwtTokenProvider jwtTokenProvider;

    public ApiLoginFilter(JwtTokenProvider jwtTokenProvider, AuthRepository authRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authRepository = authRepository;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("--------------- ApiLoginFilter ---------------");
        ObjectMapper om = new ObjectMapper();
        User user = null;
        try {
            user = om.readValue(request.getInputStream(), User.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String username = user.getUsername();
        String password = user.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = getAuthenticationManager().authenticate(authenticationToken);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨 : 인증이 완료되었다!");

        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();


        Auth findAuth = authRepository.findByUsername(username);
        String refreshToken = "";
        // 리프레시 토큰이 데이터베이스 없다면
        // 새로 리프레시 토큰 생성
        if (findAuth == null) {
            refreshToken = jwtTokenProvider.createRefreshToken(username);
            Auth auth = Auth.builder()
                    .username(username)
                    .token(refreshToken)
                    .build();

            authRepository.save(auth);
        }
        else {
            // DB에 리프레시 토큰이 있다면 가져오기
            refreshToken = findAuth.getToken();

            // 만료된 리프레시 토큰이면 새거 만들기
            if (!jwtTokenProvider.isValidRefreshToken(refreshToken)) {
                refreshToken = jwtTokenProvider.createRefreshToken(username);
                // 토큰 레포지토리에서 업데이트?
                Auth auth = authRepository.findByUsername(username);
                authRepository.delete(auth);
            }

        }

        // 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(username);
        // 전달
        response.setHeader(jwtTokenProvider.getACCESS_TOKEN_HEADER(), jwtTokenProvider.getACCESS_TOKEN_PREFIX() + accessToken);
        response.setHeader(jwtTokenProvider.getREFRESH_TOKEN_HEADER(), jwtTokenProvider.getREFRESH_TOKEN_PREFIX() + refreshToken);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("unsuccessfulAuthentication 실행됨 : 인증이 실패하였다!");
    }
}
