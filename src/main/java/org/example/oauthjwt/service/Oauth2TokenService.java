package org.example.oauthjwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.oauthjwt.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oauth2TokenService {

    private final JWTUtil jwtUtil;

    public void regenerateAccessToken(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
                System.out.println("refresh = " + refresh);
            }
        }

        if (refresh == null) {

            //response status code
            response.setStatus(HttpStatus.BAD_REQUEST.value()); // refresh token 이 비었음.

            return;
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            response.setStatus(HttpStatus.BAD_REQUEST.value()); // access token expired 라고 반환
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            response.setStatus(HttpStatus.BAD_REQUEST.value()); // "invalid refresh token"

            return;
        }

        String userInfo = jwtUtil.getUserInfo(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("Authorization", userInfo, role, 600000L);

        //response
        response.setHeader("Authorization", newAccess);

        response.setStatus(HttpStatus.OK.value());
        return;
    }

}
