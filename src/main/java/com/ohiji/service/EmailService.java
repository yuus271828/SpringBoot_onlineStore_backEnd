package com.ohiji.service;

// JavaEmail 相關服務
public interface EmailService {
    // 寄出信箱驗證連結信件
    void sendValidationMail(String email);
    // 寄出重設密碼連結信件
    void sendResetPasswordMail(String email);
}
