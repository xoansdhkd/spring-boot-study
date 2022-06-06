package com.example.test.config.security.auth;

import com.example.test.config.security.jwt.JwtTokenProvider;
import com.example.test.domain.user.User;
import com.example.test.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiCheckFilter extends BasicAuthenticationFilter {

//    private AntPathMatcher antPathMatcher;
//    private String pattern;
    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;

//    public ApiCheckFilter(String pattern, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
//        this.antPathMatcher = new AntPathMatcher();
//        this.pattern = pattern;
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.userRepository = userRepository;
//    }

    public ApiCheckFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // 헤더가 있는지 체크하는 곳
//        if(antPathMatcher.match(pattern, request.getRequestURI())) {
            System.out.println("-------- ApiCheckFilter ---------");

            String header = jwtTokenProvider.getAccessTokenFromHeader(request);

            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }


            String token = header.split(" ")[1];

            // 토큰 만료 체크
            if (jwtTokenProvider.isValidToken(token)) {
                String username = jwtTokenProvider.verify(token).getClaim("username").asString();
                if (username == null || username.isEmpty()) {
                    // 에러 처리
                    System.out.println("토큰 username 없음 에러");
                    return;
                }

                User user = userRepository.findByUsername(username);
                UserDetailsImpl userDetails = new UserDetailsImpl(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
            } else {
                // 토큰 만료
                System.out.println("[WARN] Expired Access Token");
                return;
            }

        }
//    }
}
