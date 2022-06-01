package com.example.test.config.jwt;

import com.example.test.domain.user.Role;
import com.example.test.domain.user.User;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    private static final String secretKey = "ThisIsA_SecretKeyForJwtExample";

    public static String createJwtToken(User user) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(user.getUserName())
                .setHeader(createJwtHeader())
                .setClaims(createJwtClaims(user))
                .setExpiration(createExpireDateForOneMonth())
                .signWith(SignatureAlgorithm.HS256, createSigningKey());

        return builder.compact();
    }

    public static boolean verify(String token) {
        try {
            Claims claims = getClaimsFormToken(token);
            System.out.println("expireTime :" + claims.getExpiration());
            System.out.println("userName :" + claims.get("userName"));
            System.out.println("role :" + claims.get("role"));
            return true;

        } catch (ExpiredJwtException exception) {
            System.out.println("만료된 토큰입니다.");
            return false;
        } catch (JwtException exception) {
            System.out.println("Token Tampered");
            return false;
        } catch (NullPointerException exception) {
            System.out.println("잘못된 토큰입니다. (null)");
            return false;
        }
    }

    private static Date createExpireDateForOneMonth() {
        // 토큰 만료시간은 30일으로 설정
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 30);
        return cal.getTime();
    }

    private static Map<String, Object> createJwtHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", "JWT");
        header.put("alg", "HS256");
        header.put("regDate", System.currentTimeMillis());

        return header;
    }

    private static Map<String, Object> createJwtClaims(User user) {
        // 공개 클레임에 사용자의 이름과 이메일을 설정하여 정보를 조회할 수 있다.
        Map<String, Object> claims = new HashMap<>();

        claims.put("userName", user.getUserName());
        claims.put("role", user.getRole());

        return claims;
    }

    private static Key createSigningKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    private static Claims getClaimsFormToken(String token) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(token).getBody();
    }

    private static String getUserEmailFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return (String) claims.get("userName");
    }

    private static Role getRoleFromToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return (Role) claims.get("role");
    }
}
