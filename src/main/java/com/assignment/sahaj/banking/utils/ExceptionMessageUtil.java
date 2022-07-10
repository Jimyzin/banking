package com.assignment.sahaj.banking.utils;

import org.springframework.stereotype.Component;

import static com.assignment.sahaj.banking.utils.Messages.TRANSFER_EXCEPTION_MESSAGE;

@Component
public class ExceptionMessageUtil {


    public String formatExceptionMessage(boolean isTransfer, String formattedMessage, Long accountNumber) {

        if (isTransfer) {
            return formattedMessage.concat(String.format(TRANSFER_EXCEPTION_MESSAGE, accountNumber));
        } else {
            return formattedMessage;
        }
    }
}
