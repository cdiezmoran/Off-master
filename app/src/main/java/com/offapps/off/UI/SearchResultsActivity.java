package com.offapps.off.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.offapps.off.Adapters.MallSearchAdapter;
import com.offapps.off.Adapters.OfferSearchAdapter;
import com.offapps.off.Adapters.StoreSearchAdapter;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchResultsActivity extends AppCompatActivity {

    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.toolbar) Toolbar mToolbar;

    private List<String> mTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String extra = intent.getStringExtra("extra");

        mTags = intent.getStringArrayListExtra(ParseConstants.KEY_TAGS);

        switch (extra) {
            case "offers":
                doOffersQuery();
                break;
            case "stores":
                doStoresQuery();
                break;
            case "malls":
                doMallsQuery();
                break;
        }
    }

    private void doOffersQuery() {
        ParseQuery<Offer> offerQuery = Offer.getQuery();
        offerQuery.whereContainsAll(ParseConstants.KEY_TAGS, mTags);
        offerQuery.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> offers, ParseException e) {
                if (e == null) {
                    mToolbar.setTitle("Offers");
                    OfferSearchAdapter adapter = new OfferSearchAdapter(SearchResultsActivity.this, offers);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void doStoresQuery() {
        ParseQuery<Store> storeQuery = Store.getQuery();
        storeQuery.whereContainsAll(ParseConstants.KEY_TAGS, mTags);
        storeQuery.findInBackground(new FindCallback<Store>() {
            @Override
            public void done(List<Store> stores, ParseException e) {
                if (e == null){
                    mToolbar.setTitle("Stores");
                    StoreSearchAdapter adapter = new StoreSearchAdapter(SearchResultsActivity.this, stores);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void doMallsQuery() {
        ParseQuery<Mall> mallQuery = Mall.getQuery();
        mallQuery.whereContainsAll(ParseConstants.KEY_TAGS, mTags);
        mallQuery.findInBackground(new FindCallback<Mall>() {
            @Override
            public void done(List<Mall> malls, ParseException e) {
                if (e == null) {
                    mToolbar.setTitle("Malls");
                    MallSearchAdapter adapter = new MallSearchAdapter(SearchResultsActivity.this, malls);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }
}