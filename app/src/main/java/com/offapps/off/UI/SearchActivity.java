package com.offapps.off.UI;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.offapps.off.Adapters.CategoriesListAdapter;
import com.offapps.off.Adapters.MallSearchAdapter;
import com.offapps.off.Adapters.OfferSearchAdapter;
import com.offapps.off.Adapters.StoreSearchAdapter;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ExpandableHeightRecyclerView;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.Misc.WrappingLinearLayoutManager;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

    @InjectView(android.R.id.list) ListView mListView;
    @InjectView(R.id.scrollView) ScrollView mScrollView;
    @InjectView(R.id.offer_recycler_view) RecyclerView mOffersRecyclerView;
    @InjectView(R.id.store_recycler_view) RecyclerView mStoresRecyclerView;
    @InjectView(R.id.mall_recycler_view) RecyclerView mMallRecyclerView;
    @InjectView(R.id.progress_view) CircularProgressView mCircularProgressView;

    private Toolbar mToolbar;
    private SearchView mSearchView;
    private List<String> mTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        initializeRecyclerViews();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        String[] categoriesList = {"Hombres", "Mujeres", "Camisas", "Zapatos", "Vestidos", "Niños",
                "Popular", "Trending"};

        CategoriesListAdapter adapter = new CategoriesListAdapter(this, categoriesList);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mListView.setAdapter(adapter);
    }

    private void initializeRecyclerViews() {
        mOffersRecyclerView.setNestedScrollingEnabled(false);
        mOffersRecyclerView.setHasFixedSize(true);
        mOffersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mStoresRecyclerView.setNestedScrollingEnabled(false);
        mStoresRecyclerView.setHasFixedSize(true);
        mStoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMallRecyclerView.setNestedScrollingEnabled(false);
        mMallRecyclerView.setHasFixedSize(true);
        mMallRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorPrimary));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search_view);
        mSearchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();

                String[] tagsArr = query.split(" ");
                mTags = Arrays.asList(tagsArr);

                mListView.setVisibility(View.GONE);
                mCircularProgressView.setVisibility(View.VISIBLE);
                mCircularProgressView.startAnimation();

                doOfferQuery();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void doOfferQuery() {
        ParseQuery<Offer> offerQuery = Offer.getQuery();
        offerQuery.whereContainsAll(ParseConstants.KEY_TAGS, mTags);
        offerQuery.setLimit(2);
        offerQuery.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> offers, ParseException e) {
                if (e == null) {
                    doStoreQuery(offers);
                }
            }
        });
    }

    private void doStoreQuery(final List<Offer> offers){
        ParseQuery<Store> storeQuery = Store.getQuery();
        storeQuery.whereContainsAll(ParseConstants.KEY_TAGS, mTags);
        storeQuery.setLimit(2);
        storeQuery.findInBackground(new FindCallback<Store>() {
            @Override
            public void done(List<Store> stores, ParseException e) {
                if (e == null) {
                    doMallQuery(offers, stores);
                }
            }
        });
    }

    private void doMallQuery(final List<Offer> offers, final List<Store> stores) {
        ParseQuery<Mall> mallQuery = Mall.getQuery();
        mallQuery.whereContainsAll(ParseConstants.KEY_TAGS, mTags);
        mallQuery.setLimit(2);
        mallQuery.findInBackground(new FindCallback<Mall>() {
            @Override
            public void done(List<Mall> malls, ParseException e) {
                if (e == null) {
                    OfferSearchAdapter offerAdapter = new OfferSearchAdapter(SearchActivity.this, offers);
                    StoreSearchAdapter storeAdapter = new StoreSearchAdapter(SearchActivity.this, stores);
                    MallSearchAdapter mallAdapter = new MallSearchAdapter(SearchActivity.this, malls);

                    mOffersRecyclerView.setAdapter(offerAdapter);
                    mStoresRecyclerView.setAdapter(storeAdapter);
                    mMallRecyclerView.setAdapter(mallAdapter);

                    mCircularProgressView.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick(R.id.offersLinearLayout)
    public void onOffersLinearLayoutClick(){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, new ArrayList<>(mTags));
        intent.putExtra("extra", "offers");
        startActivity(intent);
    }

    @OnClick(R.id.storesLinearLayout)
    public void onStoresLinearLayoutClick() {
        Intent intent =  new Intent(this, SearchResultsActivity.class);
        intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, new ArrayList<>(mTags));
        intent.putExtra("extra", "stores");
        startActivity(intent);
    }

    @OnClick(R.id.mallsTextView)
    public void onMallsLinearLayoutClick(){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, new ArrayList<>(mTags));
        intent.putExtra("extra", "malls");
        startActivity(intent);
    }
}