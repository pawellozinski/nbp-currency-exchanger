package com.ploz.currency_exchanger.exchangers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CurrenctExchangerConfiguration {


    @Value("${currency-exchanger.implementation:" + MeanValueCurrencyExchanger.NAME + "}")
    private String beanName;
    @Autowired
    ApplicationContext applicationContext;

    @Primary
    @Bean
    public CurrencyExchanger currencyExchanger() {
        return  applicationContext.getBean(beanName, CurrencyExchanger.class);
    }
}
