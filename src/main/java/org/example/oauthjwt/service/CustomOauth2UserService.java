package org.example.oauthjwt.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.oauthjwt.domain.User;
import org.example.oauthjwt.dto.*;
import org.example.oauthjwt.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 리소스 되는 유저 정보
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("userRequest.getAccessToken() = " + userRequest.getAccessToken());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oauth2User : " + oAuth2User);

        // registrationId는 어떤 소셜 로그인으로 접속했는 지에 대한 ID다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("registrationId = " + registrationId);
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakakoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String userInfo = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        User existData = userRepository.findByUserInfo(userInfo);
        if(existData != null) {
            existData.updateEmail(oAuth2Response.getEmail());
            existData.updateName(oAuth2Response.getName());
            userRepository.save(existData);

            UserDTO userDTO = UserDTO.builder()
                    .name(oAuth2Response.getName())
                    .role(existData.getRole())
                    .userInfo(existData.getUserInfo())
                    .build();

            return new CustomOAuth2User(userDTO);
        } else {
            User newUser = User.builder()
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .provider(oAuth2Response.getProvider())
                    .role("ROLE_USER")
                    .userInfo(userInfo)
                    .build();

            userRepository.save(newUser);

            UserDTO userDTO = UserDTO.builder()
                    .userInfo(userInfo)
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build();

            return new CustomOAuth2User(userDTO);
        }
    }
}
