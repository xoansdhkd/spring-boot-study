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
        System.out.println("successfulAuthentication ????????? : ????????? ???????????????!");

        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();


        Auth findAuth = authRepository.findByUsername(username);
        String refreshToken = "";
        // ???????????? ????????? ?????????????????? ?????????
        // ?????? ???????????? ?????? ??????
        if (findAuth == null) {
            refreshToken = jwtTokenProvider.createRefreshToken(username);
            Auth auth = Auth.builder()
                    .username(username)
                    .token(refreshToken)
                    .build();

            authRepository.save(auth);
        }
        else {
            // DB??? ???????????? ????????? ????????? ????????????
            refreshToken = findAuth.getToken();

            // ????????? ???????????? ???????????? ?????? ?????????
            if (!jwtTokenProvider.isValidRefreshToken(refreshToken)) {
                refreshToken = jwtTokenProvider.createRefreshToken(username);
                // ?????? ????????????????????? ?????????????
                Auth auth = authRepository.findByUsername(username);
                authRepository.delete(auth);
            }

        }

        // ????????? ?????? ??????
        String accessToken = jwtTokenProvider.createAccessToken(username);
        // ??????
        response.setHeader(jwtTokenProvider.getACCESS_TOKEN_HEADER(), jwtTokenProvider.getACCESS_TOKEN_PREFIX() + accessToken);
        response.setHeader(jwtTokenProvider.getREFRESH_TOKEN_HEADER(), jwtTokenProvider.getREFRESH_TOKEN_PREFIX() + refreshToken);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("unsuccessfulAuthentication ????????? : ????????? ???????????????!");
    }
}
