package com.example.everythingeverywherehere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everythingeverywherehere.DataBaseHelper;
import com.example.everythingeverywherehere.R;
import com.example.everythingeverywherehere.activities.HomeActivity;
import com.example.everythingeverywherehere.activities.ResultActivity;

import org.parceler.Parcels;

import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView homeRecyclerView;
    ListView listView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        homeRecyclerView = v.findViewById(R.id.homeRecyclerView);
        listView = v.findViewById(R.id.listView);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        List<String> everyProduct = dataBaseHelper.getkeyWord();
        Log.i("HOME", "bye " + everyProduct);
        ArrayAdapter<String> arr;
        arr = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, everyProduct);
        listView.setAdapter(arr);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ResultActivity.class);
                String selectedFromList = (String) (listView.getItemAtPosition(position));
                i.putExtra("keyword", Parcels.wrap(selectedFromList));
                startActivity(i);

            }
        });
        return v;
    }


}
