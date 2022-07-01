package com.example.everythingeverywherehere.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.everythingeverywherehere.ProductModel;
import com.example.everythingeverywherehere.R;

import org.parceler.Parcels;

public class ProductDetailsActivity extends AppCompatActivity {
    public static final String TAG = "PRODUCT_DETAILS";
    ProductModel productModel;
    ImageView productDetailsImg;
    TextView productDetailsDescription;
    TextView productDetailsRatingValue;
    RatingBar productDetailsRating;
    TextView productDetailsPrice;
    Button buyNowBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productDetailsImg = findViewById(R.id.productDetailsImg);
        productDetailsDescription = findViewById(R.id.productDetailsDescription);
        productDetailsRatingValue = findViewById(R.id.productDetailsRatingValue);
        productDetailsPrice = findViewById(R.id.productDetailsPrice);
        productDetailsRating = findViewById(R.id.productDetailsRating);
        buyNowBtn = findViewById(R.id.buyNowBtn);
        productModel = Parcels.unwrap(getIntent().getParcelableExtra("harrie"));
        String url = productModel.getLink();
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl(url);
                
            }
        });
        productDetailsDescription.setText(productModel.getTitle());
        productDetailsPrice.setText(productModel.getPrice().getRaw() + " USD");
        productDetailsRating.setRating(productModel.getRating());
        productDetailsRatingValue.setText(productModel.getRating() + "");
        String imageUrl = productModel.getImage();
        if (imageUrl != null) {
            Glide.with(ProductDetailsActivity.this)
                    .load(imageUrl)
                    .into(productDetailsImg);
        }


    }

    private void goToUrl(String s) {
        Uri uri = Uri.parse(s);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }
}
