package org.example.oauthjwt.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@RedisHash(value = "refreshToken")
public class RefreshToken {

    @Id
    private Long userId;
    @Indexed
    private String refreshToken;

    @TimeToLive
    private Long refreshTokenExpired;

}
