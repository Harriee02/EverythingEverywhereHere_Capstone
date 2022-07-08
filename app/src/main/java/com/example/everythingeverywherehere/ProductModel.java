package com.example.everythingeverywherehere;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class ProductModel implements Comparable<ProductModel>{
    private String title;
    private String asin;
    private String link;
    private String image;
    private float rating;
    private float ratings_total;
    private boolean sponsored;
    private Price price;


    // Getter Methods

    public String getTitle() {
        return title;
    }

    public String getAsin() {
        return asin;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }

    public float getRating() {
        return rating;
    }

    public float getRatings_total() {
        return ratings_total;
    }

    public boolean getSponsored() {
        return sponsored;
    }

    public Price getPrice() {
        return price;
    }

    // Setter Methods

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setRatings_total(float ratings_total) {
        this.ratings_total = ratings_total;
    }

    public void setSponsored(boolean sponsored) {
        this.sponsored = sponsored;
    }

    public void setPrice(Price priceObject) {
        this.price = priceObject;
    }

    @Override
    public int compareTo(@NonNull ProductModel o) {
        return Float.compare(this.getPrice().getValue(), o.getPrice().getValue());
    }
}
