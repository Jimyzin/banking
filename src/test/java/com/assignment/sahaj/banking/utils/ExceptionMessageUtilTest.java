package com.assignment.sahaj.banking.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ExceptionMessageUtilTest {

    @Autowired
    private ExceptionMessageUtil exceptionMessageUtil;

    @Test
    public void givenIsTransferIsTrue_whenFormatExceptionMessage_thenReturnMessageWithAccountNumber() {
        var accountNumber = 1000L;
        assertThat(exceptionMessageUtil.formatExceptionMessage(true,
                String.format(Messages.MINIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE, 1200.00),
                accountNumber))
                .contains(String.format("%d", accountNumber));
    }

    @Test
    public void givenIsTransferIsFalse_whenFormatExceptionMessage_thenReturnMessageWithoutAccountNumber() {
        var accountNumber = 1000L;
        assertThat(exceptionMessageUtil.formatExceptionMessage(false,
                String.format(Messages.MINIMUM_WITHDRAWAL_LIMIT_EXCEPTION_MESSAGE, 1200.00),
                accountNumber).contains(String.format("%d", accountNumber)))
                .isFalse();
    }
}
