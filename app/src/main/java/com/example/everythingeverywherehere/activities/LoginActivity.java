package com.example.everythingeverywherehere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    EditText eMail;
    EditText passWord;
    Button loginBtn;
    Button signupBtn;
    CheckBox checkBoxShowPwd;

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

        passWord.setTransformationMethod(new PasswordTransformationMethod());
        checkBoxShowPwd = findViewById(R.id.checkBoxShowPwd);
        checkBoxShowPwd.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if (isChecked) {
                    passWord.setTransformationMethod(null); // Show password when box checked
                } else {
                    passWord.setTransformationMethod(new PasswordTransformationMethod()); // Hide password when box not checked
                }
            }
        } );

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

    /**
     * This method is called when the "sign up' button is clicked. It creates a new ParseUser object that is a new user trying to signup.
     * @param email This is the email the user is trying to signup with.
     * @param password The password typed in by the user to protect their new account
     */
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

    /**
     * This method is called if a sign up action was not successful.
     */
    private void newUserSignUpDidNotSucceed() {
        Toast.makeText(LoginActivity.this, "SignUp was unsuccessful", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called upon a successful sign up.
     */
    private void newUserSignedUpSuccessfully() {
        Toast.makeText(LoginActivity.this, "SignUp was successful", Toast.LENGTH_SHORT).show();
        eMail.setText("");
        passWord.setText("");
    }

    /**
     * This method is called when a login action is attempted. The built in logInBackground method in Parse is called to verify the login in details provided by the user.
     * @param email Provided by the user to login in, such email should have been used to sign up previously.
     * @param password Provided by the user, and must be accurately inputted to access the account.
     */
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

    /**
     * This method starts an intent to go into the main activity of the app.
     */
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
