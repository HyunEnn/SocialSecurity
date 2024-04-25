package org.example.oauthjwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.oauthjwt.service.CustomOauth2UserService;
import org.example.oauthjwt.service.Oauth2TokenService;
import org.example.oauthjwt.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {

    private final JWTUtil jwtUtil;
    private final Oauth2TokenService oauth2TokenService;

    @GetMapping("/my")
    public ResponseEntity<String> MyAPI() {
        return ResponseEntity.ok("my route");
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        oauth2TokenService.regenerateAccessToken(request, response);
        return ResponseEntity.ok("access token regenerate success");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> LogOutAPI() {
        return ResponseEntity.ok("log out");
    }
}
