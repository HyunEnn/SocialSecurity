package org.example.oauthjwt.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.oauthjwt.dto.CustomOAuth2User;
import org.example.oauthjwt.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    // 로그인을 통해서 jwt 토큰을 발급하고, 발급된 jwt token을 3000번 포트로 쿠키에 담아서 전달한다.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        // jwt를 만들 때, role과 username을 claim해서 만들었기 때문에 값을 넘겨줘야함
        String userInfo = customUserDetails.getUserInfo();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access = jwtUtil.createJwt("access", userInfo, role, 60 * 60 * 10L); // 1시간
        String refresh = jwtUtil.createJwt("refresh", userInfo, role, 86400000L); // 하루

        response.setHeader("Authorization", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("http://localhost:5173");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*24000);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}

/**
 * 하이퍼링크로 프론트한테 JWT 토큰을 보내줌.
 * 로그인에 대한 책임을 모두 백엔드가 가짐.
 * 그래서 프론트로 내보낼 때, 토큰에 대한 값 처리를 cookie로 내보내는게 일반적
 */