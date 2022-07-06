package com.example.everythingeverywherehere;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface APICallWalmart {
    @GET("search.json?engine=walmart&api_key=3f5dcdc7e0cf17ddd3012979e5c127e2dbfa666951d0e514df996c65aa418564")
    Call<ResponseBody> getProducts(
            @QueryMap Map<String, Object> map
    );
}
