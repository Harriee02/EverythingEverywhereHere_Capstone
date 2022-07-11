package com.example.everythingeverywherehere.activities;


import android.content.Intent;
        import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everythingeverywherehere.DataBaseHelper;
import com.example.everythingeverywherehere.ProductModel;
import com.example.everythingeverywherehere.R;
import com.example.everythingeverywherehere.adapters.ProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import org.parceler.Parcels;

        import java.lang.reflect.Type;
import java.util.ArrayList;
        import java.util.List;

public class ResultActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ProductModel> allProducts;
    ProductAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        recyclerView = findViewById(R.id.recyclerView);
        allProducts = new ArrayList<>();
        adapter = new ProductAdapter(this, allProducts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // shows the back button on the action bar.
        String keyword = Parcels.unwrap(getIntent().getParcelableExtra("keyword"));// unwraps data wrapped in the HomeListAdapter.
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ResultActivity.this);
        String listJsonArray = dataBaseHelper.getDataById(keyword); // gets data from the database.
        Type listType = new TypeToken<List<ProductModel>>() {
        }.getType();
        allProducts.addAll(new Gson().fromJson(listJsonArray, listType));
        adapter.notifyDataSetChanged();


    }
}
