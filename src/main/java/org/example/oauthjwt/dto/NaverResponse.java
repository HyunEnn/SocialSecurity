package org.example.oauthjwt.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public NaverResponse(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }
    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}

/**
 * NAVER 회원 프로필 조회 API response 명세
 * {
 *   "resultcode": "00",
 *   "message": "success",
 *   "response": {
 *     "email": "openapi@naver.com",
 *     "nickname": "OpenAPI",
 *     "profile_image": "https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif",
 *     "age": "40-49",
 *     "gender": "F",
 *     "id": "32742776",
 *     "name": "오픈 API",
 *     "birthday": "10-01",
 *     "birthyear": "1900",
 *     "mobile": "010-0000-0000"
 *   }
 * }
 */