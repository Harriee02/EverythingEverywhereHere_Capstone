package com.example.everythingeverywherehere;

import android.app.Activity;

//package com.example.everythingeverywherehere;
//
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everythingeverywherehere.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//
public class ResultActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ProductModel> allProducts;
    ResultAdapter adapter;

    //
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        recyclerView = findViewById(R.id.recyclerView);
        allProducts = new ArrayList<>();
        adapter = new ResultAdapter(this, allProducts);
        Log.i("RESULT", "" + adapter);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_result);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        overridePendingTransition(0, 0);
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        String keyword = Parcels.unwrap(getIntent().getParcelableExtra("keyword"));
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ResultActivity.this);
        String listJsonArray = dataBaseHelper.getDataById(keyword);
        Type listType = new TypeToken<List<ProductModel>>() {
        }.getType();
        allProducts.addAll(new Gson().fromJson(listJsonArray, listType));
        Log.i("RESULT", "" + allProducts);
        adapter.notifyDataSetChanged();


    }
}
