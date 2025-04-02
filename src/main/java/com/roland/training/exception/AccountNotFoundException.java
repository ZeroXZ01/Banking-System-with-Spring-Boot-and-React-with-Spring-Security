package com.roland.training.exception;

public class AccountNotFoundException extends BankingException {
    private final String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super(String.format("Error: Account %s not found.", accountNumber));
        this.accountNumber = accountNumber;
    }

    public AccountNotFoundException(String accountNumber, Throwable cause) {
        super(String.format("Error: Account %s not found.", accountNumber), cause);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
