package com.offapps.off.UI;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.offapps.off.Adapters.CategoriesListAdapter;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SearchView mSearchView;

    @InjectView(android.R.id.list) ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        String[] categoriesList = {"Hombres", "Mujeres", "Camisas", "Zapatos", "Vestidos", "Niños",
                "Bebes", "Popular", "Trending"};

        CategoriesListAdapter adapter = new CategoriesListAdapter(this, categoriesList);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mListView.setAdapter(adapter);
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
                List<String> tags = Arrays.asList(tagsArr);

                doOfferQuery(tags);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void doOfferQuery(final List<String> tags) {
        ParseQuery<Offer> offerQuery = Offer.getQuery();
        offerQuery.whereContainsAll(ParseConstants.KEY_TAGS, tags);
        offerQuery.setLimit(2);
        offerQuery.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> offers, ParseException e) {
                if (e == null) {
                    doStoreQuery(tags, offers);
                }
            }
        });
    }

    private void doStoreQuery(final List<String> tags, final List<Offer> offers){
        ParseQuery<Store> storeQuery = Store.getQuery();
        storeQuery.whereContainsAll(ParseConstants.KEY_TAGS, tags);
        storeQuery.setLimit(2);
        storeQuery.findInBackground(new FindCallback<Store>() {
            @Override
            public void done(List<Store> stores, ParseException e) {
                if (e == null) {
                    doMallQuery(tags, offers, stores);
                }
            }
        });
    }

    private void doMallQuery(final List<String> tags, final List<Offer> offers, final List<Store> stores) {
        ParseQuery<Mall> mallQuery = Mall.getQuery();
        mallQuery.whereContainsAll(ParseConstants.KEY_TAGS, tags);
        mallQuery.setLimit(2);
        mallQuery.findInBackground(new FindCallback<Mall>() {
            @Override
            public void done(List<Mall> malls, ParseException e) {
                if (e == null) {

                }
            }
        });
    }
}