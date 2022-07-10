package com.assignment.sahaj.banking.service;

import com.assignment.sahaj.banking.entity.Account;
import com.assignment.sahaj.banking.entity.Customer;
import com.assignment.sahaj.banking.repository.AccountRepository;
import com.assignment.sahaj.banking.repository.CustomerRepository;
import com.assignment.sahaj.banking.utils.ExceptionMessageUtil;
import com.assignment.sahaj.banking.utils.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_DAILY_CREDIT_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_DAILY_DEBIT_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_ACCOUNT_BALANCE_EXCEPTION_MESSAGE;

@Service
public class BankingService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final ValidationService validationService;
    private final ExceptionMessageUtil exceptionMessageUtil;
    private final PropertyUtil propertyUtil;

    @Autowired
    public BankingService(CustomerRepository customerRepository,
                          AccountRepository accountRepository,
                          ValidationService validationService,
                          ExceptionMessageUtil exceptionMessageUtil,
                          PropertyUtil propertyUtil) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.validationService = validationService;
        this.exceptionMessageUtil = exceptionMessageUtil;
        this.propertyUtil = propertyUtil;
    }

    @Transactional
    public Long createAccount(String name) {

        validationService.validateName(name);

        return accountRepository.saveAndFlush(Account.builder()
                .balance(0.0)
                .dailyCreditCount(0)
                .dailyDebitCount(0)
                .lastCreditDate(LocalDate.now())
                .lastDebitDate(LocalDate.now())
                .customer(customerRepository
                        .saveAndFlush(
                                Customer.builder()
                                        .name(name)
                                        .build()))
                .build()
        ).getAccountNumber();
    }

    @Transactional
    public String transferFund(Long sourceAccountNumber, Long destinationAccountNumber, Double amount) {
        withdraw(sourceAccountNumber, amount, true);
        deposit(destinationAccountNumber, amount, true);

        return "Success";

    }

    @Transactional
    public Double deposit(Long accountNumber, Double amount) {
        return deposit(accountNumber, amount, false);
    }

    @Transactional
    public Double withdraw(Long accountNumber, Double amount) {
        return withdraw(accountNumber, amount, false);
    }

    private Double withdraw(Long accountNumber, Double amount, boolean isTransfer) {

        final var balance = new AtomicReference<Double>();

        validationService.validateWithdrawAmount(amount, isTransfer, accountNumber);

        validationService.ifValidAccountNumber(accountNumber,
                account -> {
                    if (account.debit(amount) >= propertyUtil.getMinimumBalanceLimit()) {
                        if (isWithinDailyDebitLimit(account)) {
                            balance.set(account.getBalance());
                            account.incrementDailyDebitCount();
                            accountRepository.saveAndFlush(account);

                        } else {
                            throw new IllegalArgumentException(
                                    exceptionMessageUtil.formatExceptionMessage(isTransfer,
                                            String.format(MAXIMUM_DAILY_DEBIT_LIMIT_EXCEPTION_MESSAGE,
                                                    propertyUtil.getDailyCreditLimit()),
                                            accountNumber));
                        }
                    } else {
                        throw new IllegalArgumentException(exceptionMessageUtil.formatExceptionMessage(
                                isTransfer,
                                "Insufficient Balance", accountNumber));
                    }
                },
                isTransfer);

        return balance.get();

    }

    private Double deposit(Long accountNumber, Double amount, boolean isTransfer) {

        final var balance = new AtomicReference<Double>();
        validationService.validateDepositAmount(amount, isTransfer, accountNumber);

        validationService.ifValidAccountNumber(accountNumber,
                account -> {
                    if (account.credit(amount) <= propertyUtil.getMaximumBalanceLimit()) {
                        if (isWithinDailyCreditLimit(account)) {
                            balance.set(account.getBalance());
                            account.incrementDailyCreditCount();
                            accountRepository.saveAndFlush(account);

                        } else {
                            throw new IllegalArgumentException(exceptionMessageUtil.formatExceptionMessage(isTransfer,
                                    String.format(MAXIMUM_DAILY_CREDIT_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getDailyCreditLimit()),
                                    accountNumber));
                        }
                    } else {
                        throw new IllegalArgumentException(exceptionMessageUtil.formatExceptionMessage(isTransfer,
                                String.format(MAXIMUM_ACCOUNT_BALANCE_EXCEPTION_MESSAGE, account.getAccountNumber(),
                                        propertyUtil.getMaximumBalanceLimit()), accountNumber));
                    }
                },
                isTransfer
        );

        return balance.get();

    }

    public Double getBalance(Long accountNumber) {
        var balance = new AtomicReference<Double>();
        validationService.ifValidAccountNumber(accountNumber,
                account -> {
                    balance.set(account.getBalance());
                },
                false);
        return balance.get();
    }

    private boolean isWithinDailyCreditLimit(Account account) {
        var now = LocalDate.now();

        if (now.isAfter(account.getLastCreditDate())) {
            account.resetDailyCreditCount();
            return true;
        } else {
            // if the last credit was performed today
            return account.getDailyCreditCount() < propertyUtil.getDailyCreditLimit();
        }
    }

    private boolean isWithinDailyDebitLimit(Account account) {
        var now = LocalDate.now();

        if (now.isAfter(account.getLastCreditDate())) {
            account.resetDailyDebitCount();
            return true;
        } else {
            // if the last debit was performed today
            return account.getDailyDebitCount() < propertyUtil.getDailyDebitLimit();
        }
    }

}
