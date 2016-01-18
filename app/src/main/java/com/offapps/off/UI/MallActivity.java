package com.offapps.off.UI;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Adapters.OfferAdapter;
import com.offapps.off.Data.Like;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Offer;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.Misc.WrappingStaggeredGridLayoutManager;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MallActivity extends AppCompatActivity {

    @InjectView(R.id.headerImageView) ImageView mHeaderImage;
    @InjectView(R.id.mallProfileImage) CircularImageView mProfileImage;
    @InjectView(R.id.addressTextView) TextView mAddressTextView;
    @InjectView(R.id.scheduleTextView) TextView mScheduleTextView;
    @InjectView(R.id.twitterTextView) TextView mTwitterTextView;
    @InjectView(R.id.facebookTextView) TextView mFacebookTextView;
    @InjectView(R.id.webPageTextView) TextView mWebPageTextView;
    @InjectView(R.id.nameTextView) TextView mNameTextView;
    @InjectView(R.id.tool_bar) Toolbar mToolbar;
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;

    public static Stack<Class<?>> parents = new Stack<>();
    private Class<?> mParentClass;
    private ArrayList<String> mTags;

    private String mMallId;
    private Mall mMall;
    private List<Offer> mOffers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall);
        ButterKnife.inject(this);
        OffersActivity.parents.push(getClass());

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        WrappingStaggeredGridLayoutManager layoutManager = new WrappingStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        mMallId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);

        if (!parents.empty()) {
            mParentClass = parents.pop();
            if (mParentClass == SearchResultsActivity.class|| mParentClass == SearchActivity.class) {
                mTags = intent.getStringArrayListExtra(ParseConstants.KEY_TAGS);
            }
        }

        getMall();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setCompoundDrawablesColor();
    }

    private void setCompoundDrawablesColor() {
        final Drawable addressDrawable = ContextCompat.getDrawable(this, R.drawable.ic_room_black_18dp);
        addressDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mAddressTextView.setCompoundDrawablesWithIntrinsicBounds(addressDrawable, null, null, null);

        final Drawable scheduleDrawable = ContextCompat.getDrawable(this, R.drawable.ic_schedule_black_18dp);
        scheduleDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mScheduleTextView.setCompoundDrawablesWithIntrinsicBounds(scheduleDrawable, null, null, null);

        final Drawable webDrawable = ContextCompat.getDrawable(this, R.drawable.ic_television_guide_black_18dp);
        webDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mWebPageTextView.setCompoundDrawablesWithIntrinsicBounds(webDrawable, null, null, null);

        final Drawable twitterDrawable = ContextCompat.getDrawable(this, R.drawable.ic_twitter_black_18dp);
        twitterDrawable.setColorFilter(ContextCompat.getColor(this, R.color.TwitterColor), PorterDuff.Mode.SRC_ATOP);
        mTwitterTextView.setCompoundDrawablesWithIntrinsicBounds(twitterDrawable, null, null, null);

        final Drawable facebookDrawable = ContextCompat.getDrawable(this, R.drawable.ic_facebook_black_18dp);
        facebookDrawable.setColorFilter(ContextCompat.getColor(this, R.color.FacebookColor), PorterDuff.Mode.SRC_ATOP);
        mFacebookTextView.setCompoundDrawablesWithIntrinsicBounds(facebookDrawable, null, null, null);
    }

    private void getMall() {
        ParseQuery<Mall> query = Mall.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mMallId);
        query.getFirstInBackground(new GetCallback<Mall>() {
            @Override
            public void done(Mall mall, ParseException e) {
                if (e == null) {
                    mMall = mall;
                    setData();
                    doTopOffersQuery();
                }
            }
        });
    }

    private void doTopOffersQuery(){
        ParseQuery<Offer> offerQuery = Offer.getQuery();
        offerQuery.whereEqualTo(ParseConstants.KEY_MALL, mMall);
        offerQuery.orderByAscending(ParseConstants.KEY_VIEW_COUNT);
        offerQuery.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 10)
                        for (int i = 0; i < 10; i++)
                            mOffers.add(list.get(i));
                    else
                        for (Offer offer : list)
                            mOffers.add(offer);
                    doLikeQuery();
                }//if
            } //done
        });
    }

    private void doLikeQuery(){
        ParseQuery<Like> query = Like.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> list, ParseException e) {
                if (e == null) {
                    OfferAdapter adapter = new OfferAdapter(MallActivity.this, mOffers, "Top", list);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void setData() {
        mToolbar.setTitle(mMall.getName());

        mNameTextView.setText(mMall.getName());
        mAddressTextView.setText(mMall.getAddress());
        mScheduleTextView.setText(mMall.getSchedule());
        mWebPageTextView.setText(mMall.getWebPage());
        String twitterString = "@" + mMall.getTwitterUser();
        mTwitterTextView.setText(twitterString);
        mFacebookTextView.setText(mMall.getFacebookUser());

        Picasso.with(this).load(mMall.getImageUri().toString()).into(mProfileImage);
        Picasso.with(this).load(mMall.getHeaderString()).into(mHeaderImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mParentClass == SearchResultsActivity.class || mParentClass == SearchActivity.class){
                Intent intent = new Intent(this, mParentClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, mTags);
                intent.putExtra("extra", "malls");
                startActivity(intent);
            }
            else {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.offersTextView)
    public void onClickOffers() {
        Intent intent = new Intent(this, OffersActivity.class);
        intent.putExtra(ParseConstants.KEY_OBJECT_ID, mMallId);
        startActivity(intent);
    }

    @OnClick(R.id.mallMapTextView)
    public void onClickMallMap() {
        Intent intent = new Intent(this, MallMapsActivity.class);
        intent.putExtra(ParseConstants.KEY_OBJECT_ID, mMallId);
        startActivity(intent);
    }

    @OnClick(R.id.twitterTextView)
    public void onClickTwitter() {
        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + mMall.getTwitterUser()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + mMall.getTwitterUser()));
        }
        startActivity(intent);
    }

    @OnClick(R.id.facebookTextView)
    public void onClickFacebook() {
        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/<id_here>"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + mMall.getFacebookUser()));
        }
        startActivity(intent);
    }

    @OnClick(R.id.webPageTextView)
    public void onClickWebPage() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mMall.getWebPage()));
        startActivity(intent);
    }

    @OnClick(R.id.addressTextView)
    public void onClickAddress() {
        String locationString = "geo:0,0?q=" + mMall.getLocation().getLatitude() + ", "
                + mMall.getLocation().getLongitude();
        Uri gMapsUri = Uri.parse(locationString);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gMapsUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MallActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Something went wrong!");
            builder.setPositiveButton("ACCEPT", null);
            builder.setMessage("There was an error opening the mall's location on Google Maps, please check if you have Google Maps installed or if you are connected to the internet.");
            builder.show();
        }
    }
}
