package com.example.everythingeverywherehere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everythingeverywherehere.DataBaseHelper;
import com.example.everythingeverywherehere.R;
import com.example.everythingeverywherehere.activities.MainActivity;
import com.example.everythingeverywherehere.activities.ResultActivity;
import com.example.everythingeverywherehere.adapters.HomeListAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView homeRecyclerView;
    List<String> items;
    HomeListAdapter adapter;
    RelativeLayout homeRelativeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        homeRecyclerView = v.findViewById(R.id.homeRecyclerView);
        homeRelativeLayout = v.findViewById(R.id.homeRelativeLayout);
        items = new ArrayList<>();
        adapter = new HomeListAdapter(getActivity(), items);
        homeRecyclerView.setAdapter(adapter);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        List<String> everyProduct = dataBaseHelper.getkeyWord();
        items.addAll(everyProduct);
        adapter.notifyDataSetChanged();
        return v;
    }
}
