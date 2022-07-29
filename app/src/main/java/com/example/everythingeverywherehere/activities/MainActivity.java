package com.example.everythingeverywherehere.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.everythingeverywherehere.R;
import com.example.everythingeverywherehere.UpdateNotification;
import com.example.everythingeverywherehere.fragments.HomeFragment;
import com.example.everythingeverywherehere.fragments.SearchFragment;
import com.example.everythingeverywherehere.fragments.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    OnSwipeListener swipeListener;
    FrameLayout frameLayout;

    int fragmentNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        frameLayout = findViewById(R.id.fragment_container);
        swipeListener = new OnSwipeListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    /**
     * This method creates the bottom navigation and implements each icon on the bottom navigation to its fragment.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.home:
                            selectedFragment = new HomeFragment();
                            fragmentNumber = 0;
                            break;
                        case R.id.search:
                            selectedFragment = new SearchFragment();
                            fragmentNumber = 1;
                            break;
                        case R.id.user:
                            selectedFragment = new UserFragment();
                            fragmentNumber = 2;
                            break;
                    }
                    // this starts the three fragments added to this activity.
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };

    public class OnSwipeListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            private static final int SWIPE_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            fragmentNumber ++;
                            switch (fragmentNumber) {
                                case 1:
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment()).commit();
                                    break;
                                case 2:
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                                    break;
                            }
                            Log.i("SWIPEINFO", ""+fragmentNumber);
                        } else {
                            // TODO
                        }
                    }
                }
                return true;
            }
        }
//    private class SwipeListener implements View.OnTouchListener{
//        GestureDetector gestureDetector;
//        SwipeListener(View view){
//            int threshold = 100;
//            int velocity_threshold = 100;
//
//            GestureDetector.SimpleOnGestureListener listener =
//                    new GestureDetector.SimpleOnGestureListener(){
//                        @Override
//                        public boolean onDown(MotionEvent e) {
//                            return true;
//                        }
//
//                        @Override
//                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                            float xDiff = e2.getX()-e1.getX();
//                            float yDiff = e2.getY() - e2.getY();
//                            try {
//                                if (Math.abs(xDiff) > Math.abs(yDiff)){
//                                    if (Math.abs(xDiff) > threshold
//                                    && Math.abs(velocityX) > velocity_threshold){
//                                        if (xDiff < 0){
//                                            switch (fragmentNumber){
//                                                case 0:
//                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();
//                                                    break;
//                                                case 1:
//                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new UserFragment()).commit();
//                                                    break;
//                                            }
//
////                                            Log.i("SWIPEINFOO", ""+fragmentNumber);
////                                            if (fragmentNumber == 0 | fragmentNumber == 1){
////                                                fragmentNumber++;
////                                                if (fragmentNumber == 1){
////                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();
////                                                }
////                                                else{
////                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new UserFragment()).commit();
////                                                }
////
////                                            }
//                                        }else{
//                                            switch (fragmentNumber) {
//                                                case 1:
//                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                                                    break;
//                                                case 2:
//                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
//                                                    break;
//                                            }
////                                            Log.i("SWIPEINFO", ""+fragmentNumber);
////                                            if (fragmentNumber == 1 | fragmentNumber == 2){
////                                                fragmentNumber--;
////                                                if (fragmentNumber == 0){
////                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
////                                                }
////                                                else{
////                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();
////                                                }
////
////                                            }
//                                        }
//                                        return true;
//                                    }
//                                }
//                            }catch(Exception e){
//                                e.printStackTrace();
//                            }
//                            return false;
//                        }
//                    };
//            gestureDetector = new GestureDetector(listener);
//            view.setOnTouchListener(this);
//        }
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            return gestureDetector.onTouchEvent(event);
//        }
    }
}
