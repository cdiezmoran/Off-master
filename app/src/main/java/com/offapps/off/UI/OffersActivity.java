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
import com.offapps.off.Data.Store;
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
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OffersActivity extends AppCompatActivity {

    private List<Offer> mOfferList = new ArrayList<>();
    private List<Like> mLikes = new ArrayList<>();
    private String mMallId;
    private String mStoreId;

    private Class<?> mParentClass;
    public static Stack<Class<?>> parents = new Stack<>();

    @InjectView(R.id.tool_bar) Toolbar mToolbar;
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
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

        if (!parents.empty()) {
            mParentClass = parents.pop();
            if (mParentClass == MallActivity.class) {
                mMallId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);
                getMall();
            } else if (mParentClass == StoreActivity.class) {
                mStoreId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);
                getStore();
            }
        }
    }

    private void getMall(){
        ParseQuery<Mall> query = Mall.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mMallId);
        query.getFirstInBackground(new GetCallback<Mall>() {
            @Override
            public void done(Mall mall, ParseException e) {
                if (e == null) {
                    mToolbar.setTitle(mall.getName());
                    doLikeQuery(mall, null);
                } else {
                    //show error dialog
                }
            }
        });
    }

    private void getStore(){
        ParseQuery<Store> query = Store.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mStoreId);
        query.getFirstInBackground(new GetCallback<Store>() {
            @Override
            public void done(Store store, ParseException e) {
                if (e == null) {
                    mToolbar.setTitle(store.getName());
                    doLikeQuery(null, store);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.ColorPrimary));
    }

    private void doLikeQuery(final Mall mall, final Store store) {
        ParseQuery<Like> query = Like.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.orderByAscending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> list, ParseException e) {
                if (e == null) {
                    mLikes = list;
                    setGridItems(mall, store);
                }
            }
        });
    }

    private void setGridItems(Mall mall, Store store) {
        if (mall != null) {
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
                        OfferAdapter adapter = new OfferAdapter(OffersActivity.this, mOfferList, mMallId, mLikes);
                        mRecyclerView.setAdapter(adapter);
                    } else {
                        //TODO
                        //add an error dialog
                    }
                }
            });
        }
        else {
            ParseQuery<Offer> query = Offer.getQuery();
            query.whereEqualTo(ParseConstants.KEY_STORE, store);
            query.findInBackground(new FindCallback<Offer>() {
                @Override
                public void done(List<Offer> list, ParseException e) {
                    if (e == null) {
                        OfferAdapter adapter = new OfferAdapter(OffersActivity.this, list, mStoreId, mLikes);
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_offers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mParentClass == MallActivity.class) {
                Intent intent = new Intent(this, MallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ParseConstants.KEY_OBJECT_ID, mMallId);
                startActivity(intent);
            }
            else if (mParentClass == StoreActivity.class) {
                Intent intent = new Intent(this, StoreActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ParseConstants.KEY_OBJECT_ID, mStoreId);
                startActivity(intent);
            }
            else {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
