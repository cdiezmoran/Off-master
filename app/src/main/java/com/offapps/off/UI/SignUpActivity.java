package com.offapps.off.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.offapps.off.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SignUpActivity extends Activity {

    @InjectView(R.id.emailField) EditText mEmail;
    @InjectView(R.id.passwordField) EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.signUpButton)
    public void onClick(View view){
        String password = mPassword.getText().toString().trim();
        String email = mEmail.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Something went wrong!");
            builder.setPositiveButton("OK", null);
            builder.setMessage("Please make sure that all fields are filled.");
            builder.show();
        }
        else if (!isEmailValid(email)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Invalid email!");
            builder.setPositiveButton("OK", null);
            builder.setMessage("Please make sure that your email is correct.");
            builder.show();
        }
        else {
            ParseUser newUser = new ParseUser();
            newUser.setEmail(email);
            newUser.setPassword(password);

            String emailParts[] = email.split("@"); //Split the string at the @ to get username
            newUser.setUsername(emailParts[0]);

            SignUpCallback callback = new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Something went wrong!");
                        builder.setPositiveButton("OK", null);
                        builder.setMessage("There was an error connecting to the server! Please try again later.");
                        builder.show();
                    }
                }
            };
            newUser.signUpInBackground(callback);
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @OnClick(R.id.goBackTextView)
    public void onClickGoBack() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
