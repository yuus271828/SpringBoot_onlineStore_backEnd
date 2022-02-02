package com.ohiji.service;

import com.ohiji.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AccountRepository accountRepository;

    @Autowired
    public AuthenticationServiceImpl(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var account = accountRepository.findByEmail(username);
        if (account.isEmpty()) {
            throw new UsernameNotFoundException("Email: '" + username + "' not found");
        }
        final var acct = account.get();
        final var roleCode = acct.getAuthority().getRoleCode();

        return User
                .withUsername(acct.getEmail())
                .password(acct.getPassword())
                .authorities(roleCode).build();
    }
}
