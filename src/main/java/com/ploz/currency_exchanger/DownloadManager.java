package com.ploz.currency_exchanger;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;

@Component
public class DownloadManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpRequestFactory requestFactory;
    private JsonObjectParser jsonObjectParser;

    @PostConstruct
    public void init() {
        requestFactory = new NetHttpTransport().createRequestFactory();
        jsonObjectParser = new JsonObjectParser(new JacksonFactory());
    }

    public synchronized <T> Optional<T> download(
            GenericUrl url,
            Type parsedType,
            Function<Object, Optional<T>> parser) {
        try {
            HttpRequest httpRequest = requestFactory.buildGetRequest(url);
            httpRequest.setParser(jsonObjectParser);
            logger.info("Requesting: {}...", url);
            HttpResponse httpResponse = httpRequest.execute();
            int statusCode = httpResponse.getStatusCode();
            logger.info("Response status: {}.", statusCode);
            if (statusCode == HttpStatusCodes.STATUS_CODE_OK) {
                Object parseResult = httpResponse.parseAs(parsedType);
                return parser.apply(parseResult);
            } else {
                logger.warn("Failed to download latest currency rates, status code: {}", statusCode);
                return Optional.empty();
            }
        } catch (IOException e) {
            logger.warn("Failed to download latest currency rates", e);
            return Optional.empty();
        }
    }


    public GenericUrl createUrl(String tableType) {
        return new GenericUrl("http://api.nbp.pl/api/exchangerates/tables/"+tableType+"?format=json");
    }

}
