package com.example.everythingeverywherehere.fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everythingeverywherehere.ExampleJobService;
import com.example.everythingeverywherehere.apiCalls.APICallWalmart;
import com.example.everythingeverywherehere.DataBaseHelper;
import com.example.everythingeverywherehere.apiCalls.APICallAmazon;
import com.example.everythingeverywherehere.models.Price;
import com.example.everythingeverywherehere.models.ProductModel;
import com.example.everythingeverywherehere.R;
import com.example.everythingeverywherehere.adapters.ProductAdapter;
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
import java.util.Comparator;
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

public class SearchFragment extends Fragment {
    SearchView searchView;
    TextView textView;
    Button searchBtn;
    String searchText;
    ImageView filter;
    RecyclerView recyclerViewSearch;
    List<ProductModel> allProducts;
    ProductAdapter adapter;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerViewSearch = v.findViewById(R.id.recyclerViewSearch);

        allProducts = new ArrayList<>();

        adapter = new ProductAdapter(getActivity(), allProducts);

        recyclerViewSearch.setAdapter(adapter);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchView = v.findViewById(R.id.searchView);
        textView = v.findViewById(R.id.textView);
        searchBtn = v.findViewById(R.id.searchBtn);
        filter = v.findViewById(R.id.filter);
        progressBar = v.findViewById(R.id.progressBar);
        searchView.clearFocus();

        filter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() { // onclicklister on the search button to enable users search for a product

            @Override
            public void onClick(View v) {
                allProducts.clear();

                searchText = searchView.getQuery().toString().toLowerCase(Locale.ROOT);

                if (searchText.length() == 0) { //condition to check is search text is empty and shows the progress bar.
                    Toast.makeText(getActivity(), "Product name cannot be an empty string. Populate search view!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                searchView.setQuery("", true);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                queryProducts();
                queryWalmartProducts();
            }
        });
        return v;
    }

