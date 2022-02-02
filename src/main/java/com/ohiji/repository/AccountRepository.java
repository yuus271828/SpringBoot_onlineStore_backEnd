package com.ohiji.repository;

import com.ohiji.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findById(int id);
    int findIdByEmail(String email);

    // 失敗，會傳回 null
    @Query("SELECT au.roleCode FROM Authority au INNER JOIN Account a WHERE a.email=?1")
    String findRoleCodeByEmail(String email);
}
