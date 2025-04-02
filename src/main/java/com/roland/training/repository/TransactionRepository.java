package com.roland.training.repository;

import com.roland.training.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByTransactionDateDesc(String accountId);

    List<Transaction> findAllByOrderByTransactionDateDesc();

    @Query("SELECT t.accountId, COUNT(t) as count FROM Transaction t GROUP BY t.accountId ORDER BY count DESC")
    List<Object[]> findMostActiveAccount();

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.transactionDate >= ?1 AND t.amount > 0")
    BigDecimal sumDepositsAfterDate(LocalDateTime date);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.transactionDate >= ?1 AND t.amount < 0")
    BigDecimal sumWithdrawalsAfterDate(LocalDateTime date);
}

