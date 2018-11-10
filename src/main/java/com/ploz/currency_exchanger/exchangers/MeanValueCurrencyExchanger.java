package com.ploz.currency_exchanger.exchangers;

import com.google.api.client.http.GenericUrl;
import com.ploz.currency_exchanger.DownloadManager;
import com.ploz.currency_exchanger.data.nbp.TableAorB;
import com.ploz.currency_exchanger.data.nbp.TableAorBRate;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Lazy
@Component(MeanValueCurrencyExchanger.NAME)
public class MeanValueCurrencyExchanger implements CurrencyExchanger {

    public static final String NAME = "MeanValueCurrencyExchanger";

    private static final Logger logger = LoggerFactory.getLogger(new Object() {}.getClass().getEnclosingClass());

    private final AtomicReference<Map<String, TableAorBRate>> rates = new AtomicReference<>(Collections.emptyMap());

    @Autowired
    private DownloadManager downloadManager;

    public List<String> getCodes() {
        return Observable.fromIterable(rates.get().keySet()).sorted().toList().blockingGet();
    }

    public boolean isKnown(String code) {
        return rates.get().containsKey(code);
    }

    public double exchange(double amount, String from, String to) {
        Map<String, TableAorBRate> rateMap = rates.get();
        double fromBid = rateMap.get(from).getMid();
        double toAsk = rateMap.get(to).getMid();
        return amount*fromBid/toAsk;
    }

    @Scheduled(fixedRate = 24*3600*1000L)
    public void refreshCurrencyExchangeRates() {
        Optional<TableAorB> optionalTableA = download("a");
        Optional<TableAorB> optionalTableB = download("b");
        updateCurrencyRates(optionalTableA, optionalTableB);
    }

    private void updateCurrencyRates(Optional<TableAorB> tableA, Optional<TableAorB> tableB) {
        Observable<TableAorBRate> observable;
        if (tableA.isPresent()) {
            observable = Observable.fromIterable(tableA.get().getRates());
        } else {
            observable = Observable.empty();
        }
        if (tableB.isPresent()) {
            observable = observable.mergeWith(Observable.fromIterable(tableB.get().getRates()));
        }
        Map<String, TableAorBRate> rateMap = observable
                .toMap(rate -> rate.getCode())
                .blockingGet();
        rates.set(rateMap);
        logger.info("Rates switched with new version.");
    }

    public Optional<TableAorB> download(String tableType) {
        GenericUrl url = downloadManager.createUrl(tableType);
        Optional<TableAorB> optionalTable = downloadManager.download(url, TableAorB.listType, parseResult -> {
            List<TableAorB> tableList = (List<TableAorB>) parseResult;
            if (tableList.size() == 1) {
                TableAorB table = tableList.get(0);
                logger.info("Got {} rates from table {}", table.getRates().size(), tableType);
                return Optional.of(table);
            } else {
                logger.warn("Expected single-element list, but got list with {} elements", tableList.size());
                return Optional.empty();
            }
        });
        return optionalTable;
    }

}
