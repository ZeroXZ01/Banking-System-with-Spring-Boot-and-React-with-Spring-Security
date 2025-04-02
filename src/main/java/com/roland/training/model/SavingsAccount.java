package com.roland.training.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends Account {

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    // Default constructor for JPA
    public SavingsAccount() {
        super();
    }

    public SavingsAccount(String accountNumber, BigDecimal balance) {
        super(accountNumber, balance);
        this.interestRate = new BigDecimal("0.025"); // 2.5% interest rate
    }

    public SavingsAccount(String accountNumber, BigDecimal balance, BigDecimal interestRate) {
        super(accountNumber, balance);
        this.interestRate = interestRate;
    }

    @Override
    public void processMonthlyFees() {
        // Calculate and add interest
        BigDecimal interest = getBalance().multiply(interestRate);
        deposit(interest);
    }

    @Override
    protected boolean canWithdraw(BigDecimal amount) {
        // Ensure minimum balance is maintained
        return getBalance().subtract(amount)
                .compareTo(getMinimumBalance()) >= 0;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return String.format("SavingsAccount[number=%s, balance=%.2f, interestRate=%.2f%%]",
                getAccountNumber(),
                getBalance(),
                interestRate.multiply(new BigDecimal("100")));
    }
}
