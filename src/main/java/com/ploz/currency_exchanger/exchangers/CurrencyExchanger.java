package com.ploz.currency_exchanger.exchangers;

import java.util.List;

public interface CurrencyExchanger {

    public List<String> getCodes();

    public boolean isKnown(String code);

    public double exchange(double amount, String from, String to);

}
