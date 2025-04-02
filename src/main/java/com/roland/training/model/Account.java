package com.roland.training.model;

import com.roland.training.exception.InsufficientFundsException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Account {

    @Id
    @Column(name = "account_id")
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "last_transaction")
    private LocalDateTime lastTransaction;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Static field - shared across instances
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");

    // Default constructor for JPA
    protected Account() {
    }

    // Public constructor
    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.lastTransaction = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    // Abstract method
    public abstract void processMonthlyFees();

    // Concrete method with virtual invocation
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException(accountNumber, amount, balance);
        }

        balance = balance.subtract(amount);
        lastTransaction = LocalDateTime.now();
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
        lastTransaction = LocalDateTime.now();
    }

    // Protected method for subclasses
    protected abstract boolean canWithdraw(BigDecimal amount);

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getLastTransaction() {
        return lastTransaction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    protected BigDecimal getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    // Setters for JPA
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setLastTransaction(LocalDateTime lastTransaction) {
        this.lastTransaction = lastTransaction;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Override Object class methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Account)) return false;
        Account other = (Account) obj;
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Account[number=%s, balance=%.2f]",
                accountNumber, balance);
    }
}
