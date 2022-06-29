package com.example.everythingeverywherehere;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    SearchView searchView;
    TextView textView;
    Button searchBtn;
    String searchText;
    RecyclerView recyclerViewSearch;
    List<ProductModel> allProducts;
    ProductAdapter adapter;
    public static final String TAG = "SEARCHACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        recyclerViewSearch = findViewById(R.id.recyclerViewSearch);
        allProducts = new ArrayList<>();
        adapter = new ProductAdapter(this, allProducts);
        recyclerViewSearch.setAdapter(adapter);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchView);
        textView = findViewById(R.id.textView);
        searchBtn = findViewById(R.id.searchBtn);
        searchView.clearFocus();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryProducts();
                Log.i(TAG, "ABC printed second?");
                searchView.setQuery("", true);
            }
        });

    }

    private void queryProducts() {
        allProducts.clear();
        searchText = searchView.getQuery().toString().toLowerCase(Locale.ROOT);
//        String passedSearchText = getIntent().getExtras().getString("searchText");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.rainforestapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Map<String, Object> map = new HashMap<>();
        map.put("search_term", searchText);
        MyAPICall myAPICall = retrofit.create(MyAPICall.class);
        Call<ResponseBody> call = myAPICall.getProducts(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responsee = response.body().string();
                    JSONObject jsonObject = new JSONObject(responsee);
                    List<String> list = new ArrayList<String>();
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(SearchActivity.this);
                    JSONArray products = jsonObject.getJSONArray("search_results");
                    boolean b = dataBaseHelper.addProduct(searchText, products);
                    Log.i(TAG, "ABC printed first?");
                    Type listType = new TypeToken<List<ProductModel>>() {
                    }.getType();
                    allProducts.addAll(new Gson().fromJson(String.valueOf(products), listType));

                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "ABCD" + allProducts);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
