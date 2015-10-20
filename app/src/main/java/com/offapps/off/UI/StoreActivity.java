package com.offapps.off.UI;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Follow;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class StoreActivity extends AppCompatActivity {

    @InjectView(R.id.headerImageView) ImageView mHeaderImageView;
    @InjectView(R.id.storeProfileImage) CircularImageView mProfileImage;
    @InjectView(R.id.nameTextView) TextView mNameTextView;
    @InjectView(R.id.categoryTextView) TextView mCategoryTextView;
    @InjectView(R.id.numOffersTextView) TextView mNumOffersTextView;
    @InjectView(R.id.localTextView) TextView mLocalTextView;
    @InjectView(R.id.twitterTextView) TextView mTwitterTextView;
    @InjectView(R.id.facebookTextView) TextView mFacebookTextView;
    @InjectView(R.id.webPageTextView) TextView mWebPageTextView;
    @InjectView(R.id.followTextView) TextView mFollowTextView;
    @InjectView(R.id.tool_bar) Toolbar mToolbar;

    private String mStoreId;
    private Store mStore;
    private Follow mFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ButterKnife.inject(this);

        setCompoundDrawables();

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        mStoreId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);

        getStore();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setCompoundDrawables();
    }

    private void setCompoundDrawables(){
        final Drawable categoryDrawable = ContextCompat.getDrawable(this, R.drawable.ic_tshirt_crew_black_18dp);
        categoryDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mCategoryTextView.setCompoundDrawablesWithIntrinsicBounds(categoryDrawable, null, null, null);

        final Drawable numOffersDrawable = ContextCompat.getDrawable(this, R.drawable.ic_tag_multiple_black_18dp);
        numOffersDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mNumOffersTextView.setCompoundDrawablesWithIntrinsicBounds(numOffersDrawable, null, null, null);

        final Drawable localDrawable = ContextCompat.getDrawable(this, R.drawable.ic_store_white_24dp);
        localDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mLocalTextView.setCompoundDrawablesWithIntrinsicBounds(localDrawable, null, null, null);

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

    private void getStore(){
        ParseQuery<Store> query = Store.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mStoreId);
        query.getFirstInBackground(new GetCallback<Store>() {
            @Override
            public void done(Store store, ParseException e) {
                if (e == null) {
                    mStore = store;
                    countStoreOffers();
                    setData();
                    doFollowsQuery();
                } else {
                    //Show error dialog
                }
            }
        });
    }

    private void doFollowsQuery() {
        ParseQuery<Follow> query = Follow.getQuery();
        query.whereEqualTo(ParseConstants.KEY_STORE, mStore);
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<Follow>() {
            @Override
            public void done(Follow follow, ParseException e) {
                if (e == null) {
                    mFollow = follow;

                    if (mFollow != null)
                        setFollowingTextView();
                    else
                        setNotFollowingTextView();
                }
                else {
                    //Show error dialog
                }
            }
        });
    }

    private void countStoreOffers(){
        ParseQuery<Offer> query = Offer.getQuery();
        query.whereEqualTo(ParseConstants.KEY_STORE, mStore);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                if (e == null) {
                    String numOffersText;
                    if (i == 1) {
                        numOffersText = i + " " + getString(R.string.offer_available_text);
                    } else {
                        numOffersText = i + " " + getString(R.string.offers_available_text);
                    }
                    mNumOffersTextView.setText(numOffersText);
                } else {
                    //show error dialog
                }
            }
        });
    }

    private void setData(){
        mToolbar.setTitle(mStore.getName());

        mNameTextView.setText(mStore.getName());
        mCategoryTextView.setText(mStore.getCategory());
        String twitterString = "@" + mStore.getTwitterUser();
        mTwitterTextView.setText(twitterString);
        mFacebookTextView.setText(mStore.getFacebookUser());
        mWebPageTextView.setText(mStore.getWebPage());

        mLocalTextView.setVisibility(View.GONE);

        Picasso.with(this).load(mStore.getImageString()).into(mProfileImage);
        Picasso.with(this).load(mStore.getHeaderString()).into(mHeaderImageView);
    }

    @OnClick(R.id.offersTextView)
    public void onOffersClick(){
        Intent intent = new Intent(this, StoreOffersActivity.class);
        intent.putExtra(ParseConstants.KEY_OBJECT_ID, mStoreId);
        startActivity(intent);
    }

    @OnClick(R.id.twitterTextView)
    public void onClickTwitter() {
        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + mStore.getTwitterUser()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + mStore.getTwitterUser()));
        }
        startActivity(intent);
    }

    @OnClick(R.id.webPageTextView)
    public void onClickWebPage() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mStore.getWebPage()));
        startActivity(intent);
    }

    @OnClick(R.id.followTextView)
    public void onClickFollow(){
        int followers = mStore.getFollowers();

        mFollowTextView.setEnabled(false);
        if (mFollow != null) {
            setNotFollowingTextView();

            followers -= 1;
            mStore.setFollowers(followers);

            mFollow.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                        mStore.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    mFollowTextView.setEnabled(true);
                            }
                        });
                }
            });
            mFollow = null;
        }
        else {
            setFollowingTextView();

            mFollow = new Follow();
            mFollow.setUser(ParseUser.getCurrentUser());
            mFollow.setStore(mStore);

            followers += 1;
            mStore.setFollowers(followers);

            mFollow.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                        mStore.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    mFollowTextView.setEnabled(true);
                            }
                        });
                }
            });
        }
    }

    private void setFollowingTextView() {
        final Drawable followingDrawable = ContextCompat.getDrawable(this, R.drawable.ic_checkbox_marked_circle_outline_black_18dp);
        followingDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mFollowTextView.setCompoundDrawablesWithIntrinsicBounds(followingDrawable, null, null, null);

        mFollowTextView.setText(R.string.following_text);
        mFollowTextView.setTextColor(ContextCompat.getColor(this, R.color.ColorPrimary));
    }

    private void setNotFollowingTextView() {
        final Drawable followingDrawable = ContextCompat.getDrawable(this, R.drawable.ic_plus_circle_outline_black_18dp);
        followingDrawable.setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        mFollowTextView.setCompoundDrawablesWithIntrinsicBounds(followingDrawable, null, null, null);

        mFollowTextView.setText(R.string.follow_text);
        mFollowTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
    }
}