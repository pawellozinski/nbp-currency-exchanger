package com.ploz.currency_exchanger.data.nbp;

import com.google.api.client.util.Key;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class TableC {

    public static final Type listType = new TypeToken<List<TableC>>() {
    }.getType();

    @Key
    private String table;
    @Key("no")
    private String number;
    @Key
    private String tradingDate;
    @Key
    private String effectiveDate;
    @Key
    private List<TableCRate> rates;

    public String getTable() {
        return table;
    }

    public String getNumber() {
        return number;
    }

    public String getTradingDate() {
        return tradingDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public List<TableCRate> getRates() {
        return Collections.unmodifiableList(rates);
    }
}

