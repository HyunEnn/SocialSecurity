package org.example.oauthjwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @GetMapping("/my")
    @ResponseBody
    public ResponseEntity<String> MyAPI() {
        return ResponseEntity.ok("my route");
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> LogOutAPI() {
        return ResponseEntity.ok("log out");
    }
}
