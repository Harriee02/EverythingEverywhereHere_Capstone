package com.example.everythingeverywherehere.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.everythingeverywherehere.activities.ProductDetailsActivity;
import com.example.everythingeverywherehere.models.ProductModel;
import com.example.everythingeverywherehere.R;

import org.parceler.Parcels;

import java.util.List;

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
            if (product.getRatings_total() == -123){
                txtFromStore.setText("Walmart");
            }
            else{
                txtFromStore.setText("Amazon");
            }

            txtPrice.setText(product.getPrice() != null ? product.getPrice().getRaw() + " " + "USD" : "0 USD");

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
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra("product details", Parcels.wrap(product));
                    context.startActivity(intent);
                }
            });
        }
    }
}

