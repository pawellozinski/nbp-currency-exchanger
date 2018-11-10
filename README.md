# NBP currency exchanger

### budowanie aplikacji i wdrożenie na serwer aplikacyjny Tomcat
`mvn clean package && cp target/currency-exchanger.war $CATLINA_HOME/webapps/`

### uruchomienie
`export CATALINA_OPTS="-Dspring.config.location=file:/tmp/application.properties"`

`./catalina.sh run`

### konfiguracja (/tmp/application.properties)

przeliczanie walut tabelą C (kursy kupna/sprzedaży)

`currency-exchanger.implementation=BidAskCurrencyExchanger`

przeliczanie walut tabelami A i B (kursy średnie)

`currency-exchanger.implementation=MeanValueCurrencyExchanger`
