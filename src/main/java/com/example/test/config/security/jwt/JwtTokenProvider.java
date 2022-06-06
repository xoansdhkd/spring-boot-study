package com.example.test.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Component
public class JwtTokenProvider {

    String ACCESS_TOKEN_SECRET_KEY = "secret";
    String REFRESH_TOKEN_SECRET_KEY = "secret2";

    String ACCESS_TOKEN_HEADER = "Authorization";
    String REFRESH_TOKEN_HEADER = "Refresh";

    String ACCESS_TOKEN_PREFIX = "Bearer ";
    String REFRESH_TOKEN_PREFIX = "Bearer ";

    int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 20; // 20초
    int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 70; // 70초

    public String createAccessToken(String username) throws UnsupportedEncodingException { // AccessToken 생성함수

        return JWT.create()
                .withSubject("access-token")
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(ACCESS_TOKEN_SECRET_KEY));
    }

    public String createRefreshToken(String username) throws UnsupportedEncodingException { // AccessToken 생성함수

        return JWT.create()
                .withSubject("refresh-token")
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(REFRESH_TOKEN_SECRET_KEY));
    }

    public String getAccessTokenFromHeader(HttpServletRequest request){ // Access Token 값 추출
        return request.getHeader(ACCESS_TOKEN_HEADER);
    }

    public String getRefreshTokenFromHeader(HttpServletRequest request){ // Access Token 값 추출
        return request.getHeader(REFRESH_TOKEN_HEADER);
    }

    public DecodedJWT verifyAccessToken(String token){ // 토큰 검증
        return JWT.require(Algorithm.HMAC256(ACCESS_TOKEN_SECRET_KEY)).build().verify(token);
    }

    public DecodedJWT verifyRefreshToken(String token){ // 토큰 검증
        return JWT.require(Algorithm.HMAC256(REFRESH_TOKEN_SECRET_KEY)).build().verify(token);
    }

    public boolean isValidAccessToken(String token){ // Access Token 만료 체크
        try{
            return !verifyAccessToken(token).getExpiresAt().before(new Date());
        }catch (Exception e){
            return false;
        }
    }

    public boolean isValidRefreshToken(String token){ // Refresh Token 만료 체크
        try{
            return !verifyRefreshToken(token).getExpiresAt().before(new Date());
        }catch (Exception e){
            return false;
        }
    }

}