package com.ohiji.controller;

import com.ohiji.entity.Account;
import com.ohiji.repository.AccountRepository;
import com.ohiji.response.APIResponse;
import com.ohiji.service.AccountService;
import com.ohiji.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 重新寄送驗證信、寄送重設密碼信件
@RestController
@RequestMapping("/api/email")
@CrossOrigin
public class EmailController {
    private final AccountService accountService;
    private final EmailService emailService;
    private final AccountRepository accountRepository;

    @Autowired
    public EmailController(AccountService accountService, EmailService emailService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.emailService = emailService;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@RequestBody Account acct) {
        var email = acct.getEmail();
        var response = new APIResponse();
        var account = accountRepository.findByEmail(email);

        if (account.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("帳號不存在");
            return ResponseEntity.ok(response);
        }

        var role = account.get().getAuthority().getRoleCode();
        if (!role.equals("UNVERIFIED")) {
            response.setSuccess(true);
            response.setMessage("帳號己通過驗證");
            return ResponseEntity.ok(response);
        }

        try {
            emailService.sendValidationMail(email);
        } catch (MailSendException e) {
            response.setSuccess(false);
            response.setMessage("發送失敗");
            return ResponseEntity.ok(response);
        }

        response.setSuccess(true);
        response.setMessage("驗證信箱 信件發送成功");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendForgot")
    public ResponseEntity<?> sendForgot(@RequestBody Account acct) {
        var email = acct.getEmail();
        var response = new APIResponse();

        if (!accountService.isEmailExisted(email)) {
            response.setSuccess(false);
            response.setMessage("帳號不存在");
            return ResponseEntity.ok(response);
        }

        try {
            emailService.sendResetPasswordMail(email);
        } catch (MailSendException e) {
            response.setSuccess(false);
            response.setMessage("發送失敗");
            return ResponseEntity.ok(response);
        }

        response.setSuccess(true);
        response.setMessage("重設密碼 信件發送成功");
        return ResponseEntity.ok(response);
    }
}
