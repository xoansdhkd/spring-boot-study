package com.example.test.config.security.auth;

import com.example.test.config.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiCheckFilter extends OncePerRequestFilter {

    private AntPathMatcher antPathMatcher;
    private String pattern;
    private JwtTokenProvider jwtTokenProvider;

    public ApiCheckFilter(String pattern, JwtTokenProvider jwtTokenProvider) {
        this.antPathMatcher = new AntPathMatcher();
        this.pattern = pattern;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더가 있는지 체크하는 곳
        if(antPathMatcher.match(pattern, request.getRequestURI())) {
            System.out.println("-------- ApiCheckFilter ---------");

            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {

                String token = header.split(" ")[1];
                try {
                    if (jwtTokenProvider.isValidToken(token)) {
                        String username = String.valueOf(jwtTokenProvider.verify(token).getClaim("username"));
                    }

                } catch (Exception e) {

                }

                return;
            }

            // 이 아래는 제대로 토큰이 있을때 검증하는 코드가 필요할 듯?
        }

        filterChain.doFilter(request, response);
    }
}
