package com.example.test.config.security.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.test.config.security.jwt.JwtTokenProvider;
import com.example.test.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {

    private JwtTokenProvider jwtTokenProvider;

    public ApiLoginFilter(String defaultFilterProcessesUrl, JwtTokenProvider jwtTokenProvider) {
        super(defaultFilterProcessesUrl);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        ObjectMapper om = new ObjectMapper();
        User user = om.readValue(request.getInputStream(), User.class);

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
        try {
            String token = jwtTokenProvider.creatAccessToken(username);
            response.setHeader(jwtTokenProvider.getACCESS_TOKEN_HEADER(), jwtTokenProvider.getACCESS_TOKEN_PREFIX() + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("unsuccessfulAuthentication 실행됨 : 인증이 실패하였다!");
    }
}
