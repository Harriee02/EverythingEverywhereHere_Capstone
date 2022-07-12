package com.example.everythingeverywherehere.models;

public class ProductModelWalmart {
    private String title;
    private String product_page_url;
    private String thumbnail;
    private float rating;
    private PrimaryOfferWalmart primary_offer;


    // Getter Methods

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return product_page_url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public float getRating() {
        return rating;
    }

    public PrimaryOfferWalmart getPrice() {
        return primary_offer;
    }

    // Setter Methods

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.product_page_url = link;
    }

    public void setThumbnail(String image) {
        this.thumbnail = image;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setPrice(PrimaryOfferWalmart priceObject) {
        this.primary_offer = priceObject;
    }
}
