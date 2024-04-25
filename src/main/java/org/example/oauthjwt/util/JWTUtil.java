package org.example.oauthjwt.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // JWT 페이로드에 들어있는 값 확인 메서드
    public String getUserInfo(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userInfo", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String category, String userInfo, String role, Long expiredMs) {
        // 서버 디폴트 시간대 설정
        TimeZone serverTimeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(serverTimeZone);

        // 발급 시간 설정
        Date now = calendar.getTime();

        // 만료 시간 설정
        calendar.add(Calendar.MILLISECOND, expiredMs.intValue());
        Date expiration = calendar.getTime();

        // JWT 생성
        return Jwts.builder()
                .claim("category", category)
                .claim("username", userInfo)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

}
