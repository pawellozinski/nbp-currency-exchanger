package com.ploz.currency_exchanger.data.nbp;

import com.google.api.client.util.Key;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class TableAorB {

    public static final Type listType = new TypeToken<List<TableAorB>>() {
    }.getType();

    @Key
    private String table;
    @Key("no")
    private String number;
    @Key
    private String effectiveDate;
    @Key
    private List<TableAorBRate> rates;

    public String getTable() {
        return table;
    }

    public String getNumber() {
        return number;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public List<TableAorBRate> getRates() {
        return Collections.unmodifiableList(rates);
    }
}

