package com.example.everythingeverywherehere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.everythingeverywherehere.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LOGIN_ACTIVITY";
    private EditText eMail;
    private EditText passWord;
    Button loginBtn;
    Button signupBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }
        eMail = findViewById(R.id.txtEmail);
        passWord = findViewById(R.id.txtPassword);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eMail.getText().toString();
                String password = passWord.getText().toString();
                loginUser(email, password);

            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eMail.getText().toString();
                String password = passWord.getText().toString();
                Log.i(TAG, "check " + email + password);
                signUpUser(email, password);
            }
        });
    }

    private void signUpUser(String email, String password) {
        ParseUser newUser = new ParseUser();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setUsername(email);
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    newUserSignedUpSuccessfully();
                } else {
                    newUserSignUpDidNotSucceed();
                    Log.e(TAG, e + " this is the error");
                }
            }
        });
    }

    private void newUserSignUpDidNotSucceed() {

        Toast.makeText(LoginActivity.this, "SignUp was unsuccessful", Toast.LENGTH_SHORT).show();
    }

    private void newUserSignedUpSuccessfully() {
        Toast.makeText(LoginActivity.this, "SignUp was successful", Toast.LENGTH_SHORT).show();
        eMail.setText("");
        passWord.setText("");
    }

    private void loginUser(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                // These toasts don't work
                if (e != null) {
                    Log.e(TAG, "Login failed");
                    Toast.makeText(LoginActivity.this, "Issue with login info!", Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
                Log.i(TAG, "Logged in!");
                Toast.makeText(LoginActivity.this, "Login was successful", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
