package pl.hmarchwat.pg;

public class ExchangeRate {
    public String exchangeDate;
    public String bid;
    public String ask;

    public ExchangeRate(String exchangeDate, String bid, String ask) {
        this.exchangeDate = exchangeDate;
        this.bid = bid;
        this.ask = ask;
    }

    public String getExchangeDate() {
        return exchangeDate;
    }

    public String getBid() {
        return bid;
    }

    public String getAsk() {
        return ask;
    }
}
