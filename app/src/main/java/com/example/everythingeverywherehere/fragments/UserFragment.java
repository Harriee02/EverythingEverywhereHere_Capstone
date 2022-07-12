package com.example.everythingeverywherehere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.everythingeverywherehere.R;
import com.example.everythingeverywherehere.activities.LoginActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class UserFragment extends Fragment {
    Button logoutBtn;
    TextView txtWrittenEmail;
    TextView txtHiddenPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        txtWrittenEmail = v.findViewById(R.id.txtWrittenEmail);
        txtHiddenPassword = v.findViewById(R.id.txtHiddenPassword);
        ParseUser user = ParseUser.getCurrentUser();
        txtWrittenEmail.setText(user.getEmail());
        logoutBtn = v.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return v;
    }

    private void logout() {
        ParseUser.logOutInBackground(new LogOutCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }
}
