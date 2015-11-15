package com.offapps.off.UI;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Floor;
import com.offapps.off.Data.Follow;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Mall_Store;
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

import java.util.ArrayList;
import java.util.Stack;

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
    @InjectView(R.id.mallTextView) TextView mMallTextView;
    @InjectView(R.id.floorTextView) TextView mFloorTextView;
    @InjectView(R.id.mall_divider) View mMallDivider;
    @InjectView(R.id.floor_divider) View mFloorDivider;
    @InjectView(R.id.local_divider) View mLocalDivider;
    @InjectView(R.id.tool_bar) Toolbar mToolbar;

    public static Stack<Class<?>> parents = new Stack<>();

    private String mStoreId;
    private Store mStore;
    private Follow mFollow;

    private ArrayList<String> mTags;
    private Class<?> mParentClass;
    private String mOfferId;
    private String mMallId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ButterKnife.inject(this);
        OffersActivity.parents.push(getClass());

        setCompoundDrawables();

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        mStoreId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);

        if (!parents.empty()) {
            mParentClass = parents.pop();
            if (mParentClass == SearchResultsActivity.class || mParentClass == SearchActivity.class) {
                mTags = intent.getStringArrayListExtra(ParseConstants.KEY_TAGS);
            }
            else if (mParentClass == OfferActivity.class) {
                mOfferId = intent.getStringExtra("offerId");
            }
            else if (mParentClass == MallMapsActivity.class) {
                mMallId = intent.getStringExtra("mallId");
            }
        }

        getStore();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setCompoundDrawables();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mParentClass == SearchResultsActivity.class || mParentClass == SearchActivity.class) {
                Intent intent = new Intent(this, mParentClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, mTags);
                intent.putExtra("extra", "stores");
                startActivity(intent);
            }
            else if (mParentClass == OfferActivity.class) {
                Intent intent = new Intent(this, mParentClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ParseConstants.KEY_OBJECT_ID, mOfferId);
                startActivity(intent);
            }
            else if (mParentClass == MallMapsActivity.class) {
                Intent intent = new Intent(this, mParentClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ParseConstants.KEY_OBJECT_ID, mMallId);
                startActivity(intent);
            }
            else{
                NavUtils.navigateUpFromSameTask(this);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setCompoundDrawables(){
        final Drawable categoryDrawable = ContextCompat.getDrawable(this, R.drawable.ic_tshirt_crew_black_18dp);
        categoryDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mCategoryTextView.setCompoundDrawablesWithIntrinsicBounds(categoryDrawable, null, null, null);

        final Drawable numOffersDrawable = ContextCompat.getDrawable(this, R.drawable.ic_tag_multiple_black_18dp);
        numOffersDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mNumOffersTextView.setCompoundDrawablesWithIntrinsicBounds(numOffersDrawable, null, null, null);

        final Drawable mallDrawable = ContextCompat.getDrawable(this, R.drawable.ic_domain_black_18dp);
        mallDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mMallTextView.setCompoundDrawablesWithIntrinsicBounds(mallDrawable, null, null, null);

        final Drawable floorDrawable = ContextCompat.getDrawable(this, R.drawable.ic_escalator_black_18dp);
        floorDrawable.setColorFilter(ContextCompat.getColor(this, R.color.ColorPrimary), PorterDuff.Mode.SRC_ATOP);
        mFloorTextView.setCompoundDrawablesWithIntrinsicBounds(floorDrawable, null, null, null);

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
                } else {
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

        if (mParentClass == MallMapsActivity.class)
            doMallQuery();
        else
            setMallViewsVisibility(View.GONE);


        Picasso.with(this).load(mStore.getImageString()).into(mProfileImage);
        Picasso.with(this).load(mStore.getHeaderString()).into(mHeaderImageView);
    }

    public void doMallQuery() {
        ParseQuery<Mall> query = Mall.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mMallId);
        query.getFirstInBackground(new GetCallback<Mall>() {
            @Override
            public void done(Mall mall, ParseException e) {
                if (e == null)
                    doMallStoreQuery(mall);
            }
        });
    }

    public void doMallStoreQuery(final Mall mall){
        ParseQuery<Mall_Store> query = Mall_Store.getQuery();
        query.whereEqualTo(ParseConstants.KEY_STORE, mStore);
        query.whereEqualTo(ParseConstants.KEY_MALL, mall);
        query.getFirstInBackground(new GetCallback<Mall_Store>() {
            @Override
            public void done(Mall_Store object, ParseException e) {
                if (e == null) {
                    setMallViewsVisibility(View.VISIBLE);
                    mLocalTextView.setText(object.getLocal());
                    mMallTextView.setText(mall.getName());
                    try {
                        Floor floor = object.getFloor().fetchIfNeeded();
                        mFloorTextView.setText(floor.getName());
                    } catch(ParseException e1) {

                    }
                }
            }
        });
    }

    public void setMallViewsVisibility(int visibility) {
        mMallDivider.setVisibility(visibility);
        mFloorDivider.setVisibility(visibility);
        mLocalDivider.setVisibility(visibility);

        mMallTextView.setVisibility(visibility);
        mFloorTextView.setVisibility(visibility);
        mLocalTextView.setVisibility(visibility);
    }

    @OnClick(R.id.offersTextView)
    public void onOffersClick(){
        Intent intent = new Intent(this, OffersActivity.class);
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