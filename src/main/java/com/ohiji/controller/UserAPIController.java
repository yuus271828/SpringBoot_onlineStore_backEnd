package com.ohiji.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ohiji.entity.UserInfo;
import com.ohiji.repository.AccountRepository;
import com.ohiji.response.APIResponse;
import com.ohiji.response.UserInfoResponse;
import com.ohiji.service.AccountService;
import com.ohiji.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.ohiji.security.jwt.JWTConstants.HEADER_STRING;
import static com.ohiji.security.jwt.JWTConstants.SECRET_KEY;
import static com.ohiji.security.jwt.JWTConstants.TOKEN_PREFIX;

// 取得 userInfo、更新 userInfo、檢查登入狀態(JWT是否有效)
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserAPIController {
    private final UserService userService;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Autowired
    public UserAPIController(UserService userService, AccountRepository accountRepository, AccountService accountService) {
        this.userService = userService;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<?> getInfo(HttpServletRequest request) {
        var response = new UserInfoResponse();
        var token = request.getHeader(HEADER_STRING);
        var email = JWT.require(Algorithm.HMAC512(SECRET_KEY.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
        var account = accountRepository.findByEmail(email);

        if (account.isEmpty()) {
            return ResponseEntity.ok(response);
        }

        var userInfo = account.get().getUserInfo();
        response.setEmail(email);
        response.setLastName(userInfo.getLastName());
        response.setFirstName(userInfo.getFirstName());
        response.setPhoneNumber(userInfo.getPhoneNumber());
        response.setAddress(userInfo.getAddress());

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/updateUserInfo")
    public ResponseEntity<?> updateInfo(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        var response = new APIResponse();
        var token = request.getHeader(HEADER_STRING);
        var email = JWT.require(Algorithm.HMAC512(SECRET_KEY.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
        if(!accountService.isEmailExisted(email)){
            response.setSuccess(false);
            response.setMessage("帳號不存在");
            return ResponseEntity.ok(response);
        }
        userService.updateUserInfo(email, userInfo);
        response.setSuccess(true);
        response.setMessage("會員資料更新成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/connected")
    public ResponseEntity<?> connected(HttpServletRequest request) {
        var response = new APIResponse();
        var token = request.getHeader(HEADER_STRING);
        String email;
        try {
            email = JWT.require(Algorithm.HMAC512(SECRET_KEY.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();
        } catch (SignatureVerificationException | JWTDecodeException | TokenExpiredException e) {
            response.setSuccess(false);
            return ResponseEntity.ok(response);
        }
        if (!accountService.isEmailExisted(email)) {
            response.setSuccess(false);
            return ResponseEntity.ok(response);
        }
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }


}
