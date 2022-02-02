package com.ohiji.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ohiji.entity.Account;
import com.ohiji.response.APIResponse;
import com.ohiji.service.AccountService;
import com.ohiji.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;

import static com.ohiji.security.jwt.JWTConstants.EMAIL_RESET_PASSWORD_KEY;
import static com.ohiji.security.jwt.JWTConstants.EMAIL_VERIFIED_KEY;

/*  決定用 GET 或 POST 的理由
    GET：來自超連結的 request、不需要用 @RequestBody 取得引數
    POST：創建資料、需要用 @RequestBody 取得引數
    PUT：更新資料、需要用 @RequestBody 取得引數
*/


// 帳號註冊、信箱驗證、重設密碼
@RestController
@RequestMapping("/api/acct")
@CrossOrigin
public class AccountController {
    private final AccountService accountService;
    private final EmailService emailService;

    @Autowired
    public AccountController(AccountService accountService, EmailService emailService) {
        this.accountService = accountService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> add(@RequestBody Account acct) {
        var response = new APIResponse();
        var email = acct.getEmail();

        if(accountService.isEmailExisted(email)) {
            response.setSuccess(false);
            response.setMessage("此帳號己存在");
            return ResponseEntity.ok(response);
        }

        try {
            emailService.sendValidationMail(email);
        } catch (MailSendException e) {
            response.setSuccess(false);
            response.setMessage("此信箱不存在");
            return ResponseEntity.ok(response);
        }
        accountService.createAccount(acct);
        response.setSuccess(true);
        response.setMessage("註冊成功，己送出驗證信，請前往信箱驗證");
        return ResponseEntity.ok(response);
    }

    // 超連結
    @GetMapping("/verify")
    public RedirectView verify(@RequestParam String token) {
        var redirectView = new RedirectView();
        String email;
        try {
            email = JWT.require(Algorithm.HMAC384(EMAIL_VERIFIED_KEY.getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (SignatureVerificationException | JWTDecodeException e) {
            System.out.println("接收到來自 異常 JWToken 的 verify 請求 --- " + new Date());

            redirectView.setUrl("http://localhost:3000/contents/message_page?text=verify_failed");
            return redirectView;
        } catch (TokenExpiredException e) {
            System.out.println("接收到來自 過期 JWToken 的 verify 請求 --- " + new Date());

            redirectView.setUrl("http://localhost:3000/contents/message_page?text=verify_failed");
            return redirectView;
        }

        var account = accountService.verify(email);
        if (account.isEmpty()) {
            redirectView.setUrl("http://localhost:3000/contents/message_page?text=verify_failed");
            return redirectView;
        }
        redirectView.setUrl("http://localhost:3000/contents/message_page?text=verify_success");
        return redirectView;
    }

    @PutMapping("/forgot")
    public ResponseEntity<?> forgot(@RequestParam String token, @RequestBody Account acct) throws TokenExpiredException {
        var response = new APIResponse();
        String email;
        try {
            email = JWT.require(Algorithm.HMAC384(EMAIL_RESET_PASSWORD_KEY.getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (SignatureVerificationException | JWTDecodeException e) {
            System.out.println("接收到來自 異常 JWToken 的 forgot 請求 --- " + new Date());

            response.setSuccess(false);
            response.setMessage("連結失效，請重新嘗試");
            return ResponseEntity.ok(response);
        } catch (TokenExpiredException e) {
            System.out.println("接收到來自 過期 JWToken 的 forgot 請求 --- " + new Date());

            response.setSuccess(false);
            response.setMessage("連結過期，請重新嘗試");
            return ResponseEntity.ok(response);
        }

        acct.setEmail(email);
        var account = accountService.resetPassword(acct);

        if (account.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("此帳號不存在");
            return ResponseEntity.ok(response);
        }

        response.setSuccess(true);
        response.setMessage("重設成功");
        return ResponseEntity.ok(response);
    }
}
