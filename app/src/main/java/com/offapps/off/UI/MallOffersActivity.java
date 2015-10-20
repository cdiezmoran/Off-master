package com.offapps.off.UI;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.offapps.off.Adapters.OfferAdapter;
import com.offapps.off.Data.Like;
import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Offer;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MallOffersActivity extends AppCompatActivity {

    private List<Offer> mOfferList = new ArrayList<>();
    private List<Like> mLikes = new ArrayList<>();
    private String mMallId;

    @InjectView(R.id.tool_bar) Toolbar mToolbar;
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_offers);
        ButterKnife.inject(this);
        OfferActivity.parents.push(getClass());

        mRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(layoutManager);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        Intent intent = getIntent();
        mMallId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);

        getMall();
    }

    private void getMall(){
        ParseQuery<Mall> query = Mall.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mMallId);
        query.getFirstInBackground(new GetCallback<Mall>() {
            @Override
            public void done(Mall mall, ParseException e) {
                if (e == null) {
                    mToolbar.setTitle(mall.getName());
                    doLikeQuery(mall);
                } else {
                    //show error dialog
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorPrimary));
    }

    private void doLikeQuery(final Mall mall) {
        ParseQuery<Like> query = Like.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.orderByAscending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> list, ParseException e) {
                if (e == null) {
                    mLikes = list;
                    setGridItems(mall);
                }
            }
        });
    }

    private void setGridItems(Mall mall) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.TABLE_MALL_OFFER);
        query.whereEqualTo(ParseConstants.KEY_MALL, mall);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    for (ParseObject object : list) {
                        try {
                            mOfferList.add((Offer) object.getParseObject(ParseConstants.KEY_OFFER).fetchIfNeeded());
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    OfferAdapter adapter = new OfferAdapter(MallOffersActivity.this, mOfferList, mMallId, mLikes);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    //TODO
                    //add an error dialog
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mall_offers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MallActivity.class);
            intent.putExtra(ParseConstants.KEY_OBJECT_ID, mMallId);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_mall) {
            Intent intent = new Intent(this, MallMapsActivity.class);
            intent.putExtra(ParseConstants.KEY_OBJECT_ID, mMallId);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
