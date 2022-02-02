package com.ohiji.service;

import com.ohiji.entity.Account;
import com.ohiji.entity.UserInfo;
import com.ohiji.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final AccountRepository accountRepository;

    @Autowired
    public UserServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> updateUserInfo(String email, UserInfo userInfo) {
        var account = accountRepository.findByEmail(email);

        if (account.isEmpty()) return Optional.empty();

        var acct = account.get();
        var info = acct.getUserInfo();
        info.setLastName(userInfo.getLastName());
        info.setFirstName(userInfo.getFirstName());
        info.setPhoneNumber(userInfo.getPhoneNumber());
        info.setAddress(userInfo.getAddress());
        acct.setUserInfo(info);

        return Optional.of(accountRepository.save(acct));
    }
}
