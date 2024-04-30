package org.example.oauthjwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.oauthjwt.domain.RefreshToken;
import org.example.oauthjwt.exception.CustomException;
import org.example.oauthjwt.exception.ErrorCode;
import org.example.oauthjwt.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.jwt.refresh-expired}")
    private Long JWT_EXPIRED;

    public void addRefreshToken(Long userId, String tokenValue) {

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .refreshToken(tokenValue)
                .refreshTokenExpired(JWT_EXPIRED)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(Long userId) {

        refreshTokenRepository.deleteById(userId);
    }


}
