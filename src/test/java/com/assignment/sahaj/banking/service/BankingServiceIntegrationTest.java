package com.assignment.sahaj.banking.service;

import com.assignment.sahaj.banking.repository.AccountRepository;
import com.assignment.sahaj.banking.utils.PropertyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.assignment.sahaj.banking.utils.Messages.INVALID_ACCOUNT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.INVALID_NAME_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_DAILY_CREDIT_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_DAILY_DEBIT_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MAXIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MINIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE;
import static com.assignment.sahaj.banking.utils.Messages.MINIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BankingServiceIntegrationTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private BankingService bankingService;
    @Autowired
    private PropertyUtil propertyUtil;

    @Test
    public void givenValidName_whenCreateAccount_thenSuccessful() {
        var accountNumber = bankingService.createAccount("John Doe");
        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber).isGreaterThanOrEqualTo(1001);
    }

    @Test
    public void givenNullName_whenCreateAccount_thenThrowIllegalArgumentException() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.createAccount(null));
        assertThat(exception.getMessage()).isEqualTo(INVALID_NAME_EXCEPTION_MESSAGE);
    }

    @Test
    public void givenBlankName_whenCreateAccount_thenThrowIllegalArgumentException() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.createAccount(""));
        assertThat(exception.getMessage()).isEqualTo(INVALID_NAME_EXCEPTION_MESSAGE);
    }

    @Test
    public void givenValidBankAccount_whenDepositedValidAmount_thenSuccessful() {
        var amount = (propertyUtil.getMinimumDepositLimit()
                + propertyUtil.getMaximumDepositLimit()) / 2; // Minimum Deposit Limit < amount < Maximum Deposit Limit
        var accountNumber = bankingService.createAccount("John Doe");
        var balance = bankingService.deposit(accountNumber, amount);
        assertThat(balance).isEqualTo(amount);
    }

    @Test
    public void givenValidBankAccount_whenDepositedAmountLessThanMinimumDepositLimit_thenThrowException() {
        var amount = propertyUtil.getMinimumDepositLimit() - 1; // amount < Minimum Deposit Limit
        var accountNumber = bankingService.createAccount("John Doe");
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.deposit(accountNumber, amount));
        assertThat(exception.getMessage()).isEqualTo(
                String.format(MINIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMinimumDepositLimit()));
    }

    @Test
    public void givenValidBankAccount_whenDepositedAmountGreaterThanMaximumDepositLimit_thenThrowException() {
        var amount = propertyUtil.getMaximumDepositLimit() + 1; // amount > Maximum Deposit Limit
        var accountNumber = bankingService.createAccount("John Doe");
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.deposit(accountNumber, amount));
        assertThat(exception.getMessage()).isEqualTo(
                String.format(MAXIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMaximumDepositLimit()));
    }

    @Test
    public void givenInvalidBankAccount_whenDepositedValidAmount_thenThrowException() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.deposit(100L, 1000.00));
        assertThat(exception.getMessage()).isEqualTo(INVALID_ACCOUNT_EXCEPTION_MESSAGE);
    }

    @Test
    public void givenValidBankAccount_whenDepositedValidAmountBeyondMaximumBalanceLimit_thenThrowException() {
        var accountNumber = bankingService.createAccount("John Doe");
        accountRepository.findById(accountNumber)
                .ifPresent(account -> {
                    account.credit(propertyUtil.getMaximumBalanceLimit()); // deposit maximum balance limit
                    accountRepository.saveAndFlush(account);
                });
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.deposit(accountNumber, 1000.00));
        assertThat(exception.getMessage()).contains(String.format("cannot exceed $%.2f", propertyUtil.getMaximumBalanceLimit()));
    }

    @Test
    public void givenValidBankAccount_whenDepositedValidAmountBeyondDailyDepositLimit_thenThrowException() {
        var accountNumber = bankingService.createAccount("John Doe");
        for (int i = 0; i < propertyUtil.getDailyCreditLimit(); i++) {
            bankingService.deposit(accountNumber, propertyUtil.getMinimumDepositLimit());
        }
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.deposit(accountNumber, 1000.00));
        assertThat(exception.getMessage()).isEqualTo(String.format(
                MAXIMUM_DAILY_CREDIT_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getDailyCreditLimit()));
    }

    @Test
    public void givenValidAccount_whenGetBalance_thenSuccessful() {
        var deposit = (propertyUtil.getMinimumDepositLimit() + propertyUtil.getMaximumDepositLimit()) / 2;
        var accountNumber = bankingService.createAccount("John Doe");
        bankingService.deposit(accountNumber, deposit);
        var balance = bankingService.getBalance(accountNumber);
        assertThat(balance).isEqualTo(deposit);
    }

    @Test
    public void givenInvalidAccount_whenGetBalance_thenThrowException() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.getBalance(2L));
        assertThat(exception.getMessage()).isEqualTo("Account does not exist");
    }

    @Test
    public void givenValidBankAccount_whenWithdrawnValidAmount_thenSuccessful() {
        var amount = (propertyUtil.getMinimumDepositLimit()
                + propertyUtil.getMaximumDepositLimit()) / 2; // Minimum Withdrawal Limit < amount < Maximum Withdrawal Limit
        var accountNumber = bankingService.createAccount("John Doe");
        bankingService.deposit(accountNumber, amount); // deposit amount
        var balance = bankingService.withdraw(accountNumber, amount / 2); // withdraw half of amount
        assertThat(balance).isEqualTo(amount / 2); // remaining balance is half of amount
    }

    @Test
    public void givenValidBankAccount_whenWithdrawnAmountLessThanMinimumWithdrawalLimit_thenThrowException() {
        var accountNumber = bankingService.createAccount("John Doe");
        bankingService.deposit(accountNumber, propertyUtil.getMaximumDepositLimit());
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.withdraw(accountNumber, propertyUtil.getMinimumWithdrawalLimit() - 1));
        assertThat(exception.getMessage()).isEqualTo(
                String.format(MINIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMinimumWithdrawalLimit()));
    }

    @Test
    public void givenValidBankAccount_whenWithdrawnAmountGreaterThanMaximumWithdrawalLimit_thenThrowException() {
        var accountNumber = bankingService.createAccount("John Doe");
        bankingService.deposit(accountNumber, propertyUtil.getMaximumDepositLimit());
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.withdraw(accountNumber, 26000.00));
        assertThat(exception.getMessage()).isEqualTo(
                String.format(MAXIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMaximumWithdrawalLimit()));
    }

    @Test
    public void givenInvalidBankAccount_whenWithdrawnValidAmount_thenThrowException() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.withdraw(100L, 1000.00));
        assertThat(exception.getMessage()).isEqualTo(INVALID_ACCOUNT_EXCEPTION_MESSAGE);
    }

    @Test
    public void givenValidBankAccount_whenWithdrawnValidAmountBeyondMinimumBalanceLimit_thenThrowException() {
        var accountNumber = bankingService.createAccount("John Doe");
        accountRepository.findById(accountNumber)
                .ifPresent(account -> {
                    account.credit(propertyUtil.getMinimumBalanceLimit()); // account balance == minimum balance limit
                    accountRepository.saveAndFlush(account);
                });
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.withdraw(accountNumber, propertyUtil.getMinimumWithdrawalLimit()));
        assertThat(exception.getMessage()).isEqualTo("Insufficient Balance");
    }

    @Test
    public void givenValidBankAccount_whenWithdrawnValidAmountBeyondDailyDebitLimit_thenThrowException() {
        var accountNumber = bankingService.createAccount("John Doe");
        accountRepository.findById(accountNumber)
                .ifPresent(account -> {
                    account.credit(propertyUtil.getMaximumBalanceLimit()); // account balance == maximum balance limit
                    accountRepository.saveAndFlush(account);
                });
        for (int i = 0; i < propertyUtil.getDailyDebitLimit(); i++) {
            bankingService.withdraw(accountNumber, propertyUtil.getMinimumWithdrawalLimit());
        }
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.withdraw(accountNumber, propertyUtil.getMinimumWithdrawalLimit()));
        assertThat(exception.getMessage()).isEqualTo(
                String.format(MAXIMUM_DAILY_DEBIT_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getDailyDebitLimit()));
    }

    @Test
    public void givenValidAccounts_whenTransferredValidAmount_thenSuccessful() {
        var deposit = propertyUtil.getMaximumDepositLimit();
        var transfer = propertyUtil.getMinimumWithdrawalLimit();
        var sourceAccountNumber = bankingService.createAccount("John Doe");
        var destinationAccountNumber = bankingService.createAccount("Jane Doe");
        bankingService.deposit(sourceAccountNumber, deposit);
        assertThat(bankingService.transferFund(sourceAccountNumber, destinationAccountNumber, transfer)).
                isEqualTo("Success");
    }

    @Test
    public void givenValidAccounts_whenTransferredAmountLesserThanMinimumWithdrawalLimit_thenException() {
        var deposit = propertyUtil.getMaximumDepositLimit();
        var transfer = propertyUtil.getMinimumWithdrawalLimit() - 1;
        var sourceAccountNumber = bankingService.createAccount("John Doe");
        var destinationAccountNumber = bankingService.createAccount("Jane Doe");
        bankingService.deposit(sourceAccountNumber, deposit);
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.transferFund(sourceAccountNumber, destinationAccountNumber, transfer));
        assertThat(exception.getMessage()).startsWith(
                String.format(MINIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMinimumWithdrawalLimit()));
    }

    @Test
    public void givenValidAccounts_whenTransferredAmountGreaterThanMaximumWithdrawalLimit_thenException() {
        var deposit = propertyUtil.getMaximumDepositLimit();
        var transfer = propertyUtil.getMaximumWithdrawalLimit() + 1;
        var sourceAccountNumber = bankingService.createAccount("John Doe");
        var destinationAccountNumber = bankingService.createAccount("Jane Doe");
        bankingService.deposit(sourceAccountNumber, deposit);
        var exception = assertThrows(IllegalArgumentException.class,
                () -> bankingService.transferFund(sourceAccountNumber, destinationAccountNumber, transfer));
        assertThat(exception.getMessage()).startsWith(
                String.format(MAXIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE, propertyUtil.getMaximumWithdrawalLimit()));
    }

}
