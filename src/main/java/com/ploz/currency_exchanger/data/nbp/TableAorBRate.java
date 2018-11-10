package com.ploz.currency_exchanger.data.nbp;

import com.google.api.client.util.Key;

public class TableAorBRate {
    @Key
    private String currency;
    @Key
    private String code;
    @Key
    private double mid;

    public String getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public double getMid() {
        return mid;
    }
}
