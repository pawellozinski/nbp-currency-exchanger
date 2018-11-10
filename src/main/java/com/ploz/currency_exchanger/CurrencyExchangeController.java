package com.ploz.currency_exchanger;

import com.ploz.currency_exchanger.data.api.ErrorCode;
import com.ploz.currency_exchanger.data.api.Result;
import com.ploz.currency_exchanger.data.api.Status;
import com.ploz.currency_exchanger.exchangers.CurrencyExchanger;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class CurrencyExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(new Object() {}.getClass().getEnclosingClass());

    @Autowired
    CurrencyExchanger currencyExchanger;

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result handleMissingParams(MissingServletRequestParameterException ex) {
        return new Result(Status.ERROR, getMissingParamMsg(ex), Collections.singletonList(ErrorCode.MISSING_PARAMETER));
    }

    private String getMissingParamMsg(MissingServletRequestParameterException ex) {
        return String.format(
                "Missing parameter: %s. Required parameters are: 'amount' (non-negative double), " +
                        "'from' (string) and 'to' (string)", ex.getParameterName());
    }

    @GetMapping("/")
    public Result exchange(@RequestParam double amount, @RequestParam String from, String to) {
        List<ErrorCode> errorCodes = getErrorCodes(amount, from, to);
        if (errorCodes.isEmpty()) {
            double result = currencyExchanger.exchange(amount, from, to);
            return new Result(result);
        } else {
            String errorMessage = toErrorMessage(errorCodes, from, to);
            return new Result(Status.ERROR, errorMessage, errorCodes);
        }
    }

    private List<ErrorCode> getErrorCodes(double amount, String from, String to) {
        List<ErrorCode> errorCodes = new ArrayList<>();
        if (amount < 0) {
            errorCodes.add(ErrorCode.AMOUNT_NEGATIVE);
        }
        if (!currencyExchanger.isKnown(from)) {
            errorCodes.add(ErrorCode.FROM_UNKNOWN);
        }
        if (!currencyExchanger.isKnown(to)) {
            errorCodes.add(ErrorCode.TO_UNKNOWN);
        }
        return errorCodes;
    }

    private String toErrorMessage(List<ErrorCode> errorCodes, String from, String to) {
        Set<ErrorCode> errorCodeSet = Observable.fromIterable(errorCodes).toMap(c -> c).blockingGet().keySet();

        StringBuilder message = new StringBuilder();
        if (errorCodeSet.contains(ErrorCode.FROM_UNKNOWN)) {
            message.append(from).append(" is unknown");
        }
        if (errorCodeSet.contains(ErrorCode.TO_UNKNOWN)) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append(to).append(" is unknown");
        }
        if (errorCodeSet.contains(ErrorCode.AMOUNT_NEGATIVE)) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append("amount cannot be negative");
        }
        if (errorCodeSet.contains(ErrorCode.FROM_UNKNOWN) || errorCodes.contains(ErrorCode.TO_UNKNOWN)) {
            message.append(", known currencies are: ").append(currencyExchanger.getCodes());
        }
        return message.toString();
    }
}
