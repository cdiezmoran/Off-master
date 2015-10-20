package com.offapps.off.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
            AlertDialogFragment dialog = new AlertDialogFragment();
            dialog.show(getFragmentManager(), "error_dialog");
        }
        else {
            ParseUser newUser = new ParseUser();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUsername(email);
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
                        AlertDialogFragment exceptionDialog = new AlertDialogFragment();
                        exceptionDialog.show(getFragmentManager(), "exception_dialog");
                    }
                }
            };
            newUser.signUpInBackground(callback);
        }
    }
    @OnClick(R.id.loginTextView)
    public void onClick(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
