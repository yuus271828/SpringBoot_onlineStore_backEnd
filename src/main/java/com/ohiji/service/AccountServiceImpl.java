package com.ohiji.service;

import com.ohiji.entity.Account;
import com.ohiji.entity.Authority;
import com.ohiji.entity.UserInfo;
import com.ohiji.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder){
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean isEmailExisted(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<Account> createAccount(Account acct) {
        var encrypt = passwordEncoder.encode(acct.getPassword());
        acct.setPassword(encrypt);
        acct.setCreateTime(new Date());
        acct.setUpdateTime(new Date());

        var authority = createUnverifiedAuthority();
        acct.setAuthority(authority);

        var userInfo = new UserInfo();
        acct.setUserInfo(userInfo);

        return Optional.of(accountRepository.save(acct));
    }

    @Override
    public Optional<Account> verify(String email) {
        var account = accountRepository.findByEmail(email);

        if (account.isEmpty()) return Optional.empty();

        var authority = account.get().getAuthority();
        authority.setRoleCode("USER");
        authority.setRoleDescription("Verified User");
        account.get().setAuthority(authority);
        account.get().setUpdateTime(new Date());
        return Optional.of(accountRepository.save(account.get()));
    }

    @Override
    public Optional<Account> resetPassword(Account acct) {
        var email = acct.getEmail();
        var encrypt = passwordEncoder.encode(acct.getPassword());
        var account = accountRepository.findByEmail(email);

        if (account.isEmpty()) return Optional.empty();

        account.get().setPassword(encrypt);
        account.get().setUpdateTime(new Date());
        return Optional.of(accountRepository.save(account.get()));
    }

    private Authority createUnverifiedAuthority() {
        var authority = new Authority();
        authority.setRoleCode("UNVERIFIED");
        authority.setRoleDescription("Unverified User");
        return authority;
    }
}
