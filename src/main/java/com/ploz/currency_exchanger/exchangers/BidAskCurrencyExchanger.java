package com.ploz.currency_exchanger.exchangers;

import com.google.api.client.http.GenericUrl;
import com.ploz.currency_exchanger.DownloadManager;
import com.ploz.currency_exchanger.data.nbp.TableC;
import com.ploz.currency_exchanger.data.nbp.TableCRate;
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
@Component(BidAskCurrencyExchanger.NAME)
public class BidAskCurrencyExchanger implements CurrencyExchanger {

    public static final String NAME = "BidAskCurrencyExchanger";

    private static final Logger logger = LoggerFactory.getLogger(new Object() {}.getClass().getEnclosingClass());

    private final AtomicReference<Map<String, TableCRate>> rates = new AtomicReference<>(Collections.emptyMap());

    @Autowired
    private DownloadManager downloadManager;

    public List<String> getCodes() {
        return Observable.fromIterable(rates.get().keySet()).sorted().toList().blockingGet();
    }

    public boolean isKnown(String code) {
        return rates.get().containsKey(code);
    }

    public double exchange(double amount, String from, String to) {
        Map<String, TableCRate> rateMap = rates.get();
        double fromBid = rateMap.get(from).getBid();
        double toAsk = rateMap.get(to).getAsk();
        return amount*fromBid/toAsk;
    }

    @Scheduled(fixedRate = 24*3600*1000L)
    public void refreshCurrencyExchangeRates() {
        Optional<TableC> optionalTableC = download();
        optionalTableC.ifPresent(this::updateCurrencyRates);
    }

    private void updateCurrencyRates(TableC tableC) {
        Map<String, TableCRate> rateMap = Observable.fromIterable(tableC.getRates())
                .toMap(rate -> rate.getCode())
                .blockingGet();
        rates.set(rateMap);
        logger.info("Rates switched with new version.");
    }

    public Optional<TableC> download() {
        GenericUrl url = downloadManager.createUrl("c");
        Optional<TableC> optionalTableC = downloadManager.download(url, TableC.listType, parseResult -> {
            List<TableC> tableCList = (List<TableC>) parseResult;
            if (tableCList.size() == 1) {
                TableC tableC = tableCList.get(0);
                logger.info("Got {} rates", tableC.getRates().size());
                return Optional.of(tableC);
            } else {
                logger.warn("Expected single-element list, but got list with {} elements", tableCList.size());
                return Optional.empty();
            }
        });
        return optionalTableC;
    }

}
