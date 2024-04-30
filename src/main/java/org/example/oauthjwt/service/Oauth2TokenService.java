package org.example.oauthjwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.oauthjwt.exception.CustomException;
import org.example.oauthjwt.exception.ErrorCode;
import org.example.oauthjwt.repository.RefreshTokenRepository;
import org.example.oauthjwt.repository.UserRepository;
import org.example.oauthjwt.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oauth2TokenService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

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

        // refresh 토큰의 만료 검사
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            // 불분명한 토큰
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String userInfo = jwtUtil.getUserInfo(refresh);
        System.out.println("토큰 재발급을 위한 userInfo = " + userInfo);
        String role = jwtUtil.getRole(refresh);
        Long userId = userRepository.findByUserInfo(userInfo).getId();

        // 들어온 refresh 가 redis 에 들어있는 refresh 인지 검사
        boolean isExist = refreshTokenRepository.existsById(userId);
        if(!isExist) {
            throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
        }

        // 새로운 JWT 토큰 발행
        String newAccess = jwtUtil.createJwt("Authorization", userInfo, role, 60 * 60 * 1000L);
        String newRefresh = jwtUtil.createJwt("refresh", userInfo, role, 60 * 60 * 24L * 1000);

        // 기존 redis 에 있는 refresh 삭제 후 신규 토큰 저장
        refreshTokenService.deleteRefreshToken(userId);
        refreshTokenService.addRefreshToken(userId, newRefresh);

        // 응답
        response.setHeader("Authorization", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        response.setStatus(HttpStatus.OK.value());
        return;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*24);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
