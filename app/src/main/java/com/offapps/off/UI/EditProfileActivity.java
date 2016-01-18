package com.offapps.off.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EditProfileActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.usernameField) EditText mUsernameField;
    @InjectView(R.id.emailField) EditText mEmailField;
    @InjectView(R.id.ageField) EditText mAgeField;
    @InjectView(R.id.male_image) ImageView mMaleIcon;
    @InjectView(R.id.female_image) ImageView mFemaleIcon;

    private ParseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mUser = ParseUser.getCurrentUser();

        mUsernameField.setText(mUser.getUsername());
        mEmailField.setText(mUser.getEmail());

        if (mUser.getNumber(ParseConstants.KEY_GENDER).intValue() == 1) {
            mMaleIcon.setColorFilter(ContextCompat.getColor(this, R.color.RegularBlue), PorterDuff.Mode.SRC_ATOP);
            mMaleIcon.setTag(1);
        }
        else {
            mFemaleIcon.setColorFilter(ContextCompat.getColor(this, R.color.RegularPink), PorterDuff.Mode.SRC_ATOP);
            mFemaleIcon.setTag(1);
        }

        if (!mUser.get(ParseConstants.KEY_AGE).equals(null)) {
            mAgeField.setText(mUser.get(ParseConstants.KEY_AGE).toString());
        }
    }

    @OnClick(R.id.save_button)
    public void onSaveClick() {
        String username = mUsernameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();

        if (!isEmailValid(email)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Invalid email!");
            builder.setPositiveButton("OK", null);
            builder.setMessage("Please make sure you enter a valid email.");
            builder.show();
            return;
        }

        if (!isUsernameValid(username)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Invalid username!");
            builder.setPositiveButton("OK", null);
            builder.setMessage("Username must be 1-15 characters long and may only contain: a-z, 0-9, and _");
            builder.show();
            return;
        }

        if (!email.equals(mUser.getEmail())) {
            doEmailQuery(username, email);
        }
        else if (!username.equals(mUser.getUsername())) {
            doUsernameQuery(username, email);
        }
        else {
            updateUser(username, email);
        }
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        NavUtils.navigateUpFromSameTask(this);
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

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isUsernameValid(CharSequence username){
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]{1,15}");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private void doEmailQuery(final String username, final String email) {
        ParseQuery<ParseUser> emailQuery = ParseUser.getQuery();
        emailQuery.whereEqualTo(ParseConstants.KEY_EMAIL, email);
        emailQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.isEmpty()) {
                        if (username.equals(mUser.getUsername())) {
                            doUsernameQuery(username, email);
                        }
                        else {
                            updateUser(username, email);
                        }
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Email taken!");
                        builder.setPositiveButton("OK", null);
                        builder.setMessage("Please try with a different email.");
                        builder.show();
                    }
                }
            }
        });
    }

    private void doUsernameQuery(final String username, final String email) {
        ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
        usernameQuery.whereEqualTo(ParseConstants.KEY_USERNAME, username);
        usernameQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.isEmpty()){
                        updateUser(username, email);
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Username taken!");
                        builder.setPositiveButton("OK", null);
                        builder.setMessage("Please try another username.");
                        builder.show();
                    }
                }
            }
        });
    }

    private void updateUser(String username, String email) {
        String ageString = mAgeField.getText().toString().trim();
        int age = Integer.parseInt(ageString);

        mUser.setUsername(username);
        mUser.setEmail(email);

        if (mMaleIcon.getTag().equals(1)) {
            mUser.put(ParseConstants.KEY_GENDER, 1);
        }
        else if (mFemaleIcon.getTag().equals(1)){
            mUser.put(ParseConstants.KEY_GENDER, 0);
        }

        mUser.put(ParseConstants.KEY_AGE, age);

        mUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    NavUtils.navigateUpFromSameTask(EditProfileActivity.this);
                }
            }
        });
    }
}