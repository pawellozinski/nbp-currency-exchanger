package com.ploz.currency_exchanger.data.api;

import java.util.Collections;
import java.util.List;

public class Result {
    public Status status;
    public double result;
    public String message;
    public List<ErrorCode> errorCodes;

    public Result() {
    }

    public Result(double result) {
        this(Status.OK, "", result, Collections.emptyList());
    }

    public Result(Status status, String message, List<ErrorCode> errorCodes) {
        this(status, message, -1, errorCodes);
    }

    private Result(Status status, String message, double result, List<ErrorCode> errorCodes) {
        this.status = status;
        this.message = message;
        this.result = result;
        this.errorCodes = errorCodes;
    }
}
