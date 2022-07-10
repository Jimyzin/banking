package com.assignment.sahaj.banking.service;

import com.assignment.sahaj.banking.entity.Account;
import com.assignment.sahaj.banking.repository.AccountRepository;
import com.assignment.sahaj.banking.utils.ExceptionMessageUtil;
import com.assignment.sahaj.banking.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static com.assignment.sahaj.banking.utils.Messages.INVALID_ACCOUNT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.INVALID_NAME_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MINIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MINIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE;

@Service
public class ValidationService {

    private final ExceptionMessageUtil exceptionMessageUtil;
    private final AccountRepository accountRepository;
    private final PropertyUtil propertyUtil;

    @Autowired
    public ValidationService(ExceptionMessageUtil exceptionMessageUtil,
                             AccountRepository accountRepository,
                             PropertyUtil propertyUtil) {
        this.exceptionMessageUtil = exceptionMessageUtil;
        this.accountRepository = accountRepository;
        this.propertyUtil = propertyUtil;
    }

    public void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException(INVALID_NAME_EXCEPTION_MESSAGE);
        }
    }

    public void validateWithdrawAmount(Double amount, boolean isTransfer, Long accountNumber) {
        if (amount < propertyUtil.getMinimumWithdrawalLimit()) {
            throw new IllegalArgumentException(exceptionMessageUtil.formatExceptionMessage(
                    isTransfer,
                    String.format(MINIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMinimumWithdrawalLimit()),
                    accountNumber));
        }

        if (amount > propertyUtil.getMaximumWithdrawalLimit()) {
            throw new IllegalArgumentException(exceptionMessageUtil.formatExceptionMessage(
                    isTransfer,
                    String.format(MAXIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMaximumWithdrawalLimit()),
                    accountNumber));
        }
    }

    public void validateDepositAmount(Double amount, boolean isTransfer, Long accountNumber) {
        if (amount < propertyUtil.getMinimumDepositLimit()) {
            throw new IllegalArgumentException(exceptionMessageUtil.formatExceptionMessage(
                    isTransfer,
                    String.format(MINIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMinimumDepositLimit()),
                    accountNumber));
        }

        if (amount > propertyUtil.getMaximumDepositLimit()) {
            throw new IllegalArgumentException(exceptionMessageUtil.formatExceptionMessage(
                    isTransfer,
                    String.format(MAXIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMaximumDepositLimit()),
                    accountNumber));
        }

    }

    public void ifValidAccountNumber(Long accountNumber, Consumer<Account> consumer, boolean isTransfer) {
        accountRepository.findById(accountNumber)
                .ifPresentOrElse((t) -> consumer.accept(t), () -> {
                    throw new IllegalArgumentException(exceptionMessageUtil.formatExceptionMessage(isTransfer,
                            INVALID_ACCOUNT_EXCEPTION_MESSAGE, accountNumber));
                });
    }
}
