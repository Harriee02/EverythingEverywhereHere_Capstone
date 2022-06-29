package com.example.everythingeverywherehere;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.Rating;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    Context context;
    List<ProductModel> products;
    public static final String TAG = "PRODUCT_ADAPTER";


    public ProductAdapter(Context context, List<ProductModel> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        ProductModel product = products.get(position);
        holder.bind(product);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImg;
        TextView txtProductDescription;
        TextView txtRatings;
        TextView txtPrice;
        TextView txtFromStore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImg = itemView.findViewById(R.id.productImg);
            txtProductDescription = (TextView) itemView.findViewById(R.id.txtProductDescription);
            txtRatings = itemView.findViewById(R.id.txtRatings);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtFromStore = itemView.findViewById(R.id.txtFromStore);
            itemView.setClickable(true);
        }

        public void bind(ProductModel product) {
            txtProductDescription.setText(product.getTitle());
            txtFromStore.setText("Amazon");
            if (product.getPrice().getRaw() != null) {
                txtPrice.setText(product.getPrice().getRaw() + " " + "USD");
            }
            txtRatings.setText(product.getRating() + "");
            String imageUrl = product.getImage();
            if (imageUrl != null) {
                Glide.with(context)
                        .load(imageUrl)
                        .into(productImg);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductDetails.class);
                    intent.putExtra("harrie", Parcels.wrap(product));
                    context.startActivity(intent);
                }
            });

        }


    }
}

