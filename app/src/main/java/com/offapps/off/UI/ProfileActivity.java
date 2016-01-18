package com.offapps.off.UI;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.profileImage) ImageView mProfileImage;
    @InjectView(R.id.usernameTextView) TextView mUsernameTextView;
    @InjectView(R.id.emailTextView) TextView mEmailTextView;
    @InjectView(R.id.genderTextView) TextView mGenderTextView;
    @InjectView(R.id.ageTextView) TextView mAgeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setTitle("Profile");

        ParseUser user = ParseUser.getCurrentUser();

        mUsernameTextView.setText(user.getUsername());
        mEmailTextView.setText(user.getEmail());

        Drawable genderDrawable;
        if (user.getNumber(ParseConstants.KEY_GENDER).intValue() == 1){
            mGenderTextView.setText("Male");
            genderDrawable = ContextCompat.getDrawable(this, R.drawable.ic_gender_male_black_24dp);
        }
        else {
            mGenderTextView.setText("Female");
            genderDrawable = ContextCompat.getDrawable(this, R.drawable.ic_gender_female_black_24dp);
        }
        mGenderTextView.setCompoundDrawablesWithIntrinsicBounds(genderDrawable, null, null, null);

        if (user.get(ParseConstants.KEY_AGE).equals(null)) {
            mAgeTextView.setText("Undefined");
        }
        else {
            mAgeTextView.setText(user.get(ParseConstants.KEY_AGE).toString());
        }


        ParseFile file = ParseUser.getCurrentUser().getParseFile(ParseConstants.KEY_IMAGE);
        String userImageString = Uri.parse(file.getUrl()).toString();
        Picasso.with(this).load(userImageString).into(mProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout){
            ParseUser.logOutInBackground();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.card_view)
    public void onEditCardClick() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}