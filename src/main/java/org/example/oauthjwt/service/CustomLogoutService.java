package org.example.oauthjwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.oauthjwt.exception.CustomException;
import org.example.oauthjwt.exception.ErrorCode;
import org.example.oauthjwt.repository.RefreshTokenRepository;
import org.example.oauthjwt.util.JWTUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class CustomLogoutService extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("여긴 들어오나?");
        // 쿠키에서 REFRESH 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // REFRESH 값의 여부
        if (refresh == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
        }

        // REFRESH 토큰 만료 여부
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        // 토큰이 REFRESH 인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // redis 에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefreshToken(refresh);
        if (!isExist) {

            throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
        }

        // 로그아웃 진행
        // Refresh 를 redis 에서 제거
        refreshRepository.deleteByRefreshToken(refresh);

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        // 추가적인 로직 수행
//        System.out.println("로그아웃 작업이 완료되었습니다.");

        // 필터 체인을 계속 진행
//        filterChain.doFilter(request, response);
//        response.setStatus(HttpServletResponse.SC_OK);
    }
}

/**
 * 로그아웃은 JWT 검증 처럼 매 로직마다 처리가 필요한 필터가 아니고
 * 요청이 들어올 때만 수행이 되도록 하기 때문에, SERVICE 단에 설정해도 무방하다.
 */