    /**
     * This method is schedules a job every 24hrs. The job it schedules updates the SQLite db, so that products in the db are
     * as recent as possible
     * This method has no return value.
     */
    public void scheduleJob(){
        Log.i("JOB", "entered the method!");
        ComponentName componentName = new ComponentName(getActivity(), ExampleJobService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("searchText",searchText);
        JobInfo info = new JobInfo.Builder(123,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(1 * 60 * 1000)
                .setExtras(bundle)
                .build();
        Log.i("JOB", "about to schedule");
        JobScheduler scheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
    }


    /**
     * This method shows an alertdialog with sorting filters.
     * The alertdialog has orders in which the products will be sorted i.e from the low to high, high to low and ratings.
     * This method does not have a return value.
     */
    private void showOptionsDialog() {
        final String[] filters = {"Low->High", "High->Low", "Rating"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Filter");
        builder.setSingleChoiceItems(filters, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch(which){
                    case 0: // this sorts the list in ascending order relative to the price
                        Collections.sort(allProducts, new Comparator<ProductModel>() {

                            @Override
                            public int compare(ProductModel o1, ProductModel o2) {
                                return Float.compare(o1.getPrice().getValue(), o2.getPrice().getValue());
                            }
                        });
                        break;
                    case 1:// this sorts the list in descending order relative to the price
                        Collections.sort(allProducts, new Comparator<ProductModel>() {

                            @Override
                            public int compare(ProductModel o1, ProductModel o2) {
                                return Float.compare(o2.getPrice().getValue(), o1.getPrice().getValue());
                            }
                        });
                        break;
                    case 2:// this sorts the list in descending order relative to the ratings
                        Collections.sort(allProducts, new Comparator<ProductModel>() {

                            @Override
                            public int compare(ProductModel o1, ProductModel o2) {
                                return Float.compare(o2.getRating(), o1.getRating());
                            }
                        });
                        break;
                }
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    /**
     * This method queries from the amazon API and adds the products to allProducts list
     * Takes in no Params and return no value.
     */
    private void queryProducts() {
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
                    onSearchResultsReady(new Gson().fromJson(String.valueOf(products), listType));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void mockListProducts() {
        String mockData = "[{\"position\":1,\"title\":\"2022 Apple MacBook Pro Laptop with M2 chip: 13-inch Retina Display, 8GB RAM, 256GB \u200B\u200B\u200B\u200B\u200B\u200B\u200BSSD \u200B\u200B\u200B\u200B\u200B\u200B\u200BStorage, Touch Bar, Backlit Keyboard, FaceTime HD Camera. Works with iPhone and iPad; Silver\",\"asin\":\"B0B3C5HNXJ\",\"link\":\"https:\\/\\/www.amazon.com\\/gp\\/slredirect\\/picassoRedirect.html\\/ref=pa_sp_atf_aps_sr_pg1_1?ie=UTF8&adId=A00750973DSTMRC54QZPA&url=%2F2022-Apple-MacBook-Laptop-chip%2Fdp%2FB0B3C5HNXJ%2Fref%3Dsr_1_1_sspa%3Fkeywords%3Dmacbook%26qid%3D1656465277%26sr%3D8-1-spons%26psc%3D1&qualifier=1656465277&id=2022156551484310&widgetName=sp_atf\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61bX2AoGj2L._AC_UY218_.jpg\",\"rating\":5,\"ratings_total\":1,\"sponsored\":true,\"prices\":[{\"symbol\":\"$\",\"value\":1299,\"currency\":\"USD\",\"raw\":\"$1,299.00\",\"asin\":\"B0B3C5HNXJ\",\"link\":\"https:\\/\\/www.amazon.com\\/gp\\/slredirect\\/picassoRedirect.html\\/ref=pa_sp_atf_aps_sr_pg1_1?ie=UTF8&adId=A00750973DSTMRC54QZPA&url=%2F2022-Apple-MacBook-Laptop-chip%2Fdp%2FB0B3C5HNXJ%2Fref%3Dsr_1_1_sspa%3Fkeywords%3Dmacbook%26qid%3D1656465277%26sr%3D8-1-spons%26psc%3D1&qualifier=1656465277&id=2022156551484310&widgetName=sp_atf\"}],\"price\":{\"symbol\":\"$\",\"value\":1299,\"currency\":\"USD\",\"raw\":\"$1,299.00\",\"asin\":\"B0B3C5HNXJ\",\"link\":\"https:\\/\\/www.amazon.com\\/gp\\/slredirect\\/picassoRedirect.html\\/ref=pa_sp_atf_aps_sr_pg1_1?ie=UTF8&adId=A00750973DSTMRC54QZPA&url=%2F2022-Apple-MacBook-Laptop-chip%2Fdp%2FB0B3C5HNXJ%2Fref%3Dsr_1_1_sspa%3Fkeywords%3Dmacbook%26qid%3D1656465277%26sr%3D8-1-spons%26psc%3D1&qualifier=1656465277&id=2022156551484310&widgetName=sp_atf\"},\"delivery\":{\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":2,\"title\":\"2020 Apple MacBook Air Laptop: Apple M1 Chip, 13” Retina Display, 8GB RAM, 512GB SSD Storage, Backlit Keyboard, FaceTime HD Camera, Touch ID. Works with iPhone\\/iPad; Gold\",\"asin\":\"B08N5M9XBS\",\"link\":\"https:\\/\\/www.amazon.com\\/gp\\/slredirect\\/picassoRedirect.html\\/ref=pa_sp_atf_aps_sr_pg1_1?ie=UTF8&adId=A06321163MAPTNX76XA5J&url=%2FApple-MacBook-13-inch-512GB-Storage%2Fdp%2FB08N5M9XBS%2Fref%3Dsr_1_2_sspa%3Fkeywords%3Dmacbook%26qid%3D1656465277%26sr%3D8-2-spons%26psc%3D1&qualifier=1656465277&id=2022156551484310&widgetName=sp_atf\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/71vFKBpKakL._AC_UY218_.jpg\",\"rating\":4.8,\"ratings_total\":15263,\"sponsored\":true,\"prices\":[{\"symbol\":\"$\",\"value\":1179,\"currency\":\"USD\",\"raw\":\"$1,179.00\",\"name\":\"Primary\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":1249,\"currency\":\"USD\",\"raw\":\"$1,249.00\",\"name\":\"Original\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":1179,\"currency\":\"USD\",\"raw\":\"$1,179.00\",\"name\":\"Primary\",\"is_primary\":true},\"delivery\":{\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":3,\"title\":\"2022 Apple MacBook Pro Laptop with M2 chip: 13-inch Retina Display, 8GB RAM, 256GB \u200B\u200B\u200B\u200B\u200B\u200B\u200BSSD \u200B\u200B\u200B\u200B\u200B\u200B\u200BStorage, Touch Bar, Backlit Keyboard, FaceTime HD Camera. Works with iPhone and iPad; Silver\",\"asin\":\"B0B3C5HNXJ\",\"link\":\"https:\\/\\/www.amazon.com\\/2022-Apple-MacBook-Laptop-chip\\/dp\\/B0B3C5HNXJ\\/ref=sr_1_3?keywords=macbook&qid=1656465277&sr=8-3\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61bX2AoGj2L._AC_UY218_.jpg\",\"rating\":5,\"ratings_total\":1,\"prices\":[{\"symbol\":\"$\",\"value\":1299,\"currency\":\"USD\",\"raw\":\"$1,299.00\",\"asin\":\"B0B3C5HNXJ\",\"link\":\"https:\\/\\/www.amazon.com\\/2022-Apple-MacBook-Laptop-chip\\/dp\\/B0B3C5HNXJ\\/ref=sr_1_3?keywords=macbook&qid=1656465277&sr=8-3\"}],\"price\":{\"symbol\":\"$\",\"value\":1299,\"currency\":\"USD\",\"raw\":\"$1,299.00\",\"asin\":\"B0B3C5HNXJ\",\"link\":\"https:\\/\\/www.amazon.com\\/2022-Apple-MacBook-Laptop-chip\\/dp\\/B0B3C5HNXJ\\/ref=sr_1_3?keywords=macbook&qid=1656465277&sr=8-3\"},\"delivery\":{\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":4,\"title\":\"Apple 13in MacBook Pro, Retina, Touch Bar, 3.1GHz Intel Core i5 Dual Core, 8GB RAM, 256GB SSD, Space Gray, MPXV2LL\\/A (Renewed)\",\"asin\":\"B07C3CTML2\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-MPXV2LL-Version-Renewed\\/dp\\/B07C3CTML2\\/ref=sr_1_4?keywords=macbook&qid=1656465277&sr=8-4\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/71PIqZ37ZNL._AC_UY218_.jpg\",\"is_prime\":true,\"rating\":4.3,\"ratings_total\":1758,\"prices\":[{\"symbol\":\"$\",\"value\":630,\"currency\":\"USD\",\"raw\":\"$630.00\",\"asin\":\"B07C3CTML2\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-MPXV2LL-Version-Renewed\\/dp\\/B07C3CTML2\\/ref=sr_1_4?keywords=macbook&qid=1656465277&sr=8-4\"}],\"price\":{\"symbol\":\"$\",\"value\":630,\"currency\":\"USD\",\"raw\":\"$630.00\",\"asin\":\"B07C3CTML2\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-MPXV2LL-Version-Renewed\\/dp\\/B07C3CTML2\\/ref=sr_1_4?keywords=macbook&qid=1656465277&sr=8-4\"},\"delivery\":{\"tagline\":\"Get it as soon as Fri, Jul 1\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":5,\"title\":\"2020 Apple MacBook Air Laptop: Apple M1 Chip, 13” Retina Display, 8GB RAM, 512GB SSD Storage, Backlit Keyboard, FaceTime HD Camera, Touch ID. Works with iPhone\\/iPad; Gold\",\"asin\":\"B08N5M9XBS\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-512GB-Storage\\/dp\\/B08N5M9XBS\\/ref=sr_1_5?keywords=macbook&qid=1656465277&sr=8-5\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/71vFKBpKakL._AC_UY218_.jpg\",\"rating\":4.8,\"ratings_total\":15263,\"prices\":[{\"symbol\":\"$\",\"value\":1179,\"currency\":\"USD\",\"raw\":\"$1,179.00\",\"name\":\"Primary\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":1249,\"currency\":\"USD\",\"raw\":\"$1,249.00\",\"name\":\"Original\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":1179,\"currency\":\"USD\",\"raw\":\"$1,179.00\",\"name\":\"Primary\",\"is_primary\":true},\"delivery\":{\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":6,\"title\":\"2020 Apple MacBook Pro with Apple M1 Chip (13-inch, 8GB RAM, 512GB SSD Storage) - Space Gray\",\"asin\":\"B08N5LM1K3\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-512GB-Storage\\/dp\\/B08N5LM1K3\\/ref=sr_1_6?keywords=macbook&qid=1656465277&sr=8-6\",\"availability\":{\"raw\":\"Only 7 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/71an9eiBxpL._AC_UY218_.jpg\",\"is_prime\":true,\"rating\":4.8,\"ratings_total\":6442,\"prices\":[{\"symbol\":\"$\",\"value\":1499,\"currency\":\"USD\",\"raw\":\"$1,499.00\",\"asin\":\"B08N5LM1K3\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-512GB-Storage\\/dp\\/B08N5LM1K3\\/ref=sr_1_6?keywords=macbook&qid=1656465277&sr=8-6\"}],\"price\":{\"symbol\":\"$\",\"value\":1499,\"currency\":\"USD\",\"raw\":\"$1,499.00\",\"asin\":\"B08N5LM1K3\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-512GB-Storage\\/dp\\/B08N5LM1K3\\/ref=sr_1_6?keywords=macbook&qid=1656465277&sr=8-6\"},\"delivery\":{\"tagline\":\"Get it as soon as Wed, Jul 6\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":7,\"title\":\"Apple MacBook Pro ME865LL\\/A 13.3-Inch Laptop with Retina Display (OLD VERSION) (Renewed)\",\"asin\":\"B07J1TX7B9\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-ME865LL-13-3-Inch-Refurbished\\/dp\\/B07J1TX7B9\\/ref=sr_1_7?keywords=macbook&qid=1656465277&sr=8-7\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61HR8fLosKL._AC_UY218_.jpg\",\"rating\":3.9,\"ratings_total\":77,\"prices\":[{\"symbol\":\"$\",\"value\":239,\"currency\":\"USD\",\"raw\":\"$239.00\",\"asin\":\"B07J1TX7B9\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-ME865LL-13-3-Inch-Refurbished\\/dp\\/B07J1TX7B9\\/ref=sr_1_7?keywords=macbook&qid=1656465277&sr=8-7\"}],\"price\":{\"symbol\":\"$\",\"value\":239,\"currency\":\"USD\",\"raw\":\"$239.00\",\"asin\":\"B07J1TX7B9\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-ME865LL-13-3-Inch-Refurbished\\/dp\\/B07J1TX7B9\\/ref=sr_1_7?keywords=macbook&qid=1656465277&sr=8-7\"},\"delivery\":{\"tagline\":\"Get it as soon as Tue, Jul 5\",\"price\":{\"raw\":\"FREE Shipping\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":8,\"title\":\"Apple MacBook Pro 13-inch 2.3GHz Core i5, 256GB - Space Gray - 2017 (Renewed)\",\"asin\":\"B07J3RG68L\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-2-3GHz-256GB\\/dp\\/B07J3RG68L\\/ref=sr_1_8?keywords=macbook&qid=1656465277&sr=8-8\",\"availability\":{\"raw\":\"Only 3 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61o62q+1GUL._AC_UY218_.jpg\",\"is_prime\":true,\"rating\":4.1,\"ratings_total\":1716,\"prices\":[{\"symbol\":\"$\",\"value\":439.99,\"currency\":\"USD\",\"raw\":\"$439.99\",\"asin\":\"B07J3RG68L\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-2-3GHz-256GB\\/dp\\/B07J3RG68L\\/ref=sr_1_8?keywords=macbook&qid=1656465277&sr=8-8\"}],\"price\":{\"symbol\":\"$\",\"value\":439.99,\"currency\":\"USD\",\"raw\":\"$439.99\",\"asin\":\"B07J3RG68L\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-2-3GHz-256GB\\/dp\\/B07J3RG68L\\/ref=sr_1_8?keywords=macbook&qid=1656465277&sr=8-8\"},\"delivery\":{\"tagline\":\"Get it as soon as Wed, Jul 6\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":9,\"title\":\"2021 Apple MacBook Pro (16-inch, Apple M1 Pro chip with 10‑core CPU and 16‑core GPU, 16GB RAM, 1TB SSD) - Silver\",\"asin\":\"B09JQML3NL\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-16-inch-10%E2%80%91core-16%E2%80%91core\\/dp\\/B09JQML3NL\\/ref=sr_1_9?keywords=macbook&qid=1656465277&sr=8-9\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61Y30DpqRVL._AC_UY218_.jpg\",\"rating\":4.7,\"ratings_total\":597,\"prices\":[{\"symbol\":\"$\",\"value\":2499,\"currency\":\"USD\",\"raw\":\"$2,499.00\",\"name\":\"Save 7%\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":2699,\"currency\":\"USD\",\"raw\":\"$2,699.00\",\"name\":\"Save 7%\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":2499,\"currency\":\"USD\",\"raw\":\"$2,499.00\",\"name\":\"Save 7%\",\"is_primary\":true},\"delivery\":{\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":10,\"title\":\"2017 Apple MacBook Air with 1.8GHz Intel Core i5 (13-inch, 8GB RAM, 128GB SSD Storage) (Renewed)\",\"asin\":\"B078H42W49\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Processor-MQD32LL-Version\\/dp\\/B078H42W49\\/ref=sr_1_10?keywords=macbook&qid=1656465277&sr=8-10\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/91wYB53Y4aL._AC_UY218_.jpg\",\"is_prime\":true,\"rating\":4.4,\"ratings_total\":3872,\"prices\":[{\"symbol\":\"$\",\"value\":304.99,\"currency\":\"USD\",\"raw\":\"$304.99\",\"name\":\"Primary\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":326,\"currency\":\"USD\",\"raw\":\"$326.00\",\"name\":\"Original\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":304.99,\"currency\":\"USD\",\"raw\":\"$304.99\",\"name\":\"Primary\",\"is_primary\":true},\"delivery\":{\"tagline\":\"Get it as soon as Wed, Jul 6\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":11,\"title\":\"2017- Apple MacBook Laptop with Intel Core m3, 1.2GHz ( MNYK2LL\\/A 12in Retina Display, Dual Core Processor, 8GB RAM, 256GB , Intel HD Graphics, Mac OS)- Gold (Renewed)\",\"asin\":\"B07679RDLW\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MNYK2LL-MacBook-Laptop-Refurbished\\/dp\\/B07679RDLW\\/ref=sr_1_11?keywords=macbook&qid=1656465277&sr=8-11\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/51LkXCk-8UL._AC_UY218_.jpg\",\"rating\":4.2,\"ratings_total\":395,\"prices\":[{\"symbol\":\"$\",\"value\":387,\"currency\":\"USD\",\"raw\":\"$387.00\",\"asin\":\"B07679RDLW\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MNYK2LL-MacBook-Laptop-Refurbished\\/dp\\/B07679RDLW\\/ref=sr_1_11?keywords=macbook&qid=1656465277&sr=8-11\"}],\"price\":{\"symbol\":\"$\",\"value\":387,\"currency\":\"USD\",\"raw\":\"$387.00\",\"asin\":\"B07679RDLW\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MNYK2LL-MacBook-Laptop-Refurbished\\/dp\\/B07679RDLW\\/ref=sr_1_11?keywords=macbook&qid=1656465277&sr=8-11\"},\"delivery\":{\"tagline\":\"Get it as soon as Tue, Jul 5\",\"price\":{\"raw\":\"FREE Shipping\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":12,\"title\":\"Apple MacBook Air (13-inch Retina display, 1.6GHz dual-core Intel Core i5, 128GB) - Gold (Renewed)\",\"asin\":\"B07NPTXDK7\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-display-dual-core\\/dp\\/B07NPTXDK7\\/ref=sr_1_12?keywords=macbook&qid=1656465277&sr=8-12\",\"availability\":{\"raw\":\"Only 6 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/71thf1SYnGL._AC_UY218_.jpg\",\"rating\":4.4,\"ratings_total\":495,\"prices\":[{\"symbol\":\"$\",\"value\":574,\"currency\":\"USD\",\"raw\":\"$574.00\",\"asin\":\"B07NPTXDK7\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-display-dual-core\\/dp\\/B07NPTXDK7\\/ref=sr_1_12?keywords=macbook&qid=1656465277&sr=8-12\"}],\"price\":{\"symbol\":\"$\",\"value\":574,\"currency\":\"USD\",\"raw\":\"$574.00\",\"asin\":\"B07NPTXDK7\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-display-dual-core\\/dp\\/B07NPTXDK7\\/ref=sr_1_12?keywords=macbook&qid=1656465277&sr=8-12\"},\"delivery\":{\"tagline\":\"Get it as soon as Wed, Jul 6\",\"price\":{\"raw\":\"FREE Shipping\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":13,\"title\":\"Apple MacBook Pro 15\\\" Retina Core i7 2.6GHz MLH32LL\\/A with Touch Bar, 16GB Memory, 256GB Solid State Drive (Renewed)\",\"asin\":\"B078BSQDPK\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MLH32LL-Renewed\\/dp\\/B078BSQDPK\\/ref=sr_1_13?keywords=macbook&qid=1656465277&sr=8-13\",\"availability\":{\"raw\":\"Only 3 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61F99gFWolL._AC_UY218_.jpg\",\"is_prime\":true,\"rating\":3.9,\"ratings_total\":366,\"prices\":[{\"symbol\":\"$\",\"value\":659.99,\"currency\":\"USD\",\"raw\":\"$659.99\",\"asin\":\"B078BSQDPK\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MLH32LL-Renewed\\/dp\\/B078BSQDPK\\/ref=sr_1_13?keywords=macbook&qid=1656465277&sr=8-13\"}],\"price\":{\"symbol\":\"$\",\"value\":659.99,\"currency\":\"USD\",\"raw\":\"$659.99\",\"asin\":\"B078BSQDPK\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MLH32LL-Renewed\\/dp\\/B078BSQDPK\\/ref=sr_1_13?keywords=macbook&qid=1656465277&sr=8-13\"},\"delivery\":{\"tagline\":\"Get it Fri, Jul 1 - Wed, Jul 6\"}},{\"position\":14,\"title\":\"Apple 15in MacBook Pro, Retina, Touch Bar, 2.9GHz Intel Core i7 Quad Core, 16GB RAM, 512GB SSD, Space Gray, MPTT2LL\\/A (Renewed)\",\"asin\":\"B07FQQ8DSY\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MPTT2LL-Refurbished\\/dp\\/B07FQQ8DSY\\/ref=sr_1_14?keywords=macbook&qid=1656465277&sr=8-14\",\"availability\":{\"raw\":\"Only 1 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/71D0naxZ8RL._AC_UY218_.jpg\",\"rating\":4.1,\"ratings_total\":610,\"prices\":[{\"symbol\":\"$\",\"value\":714.99,\"currency\":\"USD\",\"raw\":\"$714.99\",\"asin\":\"B07FQQ8DSY\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MPTT2LL-Refurbished\\/dp\\/B07FQQ8DSY\\/ref=sr_1_14?keywords=macbook&qid=1656465277&sr=8-14\"}],\"price\":{\"symbol\":\"$\",\"value\":714.99,\"currency\":\"USD\",\"raw\":\"$714.99\",\"asin\":\"B07FQQ8DSY\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MPTT2LL-Refurbished\\/dp\\/B07FQQ8DSY\\/ref=sr_1_14?keywords=macbook&qid=1656465277&sr=8-14\"},\"delivery\":{\"tagline\":\"Get it as soon as Fri, Jul 1\",\"price\":{\"raw\":\"FREE Shipping\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":15,\"title\":\"Apple MacBook Air MD760LL\\/A Intel Core i5-4250U X2 1.3GHz 4GB 256GB SSD 13.3in, Silver (Renewed)\",\"asin\":\"B06XX3LYFG\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-i5-4250U-Silver-Refurbished\\/dp\\/B06XX3LYFG\\/ref=sr_1_15?keywords=macbook&qid=1656465277&sr=8-15\",\"availability\":{\"raw\":\"Only 6 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/91wYB53Y4aL._AC_UY218_.jpg\",\"rating\":4.3,\"ratings_total\":4000,\"prices\":[{\"symbol\":\"$\",\"value\":266,\"currency\":\"USD\",\"raw\":\"$266.00\",\"asin\":\"B06XX3LYFG\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-i5-4250U-Silver-Refurbished\\/dp\\/B06XX3LYFG\\/ref=sr_1_15?keywords=macbook&qid=1656465277&sr=8-15\"}],\"price\":{\"symbol\":\"$\",\"value\":266,\"currency\":\"USD\",\"raw\":\"$266.00\",\"asin\":\"B06XX3LYFG\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-i5-4250U-Silver-Refurbished\\/dp\\/B06XX3LYFG\\/ref=sr_1_15?keywords=macbook&qid=1656465277&sr=8-15\"},\"delivery\":{\"tagline\":\"Get it as soon as Tue, Jul 5\",\"price\":{\"raw\":\"FREE Shipping\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":16,\"title\":\"Apple MacBook Air MVFM2LLA, 13.3 Inches Retina Display (1.6 GHz 8th Gen Intel Core i5 Dual-Core, 8GB RAM, 128GB - Gold (Renewed)\",\"asin\":\"B0815627ZC\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-Display-Dual-Core\\/dp\\/B0815627ZC\\/ref=sr_1_16?keywords=macbook&qid=1656465277&sr=8-16\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/51FWf+esWHL._AC_UY218_.jpg\",\"rating\":4.5,\"ratings_total\":892,\"prices\":[{\"symbol\":\"$\",\"value\":676.99,\"currency\":\"USD\",\"raw\":\"$676.99\",\"asin\":\"B0815627ZC\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-Display-Dual-Core\\/dp\\/B0815627ZC\\/ref=sr_1_16?keywords=macbook&qid=1656465277&sr=8-16\"}],\"price\":{\"symbol\":\"$\",\"value\":676.99,\"currency\":\"USD\",\"raw\":\"$676.99\",\"asin\":\"B0815627ZC\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-Display-Dual-Core\\/dp\\/B0815627ZC\\/ref=sr_1_16?keywords=macbook&qid=1656465277&sr=8-16\"},\"delivery\":{\"tagline\":\"Get it as soon as Wed, Jul 6\",\"price\":{\"raw\":\"FREE Shipping\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":17,\"title\":\"Apple MacBook Pro MF839LL\\/A 128GB Flash Storage - 8GB LPDDR3 - 13.3in with Intel Core i5 2.7 GHz (Renewed)\",\"asin\":\"B01433Q792\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-MF839LL-128GB-Storage\\/dp\\/B01433Q792\\/ref=sr_1_17?keywords=macbook&qid=1656465277&sr=8-17\",\"availability\":{\"raw\":\"Only 4 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/81bTK8-ei4L._AC_UY218_.jpg\",\"rating\":4.3,\"ratings_total\":689,\"prices\":[{\"symbol\":\"$\",\"value\":285.5,\"currency\":\"USD\",\"raw\":\"$285.50\",\"name\":\"Primary\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":586.28,\"currency\":\"USD\",\"raw\":\"$586.28\",\"name\":\"Original\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":285.5,\"currency\":\"USD\",\"raw\":\"$285.50\",\"name\":\"Primary\",\"is_primary\":true},\"delivery\":{\"tagline\":\"Get it as soon as Fri, Jul 1\",\"price\":{\"raw\":\"FREE Shipping\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":18,\"title\":\"Late 2018 Apple MacBook Air with 1.6GHz Intel Core i5 (13.3 inch Retina Display, 16GB RAM, 512GB SSD) Space Gray (Renewed)\",\"asin\":\"B084YRFX5R\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-3-inch-Retina-Display\\/dp\\/B084YRFX5R\\/ref=sr_1_18?keywords=macbook&qid=1656465277&sr=8-18\",\"availability\":{\"raw\":\"Only 5 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/81JcX8hnhHL._AC_UY218_.jpg\",\"rating\":3.9,\"ratings_total\":38,\"prices\":[{\"symbol\":\"$\",\"value\":649,\"currency\":\"USD\",\"raw\":\"$649.00\",\"asin\":\"B084YRFX5R\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-3-inch-Retina-Display\\/dp\\/B084YRFX5R\\/ref=sr_1_18?keywords=macbook&qid=1656465277&sr=8-18\"}],\"price\":{\"symbol\":\"$\",\"value\":649,\"currency\":\"USD\",\"raw\":\"$649.00\",\"asin\":\"B084YRFX5R\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-3-inch-Retina-Display\\/dp\\/B084YRFX5R\\/ref=sr_1_18?keywords=macbook&qid=1656465277&sr=8-18\"},\"delivery\":{\"tagline\":\"Get it as soon as Wed, Jul 6\",\"price\":{\"raw\":\"FREE Shipping\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":19,\"title\":\"2021 Apple MacBook Pro (14-inch, Apple M1 Pro chip with 10‑core CPU and 16‑core GPU, 16GB RAM, 1TB SSD) - Space Gray\",\"asin\":\"B09JQQM86S\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-14-inch-10%E2%80%91core-16%E2%80%91core\\/dp\\/B09JQQM86S\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B09JQQM86S&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-1-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61vFO3R5UNL._AC_UL320_.jpg\",\"is_prime\":true,\"rating\":4.7,\"ratings_total\":881,\"prices\":[{\"symbol\":\"$\",\"value\":2299,\"currency\":\"USD\",\"raw\":\"$2,299.00\",\"name\":\"$2,299.00\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":2499,\"currency\":\"USD\",\"raw\":\"$2,499.00\",\"name\":\"$2,299.00\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":2299,\"currency\":\"USD\",\"raw\":\"$2,299.00\",\"name\":\"$2,299.00\",\"is_primary\":true},\"delivery\":{\"tagline\":\"Get it as soon as Fri, Jul 1\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":20,\"title\":\"2021 Apple MacBook Pro (16-inch, Apple M1 Pro chip with 10‑core CPU and 16‑core GPU, 16GB RAM, 512GB SSD) - Space Gray\",\"asin\":\"B09JQKBQSB\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-16-inch-10%E2%80%91core-16%E2%80%91core\\/dp\\/B09JQKBQSB\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B09JQKBQSB&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-3-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\",\"availability\":{\"raw\":\"Only 6 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61aUBxqc5PL._AC_UL320_.jpg\",\"is_prime\":true,\"rating\":4.7,\"ratings_total\":597,\"prices\":[{\"symbol\":\"$\",\"value\":2299,\"currency\":\"USD\",\"raw\":\"$2,299.00\",\"name\":\"$2,299.00\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":2499,\"currency\":\"USD\",\"raw\":\"$2,499.00\",\"name\":\"$2,299.00\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":2299,\"currency\":\"USD\",\"raw\":\"$2,299.00\",\"name\":\"$2,299.00\",\"is_primary\":true},\"delivery\":{\"tagline\":\"Get it as soon as Wed, Jul 6\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":21,\"title\":\"Apple 15in MacBook Pro, Retina, Touch Bar, 2.8GHz Intel Core i7 Quad Core, 16GB RAM, 256GB SSD, Space Gray, MPTR2LL\\/A (Renewed)\",\"asin\":\"B07JMLMVKP\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MPTR2LL-Renewed\\/dp\\/B07JMLMVKP\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B07JMLMVKP&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-5-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61UjgobzV7L._AC_UL320_.jpg\",\"is_prime\":true,\"rating\":4.1,\"ratings_total\":610,\"prices\":[{\"symbol\":\"$\",\"value\":625.5,\"currency\":\"USD\",\"raw\":\"$625.50\",\"name\":\"$625.50\",\"asin\":\"B07JMLMVKP\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MPTR2LL-Renewed\\/dp\\/B07JMLMVKP\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B07JMLMVKP&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-5-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\"}],\"price\":{\"symbol\":\"$\",\"value\":625.5,\"currency\":\"USD\",\"raw\":\"$625.50\",\"name\":\"$625.50\",\"asin\":\"B07JMLMVKP\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Retina-MPTR2LL-Renewed\\/dp\\/B07JMLMVKP\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B07JMLMVKP&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-5-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\"},\"delivery\":{\"tagline\":\"Get it as soon as Wed, Jul 6\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":22,\"title\":\"2019 MacBook Pro with 1.4GHz Intel Core i5 (13 inch, 8GB RAM, 128GB SSD Storage) - Space Gray (Renewed)\",\"asin\":\"B082J572X8\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-Touch-Intel-Quad-Core\\/dp\\/B082J572X8\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B082J572X8&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-7-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/71i49M4hv2L._AC_UL320_.jpg\",\"is_prime\":true,\"rating\":4.3,\"ratings_total\":1758,\"prices\":[{\"symbol\":\"$\",\"value\":614.99,\"currency\":\"USD\",\"raw\":\"$614.99\",\"name\":\"$614.99\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":654,\"currency\":\"USD\",\"raw\":\"$654.00\",\"name\":\"$614.99\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":614.99,\"currency\":\"USD\",\"raw\":\"$614.99\",\"name\":\"$614.99\",\"is_primary\":true},\"delivery\":{\"tagline\":\"Get it as soon as Fri, Jul 1\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":23,\"title\":\"2020 Apple MacBook Air Laptop: Apple M1 Chip, 13” Retina Display, 8GB RAM, 512GB SSD Storage, Backlit Keyboard, FaceTime HD Camera, Touch ID. Works with iPhone\\/iPad; Silver\",\"asin\":\"B08N5R2GQW\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-512GB-Storage\\/dp\\/B08N5R2GQW\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B08N5R2GQW&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-2-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/71TPda7cwUL._AC_UL320_.jpg\",\"is_prime\":true,\"rating\":4.8,\"ratings_total\":15263,\"prices\":[{\"symbol\":\"$\",\"value\":1243.51,\"currency\":\"USD\",\"raw\":\"$1,243.51\",\"name\":\"$1,243.51\",\"asin\":\"B08N5R2GQW\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-512GB-Storage\\/dp\\/B08N5R2GQW\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B08N5R2GQW&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-2-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\"}],\"price\":{\"symbol\":\"$\",\"value\":1243.51,\"currency\":\"USD\",\"raw\":\"$1,243.51\",\"name\":\"$1,243.51\",\"asin\":\"B08N5R2GQW\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-13-inch-512GB-Storage\\/dp\\/B08N5R2GQW\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B08N5R2GQW&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-2-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\"},\"delivery\":{\"tagline\":\"Get it as soon as Fri, Jul 1\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":24,\"title\":\"Apple MacBook Air with Intel Core i5, 1.6GHz, (13-inch, 4GB,128GB SSD) - Silver (Renewed)\",\"asin\":\"B013HD3INW\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-MJVE2LL-13-inch-Refurbished\\/dp\\/B013HD3INW\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B013HD3INW&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-4-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/91wYB53Y4aL._AC_UL320_.jpg\",\"is_prime\":true,\"rating\":4.3,\"ratings_total\":2535,\"prices\":[{\"symbol\":\"$\",\"value\":195,\"currency\":\"USD\",\"raw\":\"$195.00\",\"name\":\"$195.00\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":208.99,\"currency\":\"USD\",\"raw\":\"$208.99\",\"name\":\"$195.00\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":195,\"currency\":\"USD\",\"raw\":\"$195.00\",\"name\":\"$195.00\",\"is_primary\":true},\"delivery\":{\"tagline\":\"Get it as soon as Fri, Jul 1\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}},{\"position\":25,\"title\":\"Apple 13.3 inches MacBook Air Retina display, 1.6GHz dual-core Intel Core i5, 256GB - Space Gray (Renewed)\",\"asin\":\"B07XQHL645\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-inches-MacBook-display-dual-core\\/dp\\/B07XQHL645\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B07XQHL645&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-6-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\",\"availability\":{\"raw\":\"Only 18 left in stock - order soon.\"},\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/51dafnlz6wL._AC_UL320_.jpg\",\"is_prime\":true,\"rating\":4.4,\"ratings_total\":495,\"prices\":[{\"symbol\":\"$\",\"value\":556.5,\"currency\":\"USD\",\"raw\":\"$556.50\",\"name\":\"$556.50\",\"is_primary\":true},{\"symbol\":\"$\",\"value\":589,\"currency\":\"USD\",\"raw\":\"$589.00\",\"name\":\"$556.50\",\"is_rrp\":true}],\"price\":{\"symbol\":\"$\",\"value\":556.5,\"currency\":\"USD\",\"raw\":\"$556.50\",\"name\":\"$556.50\",\"is_primary\":true},\"delivery\":{\"tagline\":\"Get it Fri, Jul 1 - Wed, Jul 6\"}},{\"position\":26,\"title\":\"Apple MacBook Air MJVM2LL\\/A 11.6-Inch 128GB Laptop (Renewed)\",\"asin\":\"B013HD3FDK\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-MJVM2LL-11-6-Inch-Refurbished\\/dp\\/B013HD3FDK\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B013HD3FDK&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-8-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\",\"categories\":[{\"name\":\"All Departments\",\"id\":\"aps\"}],\"image\":\"https:\\/\\/m.media-amazon.com\\/images\\/I\\/61LLziMeLlL._AC_UL320_.jpg\",\"is_prime\":true,\"rating\":4.3,\"ratings_total\":458,\"prices\":[{\"symbol\":\"$\",\"value\":220,\"currency\":\"USD\",\"raw\":\"$220.00\",\"name\":\"$220.00\",\"asin\":\"B013HD3FDK\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-MJVM2LL-11-6-Inch-Refurbished\\/dp\\/B013HD3FDK\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B013HD3FDK&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-8-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\"}],\"price\":{\"symbol\":\"$\",\"value\":220,\"currency\":\"USD\",\"raw\":\"$220.00\",\"name\":\"$220.00\",\"asin\":\"B013HD3FDK\",\"link\":\"https:\\/\\/www.amazon.com\\/Apple-MacBook-MJVM2LL-11-6-Inch-Refurbished\\/dp\\/B013HD3FDK\\/ref=sxbs_aspa_sqa?content-id=amzn1.sym.7acfa045-eed7-4487-a692-72124285429c%3Aamzn1.sym.7acfa045-eed7-4487-a692-72124285429c&cv_ct_cx=macbook&keywords=macbook&pd_rd_i=B013HD3FDK&pd_rd_r=2d799643-1bb5-4ccb-bd82-72ddfb582fd2&pd_rd_w=Pp9Mx&pd_rd_wg=xxChv&pf_rd_p=7acfa045-eed7-4487-a692-72124285429c&pf_rd_r=8V7NMRJAP6T138DVP5N0&qid=1656465277&sr=1-8-e2a25af9-85f9-4cd2-bc34-0ef16cb90c3e\"},\"delivery\":{\"tagline\":\"Get it as soon as Fri, Jul 1\",\"price\":{\"raw\":\"FREE Shipping by Amazon\",\"symbol\":\"$\",\"currency\":\"USD\",\"value\":0,\"is_free\":true}}}]";
        Type listType = new TypeToken<List<ProductModel>>() {
        }.getType();
        allProducts.addAll(new Gson().fromJson(mockData, listType));
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();

    }

    /**
     * This method queries from the walmart API and adds the products to allProducts list
     * Takes in no Params and return no value.
     */
    private void queryWalmartProducts() {
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
                        String stringPrice = "$"+Float.toString(newPrice);
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
                    onSearchResultsReady(productList);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * This method checks if the two API calls have been completed and few operations on the list containing productModel objects.
     * Takes in a list of productmodel objects.
     */
    public void onSearchResultsReady(List<ProductModel> productmodel) {

        // this condition prevents certain methods to be called on allProducts until both API calls have ended.
        if (allProducts.isEmpty()) {
            allProducts.addAll(productmodel);
        } else {
            allProducts.addAll(productmodel);
            Collections.sort(allProducts);

            DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
            Gson gson = new Gson();
            String json = gson.toJson(allProducts);
            boolean addedProduct = dataBaseHelper.addProduct(searchText, json);

            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            Log.i("SEARCH", addedProduct +"");
            if (!addedProduct){
                Log.i("JOB", "ABOUT TO CALL THE METHOD!");
                scheduleJob();
            }
        }
    }
}
