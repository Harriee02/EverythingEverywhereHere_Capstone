package com.example.everythingeverywherehere;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

public class ProductDetails extends AppCompatActivity {
    public static final String TAG = "PRODUCT_DETAILS";
    ProductModel productModel;
    ImageView productDetailsImg;
    TextView productDetailsDescription;
    TextView productDetailsRatingValue;
    RatingBar productDetailsRating;
    TextView productDetailsPrice;
    TextView hyperLink;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        productDetailsImg = findViewById(R.id.productDetailsImg);
        productDetailsDescription = findViewById(R.id.productDetailsDescription);
        productDetailsRatingValue = findViewById(R.id.productDetailsRatingValue);
        productDetailsPrice = findViewById(R.id.productDetailsPrice);
        productDetailsRating = findViewById(R.id.productDetailsRating);
        hyperLink = findViewById(R.id.hyperLink);

        productModel = Parcels.unwrap(getIntent().getParcelableExtra("harrie"));
        Log.i(TAG, productModel + "");
        productDetailsDescription.setText(productModel.getTitle());
        productDetailsPrice.setText(productModel.getPrice().getRaw() + " USD");
        productDetailsRating.setRating(productModel.getRating());
        productDetailsRatingValue.setText(productModel.getRating() + "");
        hyperLink.setText(productModel.getLink());
        String imageUrl = productModel.getImage();
        if (imageUrl != null) {
            Glide.with(ProductDetails.this)
                    .load(imageUrl)
                    .into(productDetailsImg);
        }


    }
}
