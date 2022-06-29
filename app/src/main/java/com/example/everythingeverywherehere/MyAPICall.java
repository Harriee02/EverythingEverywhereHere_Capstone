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

    //https://fakestoreapi.com/           products

//    @GET("v3/267287c0-805f-414c-9f89-f82d7b8cfb86")//v3/c38ef967-0c43-4cbb-b4a0-1f330e2d33b7

    @GET("request?api_key=253F8FAC12FB4EDE9DC529D9A16B4365&type=search&amazon_domain=amazon.com")
    Call<ResponseBody> getProducts(
            @QueryMap Map<String, Object> map
    );

}
