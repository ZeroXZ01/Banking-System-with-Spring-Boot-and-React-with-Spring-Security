package com.roland.training.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDTO {
    private String accountId;
    private String accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public AccountDTO() {
    }

    public AccountDTO(String accountId, String accountType, BigDecimal balance, LocalDateTime createdAt) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public static AccountDTO fromAccount(Account account, String accountType) {
        return new AccountDTO(
                account.getAccountNumber(),
                accountType,
                account.getBalance(),
                account.getCreatedAt()
        );
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

