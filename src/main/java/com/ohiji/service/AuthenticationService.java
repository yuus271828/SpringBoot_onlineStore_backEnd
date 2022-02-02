package com.ohiji.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

// Authentication 與 UserDetailsService 相關服務
public interface AuthenticationService extends UserDetailsService {
    /*
        從 DB 中取出欲驗證帳號的 email, password, roleCode，
        然後存入 UserDetail 中建立 User 帳號。
        (在 UserDetail 中建立 User 帳號後，就可以拿來跟登入的帳密比對，如果一致，就給予授權(Authentication)。)
    */
    UserDetails loadUserByUsername(String username);
}
