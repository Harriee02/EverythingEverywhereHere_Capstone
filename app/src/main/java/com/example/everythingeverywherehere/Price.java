package com.example.everythingeverywherehere;

import org.parceler.Parcel;

@Parcel
public class Price {
    private String symbol;
    private float value;
    private String currency;
    private String raw;
    private String name;
    private boolean is_primary;


    // Getter Methods

    public String getSymbol() {
        return symbol;
    }

    public float getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    public String getRaw() {
        return raw;
    }

    public String getName() {
        return name;
    }

    public boolean getIs_primary() {
        return is_primary;
    }

    // Setter Methods

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIs_primary(boolean is_primary) {
        this.is_primary = is_primary;
    }
}