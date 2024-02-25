package com.example.vinhomeproject.service;

import com.example.vinhomeproject.config.JwtService;
import com.example.vinhomeproject.dto.UserDTO;
import com.example.vinhomeproject.models.Role;
import com.example.vinhomeproject.models.Token;
import com.example.vinhomeproject.models.TokenType;
import com.example.vinhomeproject.models.Users;
import com.example.vinhomeproject.repositories.TokenRepository;
import com.example.vinhomeproject.repositories.UsersRepository;
import com.example.vinhomeproject.request.AuthenticationRequest;
import com.example.vinhomeproject.request.ResetPasswordRequest;
import com.example.vinhomeproject.request.VerifyCodeResponse;
import com.example.vinhomeproject.response.AuthenticationResponse;
import com.example.vinhomeproject.response.ResponseObject;
import com.example.vinhomeproject.response.SendCodeResponse;
import com.example.vinhomeproject.response.VerifyCodeRequest;
import com.example.vinhomeproject.utils.SendMailUtils;
import com.example.vinhomeproject.utils.VerificationCodeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthenticationService  {

    @Autowired
    private UsersRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private VerificationCodeUtils verificationCodeUtils;

    @Autowired
    private SendMailUtils sendMailUtils;

    public AuthenticationResponse register(UserDTO request) {
        var user = Users.builder()
                .email(request.getEmail())
                .gender(request.getGender())
                .image(request.getImage())
                .phone(request.getPhone())
                .address(request.getAddress())
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .isVerified(false)
                .status(true)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .access_token(jwtToken)
//                .refresh_token(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user,jwtToken);
        return AuthenticationResponse.builder()
                .access_token(jwtToken)
//                .refresh_token(refreshToken)
                .build();
    }
    private void saveUserToken(Users user, String jwtToken) {
        var token = Token.builder()
                .users(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    private void revokeAllUserTokens(Users user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .access_token(accessToken)
//                        .refresh_token(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public ResponseEntity<ResponseObject> getUserFromAccessToken(String accessToken){
        String userEmail = jwtService.extractUsername(accessToken);
        if (userEmail != null) {
            Users user = repository.findByEmail(userEmail)
                    .orElseThrow();
            return ResponseEntity.ok(new ResponseObject("Access Token is valid", user));
        }
        return ResponseEntity.badRequest().body(new ResponseObject("Access Token is not valid", null));
    }
    public ResponseEntity<ResponseObject> resetPassword(ResetPasswordRequest request) {
        boolean result = false;
        Optional<Users> user = repository.findByEmail(request.getEmail());
        if (user.isPresent()){
            user.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(user.get());
            result = true;
        }
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    "Successfully",
                    null
            ));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                "Bad request",
                null
        ));

    }
    public ResponseEntity<ResponseObject> sendCode(String email) {
        SendCodeResponse sendCodeResponse = new SendCodeResponse();
        String _email = "";
        try {
            Optional<Users> user =  repository.findByEmail(email);
            if (user.isPresent()){
                if (user.get().isStatus()){
                    String code = verificationCodeUtils.generateVerificationCode(user.get().getEmail());
                    sendMailUtils.sendSimpleEmail(
                            email,
                            "Verification code - Whalhome",
                            "Xin chào,\n" +
                                    "\n" +
                                    "Bạn đã yêu cầu đổi mật khẩu cho tài khoản của mình trên Whalhome. Dưới đây là mã xác nhận của bạn:\n" +
                                    "\n" +
                                    "Mã Xác Nhận: "+ code + "\n" +
                                    "\n" +
                                    "Vui lòng sử dụng mã này để xác nhận quy trình đổi mật khẩu. Hãy nhớ rằng mã xác nhận chỉ có hiệu lực trong một khoảng thời gian ngắn.\n" +
                                    "\n" +
                                    "Nếu bạn không yêu cầu đổi mật khẩu, vui lòng bỏ qua email này. Để bảo vệ tài khoản của bạn, không chia sẻ mã xác nhận với người khác.\n" +
                                    "\n" +
                                    "Trân trọng,\n" +
                                    "Whalhome"
                    );
                    _email = user.get().getEmail();
                } else {
                    throw new LockedException("");
                }
            }
            if (!Objects.equals(_email, "")){
                sendCodeResponse.setEmail(_email);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                        "Successfully",
                        sendCodeResponse
                ));
            } else {
                sendCodeResponse.setMessage("The email account does not exist in the system!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        "Failed",
                        sendCodeResponse
                ));
            }
        } catch (Exception e){
            if (e instanceof LockedException) {
                sendCodeResponse.setMessage("This email account has been disabled!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        "Failed",
                        sendCodeResponse
                ));
            } else {
                e.printStackTrace();
                sendCodeResponse.setMessage("Error!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        "Failed",
                        sendCodeResponse
                ));
            }
        }

    }
    public ResponseEntity<ResponseObject> verifyCode(VerifyCodeRequest request) {
        VerifyCodeResponse verifyCodeResponse = new VerifyCodeResponse();
        String email = "";
        if (verificationCodeUtils.isValidCode(request.getCode())){
            String emailOfCode = verificationCodeUtils.getEmailByCode(request.getCode());
            if (Objects.equals(emailOfCode, request.getEmail())){
                email = emailOfCode;
            }
        } else {
            email = "";
        }

        if (!Objects.equals(email, "")){
            verifyCodeResponse.setMail(email);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    "Successfully",
                    verifyCodeResponse
            ));
        } else {
            verifyCodeResponse.setMessage("Code is incorrect or has expired.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    "Failed",
                    verifyCodeResponse
            ));
        }

    }
}