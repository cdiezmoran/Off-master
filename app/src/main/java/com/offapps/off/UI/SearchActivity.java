package com.offapps.off.UI;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.offapps.off.Adapters.CategoriesListAdapter;
import com.offapps.off.Adapters.MallSearchAdapter;
import com.offapps.off.Adapters.OfferSearchAdapter;
import com.offapps.off.Adapters.StoreSearchAdapter;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.Misc.WrappingLinearLayoutManager;
import com.offapps.off.R;
import com.parse.FindCallback;
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
    @InjectView(R.id.offersLinearLayout) LinearLayout mOffersLinearLayout;
    @InjectView(R.id.storesLinearLayout) LinearLayout mStoresLinearLayout;
    @InjectView(R.id.mallsLinearLayout) LinearLayout mMallsLinearLayout;
    @InjectView(R.id.progress_view) CircularProgressView mCircularProgressView;

    private SearchView mSearchView;
    private List<String> mTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
        OfferActivity.parents.push(getClass());
        StoreActivity.parents.push(getClass());
        MallActivity.parents.push(getClass());

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        initializeRecyclerViews();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        String[] categoriesList = {"Mujeres", "Hombres"};

        CategoriesListAdapter adapter = new CategoriesListAdapter(this, categoriesList);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mListView.setAdapter(adapter);
    }

    private void initializeRecyclerViews() {
        mOffersRecyclerView.setNestedScrollingEnabled(false);
        mOffersRecyclerView.setHasFixedSize(false);
        mOffersRecyclerView.setLayoutManager(new WrappingLinearLayoutManager(this));

        mStoresRecyclerView.setNestedScrollingEnabled(false);
        mStoresRecyclerView.setHasFixedSize(false);
        mStoresRecyclerView.setLayoutManager(new WrappingLinearLayoutManager(this));

        mMallRecyclerView.setNestedScrollingEnabled(false);
        mMallRecyclerView.setHasFixedSize(false);
        mMallRecyclerView.setLayoutManager(new WrappingLinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search_view);
        mSearchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        List<String> tags = getIntent().getStringArrayListExtra(ParseConstants.KEY_TAGS);
        if (tags != null) {
            String query = "";
            for (String tag : tags)
                query += tag + " ";
            mSearchView.setQuery(query.substring(0, query.length()-1), true);
        }

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
                //TODO
                //Suggestions while writing on search field
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
                    if (offers.isEmpty()) {
                        mOffersLinearLayout.setVisibility(View.GONE);
                        mOffersRecyclerView.setVisibility(View.GONE);
                    }//if
                    else {
                        OfferSearchAdapter offerAdapter = new OfferSearchAdapter(SearchActivity.this, offers, mTags);
                        mOffersRecyclerView.setAdapter(offerAdapter);

                        mOffersLinearLayout.setVisibility(View.VISIBLE);
                        mOffersRecyclerView.setVisibility(View.VISIBLE);

                        mCircularProgressView.setVisibility(View.GONE);
                        mScrollView.setVisibility(View.VISIBLE);
                    }//else

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
                    if (stores.isEmpty()) {
                        mStoresLinearLayout.setVisibility(View.GONE);
                        mStoresRecyclerView.setVisibility(View.GONE);
                    }//if
                    else {
                        StoreSearchAdapter storeAdapter = new StoreSearchAdapter(SearchActivity.this, stores, mTags);
                        mStoresRecyclerView.setAdapter(storeAdapter);

                        mStoresLinearLayout.setVisibility(View.VISIBLE);
                        mStoresRecyclerView.setVisibility(View.VISIBLE);

                        if (offers.isEmpty()) {
                            mCircularProgressView.setVisibility(View.GONE);
                            mScrollView.setVisibility(View.VISIBLE);
                        }
                    }//else
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
                    if (malls.isEmpty()) {
                        mMallsLinearLayout.setVisibility(View.GONE);
                        mMallRecyclerView.setVisibility(View.GONE);
                    }//if
                    else {
                        MallSearchAdapter mallAdapter = new MallSearchAdapter(SearchActivity.this, malls, mTags);
                        mMallRecyclerView.setAdapter(mallAdapter);

                        mMallsLinearLayout.setVisibility(View.VISIBLE);
                        mMallRecyclerView.setVisibility(View.VISIBLE);

                        if (offers.isEmpty() && stores.isEmpty()){
                            mCircularProgressView.setVisibility(View.GONE);
                            mScrollView.setVisibility(View.VISIBLE);
                        }
                    }//else
                }//if
            }//done
        });//findInBackground
    }//doMallQuery

    @OnClick(R.id.offersLinearLayout)
    public void onOffersLinearLayoutClick(){
        goToSearchResults("offers");
    }

    @OnClick(R.id.storesLinearLayout)
    public void onStoresLinearLayoutClick() {
        goToSearchResults("stores");
    }

    @OnClick(R.id.mallsTextView)
    public void onMallsLinearLayoutClick(){
        goToSearchResults("malls");
    }

    private void goToSearchResults(String extra) {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, new ArrayList<>(mTags));
        intent.putExtra("extra", extra);
        startActivity(intent);
    }
}