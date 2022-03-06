package com.ohiji.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;

import static com.ohiji.security.jwt.JWTConstants.EMAIL_RESET_PASSWORD_KEY;
import static com.ohiji.security.jwt.JWTConstants.EMAIL_VERIFIED_KEY;
import static com.ohiji.security.jwt.JWTConstants.EXPIRATION_TIME;

@Service
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String mailUsername;
    private final JavaMailSender mailSender;
    @Value("${key.hostname.backEnd}")
    private String API_HOSTNAME;
    @Value("${key.hostname.frontEnd}")
    private String CLI_HOSTNAME;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendValidationMail(String email) {
        try {
            var token = JWT.create()
                    .withSubject(email)
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(Algorithm.HMAC384(EMAIL_VERIFIED_KEY.getBytes()));
            var subject = "茂洋烏魚子網站會員 信箱驗證";
            var link = String.format(API_HOSTNAME+"/api/acct/verify?token=%s", token);
            var hyperlink = String.format("<a href='%s'>驗證信箱</a>", link);
            var text = String.format("<div>請按 %s 啟用帳戶，或複製鏈結至網址列：</div><br><br> %s ", hyperlink, link);
            var mail = createMimeMessage(mailUsername, email, subject, text);
            mailSender.send(mail);
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Override
    public void sendResetPasswordMail(String email) {
        try {
            var token = JWT.create()
                    .withSubject(email)
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(Algorithm.HMAC384(EMAIL_RESET_PASSWORD_KEY.getBytes()));
            var subject = "茂洋烏魚子網站會員 重設密碼";
            var link = String.format(CLI_HOSTNAME+"/contents/forgot_password?token=%s", token);
            var hyperlink = String.format("<a href='%s'>重設密碼</a>", link);
            var text = String.format("<div>請按 %s ，或複製鏈結至網址列：</div><br><br> %s ", hyperlink, link);
            var mail = createMimeMessage(mailUsername, email, subject, text);
            mailSender.send(mail);
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    private MimeMessage createMimeMessage(String from, String to, String subject, String text) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,"utf-8");

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        return mimeMessage;
    }
}
