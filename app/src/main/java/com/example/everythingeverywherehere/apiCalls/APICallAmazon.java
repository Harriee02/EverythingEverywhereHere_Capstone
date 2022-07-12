package com.example.everythingeverywherehere.apiCalls;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface APICallAmazon {

    @GET("request?api_key=F4C9F162657A40F6A9D9B584C059E855&type=search&amazon_domain=amazon.com")
    Call<ResponseBody> getProducts(
            @QueryMap Map<String, Object> map
    );

}
