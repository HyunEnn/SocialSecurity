package org.example.oauthjwt.repository;

import org.example.oauthjwt.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Boolean existsByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}
