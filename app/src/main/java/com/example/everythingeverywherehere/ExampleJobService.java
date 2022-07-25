package com.example.everythingeverywherehere;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.view.View;

import com.example.everythingeverywherehere.apiCalls.APICallAmazon;
import com.example.everythingeverywherehere.apiCalls.APICallWalmart;
import com.example.everythingeverywherehere.models.Price;
import com.example.everythingeverywherehere.models.ProductModel;
import com.example.everythingeverywherehere.models.ProductModelWalmart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExampleJobService extends JobService {
    private boolean jobCancelled = false;
    List<ProductModel> allProducts = new ArrayList<>();

    /**
     * This method starts the scheduled job.
     * @param params
     * @return true after the method has been called.
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("START", "job started");
        updateDb(params);
        return true;
    }

    /**
     * This method is where the update job is done. This method calls the APIs again and then updates the db in the background.
     * @param params
     */
    private void updateDb(JobParameters params) {
        String searchText = params.getExtras().getString("searchText");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("UPDATE", "Update starting");
                queryProducts(searchText);
                queryWalmartProducts(searchText);
                jobFinished(params, true);
                Log.d("UPDATE", "Update finished");
            }
        }).start();
    }

    /**
     * This method queries from the Amazon API and adds the products to allProducts list
     * Returns no value.
     * @param searchText this is the name of the product that the user is trying to search for.
     */
    private void queryProducts(String searchText) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.rainforestapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Map<String, Object> map = new HashMap<>();
        map.put("search_term", searchText);

        APICallAmazon apiCallAmazon = retrofit.create(APICallAmazon.class);
        Call<ResponseBody> call = apiCallAmazon.getProducts(map);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responsee = response.body().string();
                    JSONObject jsonObject = new JSONObject(responsee);
                    JSONArray products = jsonObject.getJSONArray("search_results");
                    Type listType = new TypeToken<List<ProductModel>>() {
                    }.getType();
                    onSearchResultsReady(new Gson().fromJson(String.valueOf(products), listType), searchText);
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

    /**
     * This method queries from the walmart API and adds the products to allProducts list
     * Returns no value.
     * @param searchText this is the name of the product that the user is trying to search for.
     */
    private void queryWalmartProducts(String searchText) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://serpapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Map<String, Object> map = new HashMap<>();
        map.put("query", searchText);

        APICallWalmart apiCallWal = retrofit.create(APICallWalmart.class);
        Call<ResponseBody> call = apiCallWal.getProducts(map);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responsee = response.body().string();
                    JSONObject jsonObject = new JSONObject(responsee);
                    JSONArray products = jsonObject.getJSONArray("organic_results");

                    List<ProductModel> productList = new ArrayList<>();

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject productJson = products.getJSONObject(i);

                        Type listType = new TypeToken<ProductModelWalmart>() {}.getType();
                        ProductModelWalmart product = new Gson().fromJson(String.valueOf(productJson), listType);

                        String title = product.getTitle();
                        String link = product.getLink();
                        String imageUrl = product.getThumbnail();
                        float rating = product.getRating();
                        Price price = new Price();
                        float newPrice = product.getPrice().getOfferPrice();
                        String stringPrice = Float.toString(newPrice);
                        price.setRaw(stringPrice);
                        price.setValue(newPrice);

                        // these next lines convert a WalmartProductModel object to a ProductModel class.
                        ProductModel productModel = new ProductModel();
                        productModel.setTitle(title);
                        productModel.setLink(link);
                        productModel.setImage(imageUrl);
                        productModel.setRating(rating);
                        productModel.setPrice(price);
                        productModel.setRatings_total(-123);

                        // this condition prevents the addition of a product to the productList whose price is $0.0
                        if (!price.getRaw().equals("0.0")) {
                            productList.add(productModel);
                        }
                    }
                    onSearchResultsReady(productList, searchText);
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

    /**
     * This method checks if the two API calls have been completed and few operations on the list containing productModel objects.
     * @param productmodel this is a list of product model objects
     * @param searchText this is the product name searched for by the user.
     */
    public void onSearchResultsReady(List<ProductModel> productmodel, String searchText) {

//         this condition prevents certain methods to be called on allProducts until both API calls have ended.
        if (allProducts.isEmpty()) {
            allProducts.addAll(productmodel);
        } else {
            allProducts.addAll(productmodel);
            Collections.sort(allProducts);

            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            Gson gson = new Gson();
            String json = gson.toJson(allProducts);
            boolean b = dataBaseHelper.addProduct(searchText, json);
            Log.d("TAG", "product has been added to the database");
        }
    }

    /**
     * This method returns true if the job was stopped before completion.
     * @param params
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }
}
