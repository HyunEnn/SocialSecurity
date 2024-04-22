package org.example.oauthjwt.dto;

public interface OAuth2Response {
    // 제공자
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}
