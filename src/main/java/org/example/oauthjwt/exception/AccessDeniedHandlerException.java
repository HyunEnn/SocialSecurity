package org.example.oauthjwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerException implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(
            new ObjectMapper().writeValueAsString(
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("접근거부: 로그인이 필요합니다!")
            )
        );
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

}