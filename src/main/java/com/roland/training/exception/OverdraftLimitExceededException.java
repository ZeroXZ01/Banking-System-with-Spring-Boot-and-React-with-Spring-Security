package com.roland.training.exception;

import java.math.BigDecimal;

public class OverdraftLimitExceededException extends BankingException {
    private final String accountNumber;
    private final BigDecimal requestedAmount;
    private final BigDecimal availableBalance;

    public OverdraftLimitExceededException(String accountNumber, BigDecimal requestedAmount, BigDecimal availableBalance) {
        super(String.format("Account %s exceeded the Overdraft Limit of -$100.00: requested %.2f, available %.2f.",
                accountNumber, requestedAmount, availableBalance));
        this.accountNumber = accountNumber;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
}
