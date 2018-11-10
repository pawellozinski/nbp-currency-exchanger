package com.ploz.currency_exchanger.data.nbp;

import com.google.api.client.util.Key;

public class TableCRate {
    @Key
    private String currency;
    @Key
    private String code;
    @Key
    private double bid;
    @Key
    private double ask;

    public String getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }
}
