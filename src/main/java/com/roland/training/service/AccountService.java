package com.roland.training.service;

import com.roland.training.exception.AccountNotFoundException;
import com.roland.training.exception.BankingException;
import com.roland.training.exception.InsufficientFundsException;
import com.roland.training.exception.OverdraftLimitExceededException;
import com.roland.training.model.*;
import com.roland.training.repository.AccountRepository;
import com.roland.training.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public Account createAccount(AccountType type, String accountId, BigDecimal initialBalance) throws BankingException {
        // Create appropriate account type
        Account account;
        if (type == AccountType.SAVINGS) {
            account = new SavingsAccount(accountId, initialBalance);
        } else if (type == AccountType.CHECKING) {
            account = new CheckingAccount(accountId, initialBalance);
        } else {
            throw new BankingException("Invalid account type");
        }

        // Save account to database
        account = accountRepository.save(account);

        // Log initial deposit if positive
        if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
            transactionService.logTransaction(accountId, initialBalance, "Initial deposit");
        }

        return account;
    }

    @Transactional
    public void deposit(String accountId, BigDecimal amount) throws BankingException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Deposit amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Update balance
        account.setBalance(account.getBalance().add(amount));
        account.setLastTransaction(LocalDateTime.now());
        accountRepository.save(account);

        // If it's a checking account, increment transaction count
        if (account instanceof CheckingAccount) {
            ((CheckingAccount) account).incrementTransactions();
        }

        // Log transaction
        transactionService.logTransaction(accountId, amount, "Deposit");
    }

    @Transactional
    public void withdraw(String accountId, BigDecimal amount) throws BankingException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Withdrawal amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Check balance based on account type
        if (account instanceof SavingsAccount) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(accountId, amount, account.getBalance());
            }
        } else if (account instanceof CheckingAccount) {
            BigDecimal overdraftLimit = new BigDecimal("-100.00");
            if (account.getBalance().subtract(amount).compareTo(overdraftLimit) < 0) {
                throw new OverdraftLimitExceededException(accountId, amount, account.getBalance());
            }
            // Increment transaction count
            ((CheckingAccount) account).incrementTransactions();
        }

        // Update balance
        account.setBalance(account.getBalance().subtract(amount));
        account.setLastTransaction(LocalDateTime.now());
        accountRepository.save(account);

        // Log transaction
        transactionService.logTransaction(accountId, amount.negate(), "Withdrawal");
    }

    @Transactional
    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) throws BankingException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Transfer amount must be positive");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));

        // Check balance based on account type
        if (fromAccount instanceof SavingsAccount) {
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(fromAccountId, amount, fromAccount.getBalance());
            }
        } else if (fromAccount instanceof CheckingAccount) {
            BigDecimal overdraftLimit = new BigDecimal("-100.00");
            if (fromAccount.getBalance().subtract(amount).compareTo(overdraftLimit) < 0) {
                throw new OverdraftLimitExceededException(fromAccountId, amount, fromAccount.getBalance());
            }
            // Increment transaction count
            ((CheckingAccount) fromAccount).incrementTransactions();
        }

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        LocalDateTime now = LocalDateTime.now();
        fromAccount.setLastTransaction(now);
        toAccount.setLastTransaction(now);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Log transactions
        transactionService.logTransaction(fromAccountId, amount.negate(), "Transfer to " + toAccountId);
        transactionService.logTransaction(toAccountId, amount, "Transfer from " + fromAccountId);
        transactionService.logTransfer(fromAccountId, toAccountId, amount);
    }

    public BigDecimal getBalance(String accountId) throws BankingException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId))
                .getBalance();
    }

    public String getAccountTypeById(String accountId) {
        return accountRepository.findAccountTypeById(accountId);
    }

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findByOrderByCreatedAtDesc().stream()
                .map(AccountDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAccount(String accountId) throws BankingException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // Delete the account and its transactions
        accountRepository.delete(account);
    }

    @Transactional
    public void processMonthlyFees() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            account.processMonthlyFees();
            accountRepository.save(account);

            // Log the fee transaction
            BigDecimal fee = BigDecimal.ZERO;
            if (account instanceof CheckingAccount) {
                fee = new BigDecimal("-12.00"); // Monthly fee
            } else if (account instanceof SavingsAccount) {
                // For savings, calculate the interest
                SavingsAccount savingsAccount = (SavingsAccount) account;
                fee = account.getBalance().multiply(savingsAccount.getInterestRate());
            }

            if (fee.compareTo(BigDecimal.ZERO) != 0) {
                String description = fee.compareTo(BigDecimal.ZERO) > 0 ?
                        "Monthly interest" : "Monthly fee";
                transactionService.logTransaction(account.getAccountNumber(), fee, description);
            }
        }
    }

    public Map<String, Object> getAccountSummary() {
        Map<String, Object> summary = new HashMap<>();

        summary.put("totalAccounts", accountRepository.countTotalAccounts());

        BigDecimal totalBalance = accountRepository.sumTotalBalance();
        summary.put("totalBalance", totalBalance != null ? totalBalance : BigDecimal.ZERO);

        return summary;
    }

    public Map<String, Object> getDailyTransactions() {
        Map<String, Object> report = new HashMap<>();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        BigDecimal deposits = transactionRepository.sumDepositsAfterDate(startOfDay);
        report.put("totalDeposits", deposits != null ? deposits : BigDecimal.ZERO);

        BigDecimal withdrawals = transactionRepository.sumWithdrawalsAfterDate(startOfDay);
        report.put("totalWithdrawals", withdrawals != null ? withdrawals : BigDecimal.ZERO);

        return report;
    }

    public Map<String, Object> getAccountActivity() {
        Map<String, Object> report = new HashMap<>();

        // Find most active account
        List<Object[]> mostActive = transactionRepository.findMostActiveAccount();
        if (!mostActive.isEmpty()) {
            Object[] result = mostActive.get(0);
            report.put("mostActiveAccount", result[0].toString());
            report.put("transactionCount", result[1]);
        }

        // Find highest balance account
        accountRepository.findHighestBalanceAccount().ifPresent(account -> {
            report.put("highestBalanceAccount", account.getAccountNumber());
            report.put("highestBalance", account.getBalance());
        });

        return report;
    }
}
