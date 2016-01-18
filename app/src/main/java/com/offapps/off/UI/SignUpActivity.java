package com.offapps.off.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.offapps.off.Misc.ParseConstants;
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
    @InjectView(R.id.male_image) ImageView mMaleIcon;
    @InjectView(R.id.female_image) ImageView mFemaleIcon;
    @InjectView(R.id.goBackTextView) TextView mGoBackTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);

        Drawable goBackDrawable = ContextCompat.getDrawable(this, R.drawable.ic_navigate_before_white_24dp);
        goBackDrawable.setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        mGoBackTextView.setCompoundDrawablesWithIntrinsicBounds(goBackDrawable, null, null, null);

        mMaleIcon.setTag(0);
        mFemaleIcon.setTag(0);
    }

    @OnClick(R.id.signUpButton)
    public void onClick(View view){
        String password = mPassword.getText().toString().trim();
        String email = mEmail.getText().toString().trim();

        if (!mMaleIcon.getTag().equals(1) || !mFemaleIcon.getTag().equals(1)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Please select a gender!");
            builder.setPositiveButton("OK", null);
            builder.setMessage("Please tap on the gender symbol of your gender.");
            builder.show();
            return;
        }

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

            if (mFemaleIcon.getTag().equals(1)){
                newUser.put(ParseConstants.KEY_GENDER, 0);
            }
            else if (mMaleIcon.getTag().equals(1)) {
                newUser.put(ParseConstants.KEY_GENDER, 1);
            }
            newUser.put(ParseConstants.KEY_AGE, null);

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

    @OnClick(R.id.male_image)
    public void onClickMale() {
        mFemaleIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        mFemaleIcon.setTag(0);

        mMaleIcon.setColorFilter(ContextCompat.getColor(this, R.color.RegularBlue), PorterDuff.Mode.SRC_ATOP);
        mMaleIcon.setTag(1);
    }

    @OnClick(R.id.female_image)
    public void onClickFemale() {
        mMaleIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        mMaleIcon.setTag(0);

        mFemaleIcon.setColorFilter(ContextCompat.getColor(this, R.color.RegularPink), PorterDuff.Mode.SRC_ATOP);
        mFemaleIcon.setTag(1);
    }
}
