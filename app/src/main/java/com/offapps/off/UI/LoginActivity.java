package com.offapps.off.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends Activity {

    @InjectView(R.id.emailField) EditText mUsername;
    @InjectView(R.id.passwordField) EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.loginButton)
    public void onClick(){
        String username = mUsername.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();

        if (!username.contains("@")) {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    isValidUser(parseUser);
                }
            });
        }
        else {
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo(ParseConstants.KEY_EMAIL, username);
            userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    String emailUsername = object.getUsername();
                    ParseUser.logInInBackground(emailUsername, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            isValidUser(user);
                        }
                    });
                }
            });
        }
    }

    private void isValidUser(ParseUser parseUser) {
        if (parseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Invalid credentials!");
            builder.setPositiveButton("OK", null);
            builder.setMessage("Please make sure your username and password are correct!");
            builder.show();
        }
    }

    @OnClick(R.id.twitterLogIn)
    public void onTwitterClick(){
        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    if (user != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @OnClick(R.id.signUpButton)
    public void onClickSignUpButton() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}