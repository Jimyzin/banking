package com.assignment.sahaj.banking.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PropertyUtil {

    @Value("${banking.balance.limit.minimum}")
    double minimumBalanceLimit;

    @Value("${banking.balance.limit.maximum}")
    double maximumBalanceLimit;

    @Value("${banking.daily.limit.credit}")
    int dailyCreditLimit;

    @Value("${banking.daily.limit.debit}")
    int dailyDebitLimit;

    @Value("${banking.withdrawal.limit.minimum}")
    double minimumWithdrawalLimit;

    @Value("${banking.withdrawal.limit.maximum}")
    double maximumWithdrawalLimit;

    @Value("${banking.deposit.limit.minimum}")
    double minimumDepositLimit;

    @Value("${banking.deposit.limit.maximum}")
    double maximumDepositLimit;

}
