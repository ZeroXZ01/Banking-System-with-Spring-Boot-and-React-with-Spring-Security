package com.roland.training.repository;

import com.roland.training.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("SELECT COUNT(a) FROM Account a")
    long countTotalAccounts();

    @Query("SELECT SUM(a.balance) FROM Account a")
    BigDecimal sumTotalBalance();

    @Query("SELECT a FROM Account a WHERE a.balance = (SELECT MAX(b.balance) FROM Account b)")
    Optional<Account> findHighestBalanceAccount();

    List<Account> findByOrderByCreatedAtDesc();

    @Query(value = "SELECT a.account_type FROM accounts a WHERE a.account_id = ?1", nativeQuery = true)
    String findAccountTypeById(String accountId);
}
