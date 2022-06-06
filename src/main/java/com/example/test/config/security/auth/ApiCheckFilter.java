package com.example.test.config.security.auth;

import com.example.test.config.security.jwt.JwtTokenProvider;
import com.example.test.domain.auth.Auth;
import com.example.test.domain.auth.AuthRepository;
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
    private AuthRepository authRepository;

//    public ApiCheckFilter(String pattern, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
//        this.antPathMatcher = new AntPathMatcher();
//        this.pattern = pattern;
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.userRepository = userRepository;
//    }

    public ApiCheckFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, AuthRepository authRepository) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.authRepository = authRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // 헤더가 있는지 체크하는 곳
        System.out.println("-------- ApiCheckFilter ---------");

        // 액세스 토큰 가져온다
        String header = jwtTokenProvider.getAccessTokenFromHeader(request);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
            
        // 액세스 토큰에서 토큰 값만 분리
        String accessToken = header.split(" ")[1];

            // 액세스 토큰 만료 체크
        String username = "";
        if (jwtTokenProvider.isValidAccessToken(accessToken)) {
            username = jwtTokenProvider.verifyAccessToken(accessToken).getClaim("username").asString();
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
            // 액세스 토큰 만료
            // 리프레시 토큰 DB에서 가져오기
            System.out.println("[WARN] Expired Access Token");


            // 리프레시 토큰에서 이름 가져오기
            String refreshTokenHeader = jwtTokenProvider.getRefreshTokenFromHeader(request);
            String clientRefreshToken = refreshTokenHeader.split(" ")[1];

            if (jwtTokenProvider.isValidRefreshToken(clientRefreshToken)) {
                // DB 리프레시 토큰이 유효하다면 클라이언트가 보낸 토큰이랑 비교
                username = jwtTokenProvider.verifyRefreshToken(clientRefreshToken).getClaim("username").asString();

                Auth findAuth = authRepository.findByUsername(username);
                if (findAuth == null) {
                    System.out.println("[ERROR] DB 리프레시 토큰 없음");
                    System.out.println("에러 코드 보내서 로그인 창으로 보내기 / 로그인 해제");
                    return;
                }

                String refreshToken = findAuth.getToken();
                // 두 개가 같다면
                if (clientRefreshToken.equals(refreshToken)) {
                    System.out.println("리프레시 토큰 비교 : " + refreshToken.equals(clientRefreshToken));
                    System.out.println("액세스 토큰 재생성");
                    // 액세스 토큰 새로 생성
                    String newAccessToken = jwtTokenProvider.createAccessToken(username);
                    // 전달
                    response.setHeader(jwtTokenProvider.getACCESS_TOKEN_HEADER(), jwtTokenProvider.getACCESS_TOKEN_PREFIX() + newAccessToken);
                    System.out.println("이걸로 세로 세팅해줘야됨");
                }
                else {
                    // 다르다면
                    System.out.println("[ERROR] 리프레시 토큰 불일치");
                    System.out.println("에러 코드 보내서 로그인 창으로 보내기 / 로그인 해제");
                    return;
                }

            } else {
                // 리프레시 토큰이 만료 됐으면
                // DB에서 삭제하고
                System.out.println("리프레시 토큰 만료");
                System.out.println("에러 코드 보내서 로그인 창으로 보내기 / 로그인 해제");
                System.out.println("클라이언트에서 액세스 토큰 / 리프레시 토큰 삭제");
                System.out.println("db에서 삭제");

                // 여기 에러남 어쩌지 - username을 어디서 받아와야되지
                authRepository.delete(authRepository.findByUsername(username));
                return;
            }
            
        }

    }

}
