package com.example.everythingeverywherehere;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    Context context;
    List<ProductModel> products;
    public static final String TAG = "PRODUCT_ADAPTER";


    public ResultAdapter(Context context, List<ProductModel> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_on_result, parent, false);
        return new ResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = products.get(position);
        holder.bind(product);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImgResult;
        TextView txtProductDescriptionResult;
        TextView txtRatingsResult;
        TextView txtPriceResult;
        TextView txtFromStoreResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImgResult = itemView.findViewById(R.id.productImgResult);
            txtProductDescriptionResult = (TextView) itemView.findViewById(R.id.txtProductDescriptionResult);
            txtRatingsResult = itemView.findViewById(R.id.txtRatingsResult);
            txtPriceResult = itemView.findViewById(R.id.txtPriceResult);
            txtFromStoreResult = itemView.findViewById(R.id.txtFromStoreResult);
            itemView.setClickable(true);
        }

        public void bind(ProductModel product) {
            txtProductDescriptionResult.setText(product.getTitle());
            txtFromStoreResult.setText("Amazon");
            if (product.getPrice().getRaw() != null) {
                txtPriceResult.setText(product.getPrice().getRaw() + " " + "USD");
            }
            txtRatingsResult.setText(product.getRating() + "");
            String imageUrl = product.getImage();
            if (imageUrl != null) {
                Glide.with(context)
                        .load(imageUrl)
                        .into(productImgResult);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductDetailsResult.class);
                    intent.putExtra("harry", Parcels.wrap(product));
                    context.startActivity(intent);
                }
            });
        }
    }
}
