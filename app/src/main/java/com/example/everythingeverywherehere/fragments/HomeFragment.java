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
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView homeRecyclerView;
    List<String> items;
    HomeListAdapter adapter;
    RelativeLayout homeRelativeLayout;
    SwipeListener swipeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home, container, false);

        homeRecyclerView = v.findViewById(R.id.homeRecyclerView);
        homeRelativeLayout = v.findViewById(R.id.homeRelativeLayout);

        items = new ArrayList<>();
        adapter = new HomeListAdapter(getActivity(), items);

        swipeListener = new SwipeListener(homeRelativeLayout);

        homeRecyclerView.setAdapter(adapter);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        List<String> everyProduct = dataBaseHelper.getkeyWord();
        items.addAll(everyProduct);
        Collections.sort(items);

        adapter.notifyDataSetChanged();

        return v;
    }
    private class SwipeListener implements View.OnTouchListener{
        GestureDetector gestureDetector;
        SwipeListener(View view){
            int threshold = 100;
            int velocity_threshold = 100;
            Log.d("SWIPEE", "I just swiped right!");
            GestureDetector.SimpleOnGestureListener listener =
                    new GestureDetector.SimpleOnGestureListener(){
                        @Override
                        public boolean onDown(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                            float xDiff = e2.getX()-e1.getX();
                            float yDiff = e2.getY() - e2.getY();
                            Log.d("ABOUTSWIPE", "I just swiped right!");
                            try {
                                if (Math.abs(xDiff) > Math.abs(yDiff)) {
                                    if (Math.abs(xDiff) > threshold
                                            && Math.abs(velocityX) > velocity_threshold) {
                                        if (xDiff > 0) {
                                            Log.d("SWIPE", "I just swiped right!");
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                                            return true;

                                        }
                                    }
                                    return true;
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            return false;
                        }
                    };
            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }
}
