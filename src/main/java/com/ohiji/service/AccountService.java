package com.ohiji.service;

import com.ohiji.entity.Account;

import java.util.Optional;

// 帳號相關服務
public interface AccountService {
    // 帳號是否存在
    boolean isEmailExisted(String email);
    // 創建帳號
    Optional<Account> createAccount(Account acct);
    // 驗證信箱，如果通過，更新 roleCode 為 USER
    Optional<Account> verify(String email);
    // 重設密碼
    Optional<Account> resetPassword(Account acct);
}
