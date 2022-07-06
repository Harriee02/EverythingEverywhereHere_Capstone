package com.example.everythingeverywherehere.models;

import org.parceler.Parcel;

@Parcel
public class PrimaryOfferWalmart {
    private String offer_id;
    private float offer_price;
    private int minPrice;


    // Getter Methods

    public String getOfferId() {
        return offer_id;
    }

    public float getOfferPrice() {
        return offer_price;
    }

    public int getMinPrice() {
        return minPrice;
    }


    // Setter Methods

    public void setOfferId(String offer_id) {
        this.offer_id = offer_id;
    }

    public void setOfferPrice(float offer_price) {
        this.offer_price = offer_price;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

}
