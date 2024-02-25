package com.example.vinhomeproject.controller;

import com.example.vinhomeproject.dto.UserDTO;
import com.example.vinhomeproject.request.*;
import com.example.vinhomeproject.response.ResponseObject;
import com.example.vinhomeproject.response.SendCodeResponse;
import com.example.vinhomeproject.response.VerifyCodeRequest;
import com.example.vinhomeproject.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(@RequestBody UserDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                "Register successfully",
                service.register(request)
        ));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResponseObject> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                "Login successfully",
                service.authenticate(request)
        ));

    }
    @PostMapping("/getUser")
    public ResponseEntity<ResponseObject> getUserFromAccessToken(@RequestBody AuthenticationUserRequest ar) {
        return service.getUserFromAccessToken(ar.getAccess_token());
    }
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseObject> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return  service.resetPassword(resetPasswordRequest);
    }
    @PostMapping("/send")
    public ResponseEntity<ResponseObject> sendCode(@RequestBody SendCodeRequest email) {
        return service.sendCode(email.getEmail());
    }
    @PostMapping("/verify")
    public ResponseEntity<ResponseObject> verifyCode(@RequestBody VerifyCodeRequest code) {
        return service.verifyCode(code);
    }
}