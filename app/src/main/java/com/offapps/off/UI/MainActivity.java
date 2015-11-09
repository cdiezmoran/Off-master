package com.offapps.off.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.offapps.off.Adapters.OfferAdapter;
import com.offapps.off.Data.Like;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Offer;
import com.offapps.off.Misc.OffApplication;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    private OfferAdapter mOfferAdapter;
    private List<Like> mLikes = new ArrayList<>();

    @InjectView(R.id.tool_bar) Toolbar mToolbar;
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.progress_view) CircularProgressView mCircularProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        if (ParseUser.getCurrentUser() == null) {
            navigateToSignUp();
        }
        else {
            OfferActivity.parents.push(getClass());

            mCircularProgressView.startAnimation();

            mRecyclerView.setHasFixedSize(true);

            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            mRecyclerView.setLayoutManager(layoutManager);

            setSupportActionBar(mToolbar);
            doLikeQuery();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        OffApplication.getConfigHelper().fetchConfigIfNeeded();
    }

    private void doLikeQuery() {
        ParseQuery<Like> query = Like.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.orderByAscending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> list, ParseException e) {
                if (e == null) {
                    mLikes = list;
                    setGridItems();
                }
            }
        });
    }

    private void setGridItems() {
        ParseQuery<Offer> query = Offer.getQuery();
        query.orderByAscending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> list, ParseException e) { //Get the offer list for the grid adapter
                if (e == null) {
                    mOfferAdapter = new OfferAdapter(MainActivity.this, list, "", mLikes);
                    mRecyclerView.setAdapter(mOfferAdapter);

                    mCircularProgressView.resetAnimation();
                    mCircularProgressView.setVisibility(View.GONE);

                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.logOut();
            navigateToSignUp();
        }
        else if(id == R.id.action_search){
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, null);
            startActivity(intent);
        }
        else if(id == R.id.action_add){
            Intent intent = new Intent(this, AddOfferActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_map) {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
