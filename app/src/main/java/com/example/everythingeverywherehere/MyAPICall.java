package com.example.everythingeverywherehere;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import android.app.Activity;

public interface MyAPICall {

    @GET("request?api_key=F4C9F162657A40F6A9D9B584C059E855&type=search&amazon_domain=amazon.com")
    Call<ResponseBody> getProducts(
            @QueryMap Map<String, Object> map
    );

}
