package com.example.everythingeverywherehere.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.everythingeverywherehere.ProductModel;
import com.example.everythingeverywherehere.R;

import org.parceler.Parcels;

public class ProductDetailsResultActivity extends AppCompatActivity {
    public static final String TAG = "PRODUCT_DETAILS";
    ProductModel productModel;
    ImageView productDetailsImgResult;
    TextView productDetailsDescriptionResult;
    TextView productDetailsRatingValueResult;
    RatingBar productDetailsRatingResult;
    TextView productDetailsPriceResult;
    TextView hyperLinkResult;
    Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_result);
        productDetailsImgResult = findViewById(R.id.productDetailsImgResult);
        productDetailsDescriptionResult = findViewById(R.id.productDetailsDescriptionResult);
        productDetailsRatingValueResult = findViewById(R.id.productDetailsRatingValueResult);
        productDetailsPriceResult = findViewById(R.id.productDetailsPriceResult);
        productDetailsRatingResult = findViewById(R.id.productDetailsRatingResult);
        hyperLinkResult = findViewById(R.id.hyperLinkResult);

        productModel = Parcels.unwrap(getIntent().getParcelableExtra("harry"));
        Log.i(TAG, productModel + "");
        productDetailsDescriptionResult.setText(productModel.getTitle());
        productDetailsPriceResult.setText(productModel.getPrice().getRaw() + " USD");
        productDetailsRatingResult.setRating(productModel.getRating());
        productDetailsRatingValueResult.setText(productModel.getRating() + "");
        hyperLinkResult.setText(productModel.getLink());
        String imageUrl = productModel.getImage();
        if (imageUrl != null) {
            Glide.with(ProductDetailsResultActivity.this)
                    .load(imageUrl)
                    .into(productDetailsImgResult);
        }


    }
}
