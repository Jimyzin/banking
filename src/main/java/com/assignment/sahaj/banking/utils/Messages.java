package com.assignment.sahaj.banking.utils;

public interface Messages {

    public final String MAXIMUM_ACCOUNT_BALANCE_EXCEPTION_MESSAGE = "Account balance of %d cannot exceed $%.2f";
    public final String MAXIMUM_DAILY_CREDIT_LIMIT_EXCEPTION_MESSAGE = "Only %d deposits are allowed in a day";
    public final String MAXIMUM_DAILY_DEBIT_LIMIT_EXCEPTION_MESSAGE = "Only %d withdrawals are allowed in a day";
    public final String TRANSFER_EXCEPTION_MESSAGE = " for account %d";
    public final String INVALID_NAME_EXCEPTION_MESSAGE = "Name is blank or null";
    public final String INVALID_ACCOUNT_EXCEPTION_MESSAGE = "Account does not exist";
    public final String MINIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE = "Minimum withdrawal amount is $%.2f";
    public final String MAXIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE = "Maximum withdrawal amount is $%.2f";
    public final String MINIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE = "Minimum deposit amount is $%.2f";
    public final String MAXIMUM_DEPOSIT_LIMIT_EXCEPTION_MESSAGE = "Maximum deposit amount is $%.2f";
}
