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

    String SECRET_KEY = "secret";
    String ACCESS_TOKEN_HEADER = "Authorization";
    String ACCESS_TOKEN_PREFIX = "Bearer ";
    int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60;

    public String creatAccessToken(String username) throws UnsupportedEncodingException { // AccessToken 생성함수

        return JWT.create()
                .withSubject("access-token")
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String getAccessTokenFromHeader(HttpServletRequest request){ // Access Token 값 추출
        return request.getHeader(ACCESS_TOKEN_HEADER);
    }

    public DecodedJWT verify(String token){ // 토큰 검증
        return JWT.require(Algorithm.HMAC256(SECRET_KEY)).build().verify(token);
    }

    public boolean isValidToken(String token){ // Refresh Token 유효성 확인
        try{
            return !verify(token).getExpiresAt().before(new Date());
        }catch (Exception e){
            return false;
        }
    }

}