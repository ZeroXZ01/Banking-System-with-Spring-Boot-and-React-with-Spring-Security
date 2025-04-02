package com.roland.training.service;

import com.roland.training.model.Transaction;
import com.roland.training.repository.TransactionRepository;
import com.roland.training.util.FileReporter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FileReporter fileReporter;

    public void logTransaction(String accountId, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction = new Transaction(accountId, amount, now);
        transactionRepository.save(transaction);

        String activityType = amount.compareTo(BigDecimal.ZERO) > 0 ? "DEPOSIT" : "WITHDRAWAL";
        String logEntry = String.format("[%s] %s: Account %s - $%.2f", now.format(FORMATTER), activityType, accountId, amount.abs());
        try {
            fileReporter.logActivity(logEntry);
        } catch (IOException e) {
            throw new RuntimeException("Failed to log activity to file", e);
        }
    }

    public void logTransaction(String accountId, BigDecimal amount, Timestamp timestamp) {
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        Transaction transaction = new Transaction(accountId, amount, dateTime);
        transactionRepository.save(transaction);

        String activityType = amount.compareTo(BigDecimal.ZERO) > 0 ? "DEPOSIT" : "WITHDRAWAL";
        String logEntry = String.format("[%s] %s: Account %s - $%.2f", dateTime.format(FORMATTER), activityType, accountId, amount.abs());
        try {
            fileReporter.logActivity(logEntry);
        } catch (IOException e) {
            throw new RuntimeException("Failed to log activity to file", e);
        }
    }

    public void logTransfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        String logEntry = String.format("[%s] TRANSFER: From %s to %s - $%.2f", LocalDateTime.now().format(FORMATTER), fromAccountId, toAccountId, amount);
        try {
            fileReporter.logActivity(logEntry);
        } catch (IOException e) {
            throw new RuntimeException("Failed to log transfer activity to file", e);
        }
    }

    public List<String> readTransactionHistory() {
        return transactionRepository.findAllByOrderByTransactionDateDesc().stream()
                .map(t -> String.format("%s,%s,%.2f", t.getTransactionDate(), t.getAccountId(), t.getAmount()))
                .collect(Collectors.toList());
    }

    public List<String> readTransactionHistory(String accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId).stream()
                .map(t -> String.format("%s,%s,%.2f", t.getTransactionDate(), accountId, t.getAmount()))
                .collect(Collectors.toList());
    }

    public void clearTransactions() {
        transactionRepository.deleteAll();
    }
}

