package com.example.test.config.jwt;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Auth implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String header = request.getHeader("Authorization");

        if(header != null && !header.isEmpty() && header.startsWith("Bearer ")) {
            String token = header.split(" ")[1];
            if(JwtUtils.verify(token)) {
                System.out.println("토큰 인증이 완료되었습니다.");
                return true;
            }
        }
        System.out.println("토큰 인증 실패. 처리는 response.뭐시기 이렇게 하면됨");
        return false;
    }
}
