package com.ohiji.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ohiji.entity.Account;
import com.ohiji.response.JWTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.ohiji.security.jwt.JWTConstants.EXPIRATION_TIME;
import static com.ohiji.security.jwt.JWTConstants.SECRET_KEY;

// 帳號登入驗證，回傳 JWT 給前端
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account acct) {

        /* authenticationManager 的 authenticate(boolean) 方法，如果傳入 true 就會回傳 Authentication 憑證。
           只要有了憑證，就可以訪問受保護的資源。 */
        Authentication authentication = authenticationManager.authenticate(
                /* UsernamePasswordAuthenticationToken(username, password) 方法，
                   會比對 UserDetailService 中 UserDetails 的 User 帳號，決定回傳 true or false */
                new UsernamePasswordAuthenticationToken(acct.getEmail(), acct.getPassword())
        );

        // 傳入 Authentication 憑證，獲得訪問受保護資源的許可。
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 取得 Authentication 憑證中的 User 資料(包含 username, password, authority)。
        User user = (User) authentication.getPrincipal();

        var response = new JWTokenResponse();

        var authority = user.getAuthorities().toString();
        if (authority.equals("[UNVERIFIED]")) {
            response.setToken("unverified");
            return ResponseEntity.ok(response);
        };

        var JWToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET_KEY.getBytes()));

        response.setToken(JWToken);
        return ResponseEntity.ok(response);
    }

}
