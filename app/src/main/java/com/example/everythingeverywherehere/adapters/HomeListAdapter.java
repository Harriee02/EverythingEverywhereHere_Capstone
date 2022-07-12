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

import com.example.everythingeverywherehere.R;
import com.example.everythingeverywherehere.activities.ResultActivity;
import com.example.everythingeverywherehere.fragments.HomeFragment;

import org.parceler.Parcels;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {
    Context context;
    List<String> items;

    public HomeListAdapter(Context context, List<String> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public HomeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_fragment, parent, false);
        return new HomeListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListAdapter.ViewHolder holder, int position) {
        String item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView resultBtn;
        TextView itemList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            resultBtn = itemView.findViewById(R.id.resultBtn);
            itemList = itemView.findViewById(R.id.item);
            itemView.setClickable(true);
        }

        public void bind(String item) {
            itemList.setText(item);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ResultActivity.class);
                    i.putExtra("keyword",Parcels.wrap(item));
                    context.startActivity(i);
                }
            });
        }
    }
}
