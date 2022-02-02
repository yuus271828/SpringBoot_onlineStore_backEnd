package com.ohiji.service;

import com.ohiji.entity.Account;
import com.ohiji.entity.UserInfo;

import java.util.Optional;

// 己登入的使用者( User 權限)相關服務
public interface UserService {
    // 更新 DB 的 userInfo 資料
    Optional<Account> updateUserInfo(String email, UserInfo userInfo);
}

