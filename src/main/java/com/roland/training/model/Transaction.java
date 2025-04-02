package com.roland.training.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "related_account_id")
    private String relatedAccountId;

    // Default constructor for JPA
    public Transaction() {
    }

    public Transaction(String accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactionDate = LocalDateTime.now();
        this.transactionType = amount.compareTo(BigDecimal.ZERO) > 0 ? "DEPOSIT" : "WITHDRAWAL";
    }

    public Transaction(String accountId, BigDecimal amount, LocalDateTime transactionDate) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.transactionType = amount.compareTo(BigDecimal.ZERO) > 0 ? "DEPOSIT" : "WITHDRAWAL";
    }

    public Transaction(String fromAccountId, String toAccountId, BigDecimal amount, LocalDateTime transactionDate) {
        this.accountId = fromAccountId;
        this.relatedAccountId = toAccountId;
        this.amount = amount.negate(); // From account perspective, it's negative
        this.transactionDate = transactionDate;
        this.transactionType = "TRANSFER";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getRelatedAccountId() {
        return relatedAccountId;
    }

    public void setRelatedAccountId(String relatedAccountId) {
        this.relatedAccountId = relatedAccountId;
    }

    @Override
    public String toString() {
        if ("TRANSFER".equals(transactionType) && relatedAccountId != null) {
            return String.format("%s: %s - $%.2f transferred %s account %s",
                    transactionDate, accountId, amount.abs(),
                    amount.compareTo(BigDecimal.ZERO) < 0 ? "to" : "from",
                    relatedAccountId);
        } else {
            return String.format("%s: %s - $%.2f %s",
                    transactionDate, accountId, amount.abs(),
                    amount.compareTo(BigDecimal.ZERO) > 0 ? "deposit" : "withdrawal");
        }
    }
}
