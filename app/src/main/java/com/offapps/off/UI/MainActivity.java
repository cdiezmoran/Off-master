package com.offapps.off.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.offapps.off.Adapters.OfferAdapter;
import com.offapps.off.Data.Follow;
import com.offapps.off.Data.Like;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.OffApplication;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private List<Like> mLikes = new ArrayList<>();
    private List<Offer> mLikedOffers = new ArrayList<>();

    @InjectView(R.id.tool_bar) Toolbar mToolbar;
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.progress_view) CircularProgressView mCircularProgressView;
    @InjectView(R.id.drawerLayout) DrawerLayout mDrawer;
    @InjectView(R.id.navigation_view) NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        OfferActivity.parents.push(getClass());

        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mCircularProgressView.startAnimation();

        mRecyclerView.setHasFixedSize(true);

        doLikeQuery(0);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mToolbar.setTitle("Popular");

        setUpDrawer();
    }

    private void setUpDrawer() {
        String username;
        String email;
        String userImageString;

        if (ParseUser.getCurrentUser() != null) {
            username = ParseUser.getCurrentUser().getUsername();
            email = ParseUser.getCurrentUser().getEmail();
            ParseFile file = ParseUser.getCurrentUser().getParseFile(ParseConstants.KEY_IMAGE);
            userImageString = Uri.parse(file.getUrl()).toString();
        }
        else {
            username = "Not signed in";
            email = "Please click here to sign in";
            userImageString = null;
        }

        setHeaderItems(username, email, userImageString);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mCircularProgressView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);

                switch (item.getItemId()) {
                    case R.id.popular:
                        doPopularOfferQuery();
                        mToolbar.setTitle("Popular");
                        mDrawer.closeDrawers();
                        break;

                    case R.id.trending:
                        doTrendingOfferQuery();
                        mToolbar.setTitle("Trending");
                        mDrawer.closeDrawers();
                        break;

                    case R.id.newest:
                        doNewestOfferQuery();
                        mToolbar.setTitle("Newest");
                        mDrawer.closeDrawers();
                        break;

                    case R.id.likes:
                        if (ParseUser.getCurrentUser() == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("Not signed in!");
                            builder.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    MainActivity.this.startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("CANCEL", null);
                            builder.setMessage("Please sign in to access your liked offers.");
                            builder.show();
                            return false;
                        }
                        doLikeQuery(1);
                        item.setChecked(true);
                        mDrawer.closeDrawers();
                        mToolbar.setTitle("Likes");
                        break;

                    case R.id.following:
                        if (ParseUser.getCurrentUser() == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("Not signed in!");
                            builder.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    MainActivity.this.startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("CANCEL", null);
                            builder.setMessage("Please sign in to access the offers of stores you follow.");
                            builder.show();
                            item.setChecked(false);
                            return false;
                        }
                        doFollowingOfferQuery();
                        mDrawer.closeDrawers();
                        mToolbar.setTitle("Following");
                        break;
                }
                return true;
            }
        });
    }

    private void setHeaderItems(String username, String email, String userImageString) {
        View headerView = mNavigationView.getHeaderView(0);

        TextView usernameTextView = (TextView) headerView.findViewById(R.id.usernameTextView);
        TextView emailTextView = (TextView) headerView.findViewById(R.id.emailTextView);
        CircleImageView profileImage = (CircleImageView) headerView.findViewById(R.id.profileImage);

        usernameTextView.setText(username);
        emailTextView.setText(email);
        Picasso.with(this).load(userImageString).into(profileImage);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ParseUser.getCurrentUser() == null) {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    v.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        OffApplication.getConfigHelper().fetchConfigIfNeeded();

        doLikeQuery(2);
    }

    private void doLikeQuery(final int senderId) {
        ParseQuery<Like> query = Like.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.orderByAscending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> list, ParseException e) {
                if (e == null) {
                    mLikes = list;
                    if (senderId == 0) {
                        doPopularOfferQuery();
                    } else if (senderId == 1) {
                        for (Like like : list) {
                            mLikedOffers.add(like.getOffer());
                        }
                        setGridItems(mLikedOffers);
                    }
                }
            }
        });
    }

    private void doPopularOfferQuery(){
        ParseQuery<Offer> query = Offer.getQuery();
        query.whereEqualTo(ParseConstants.KEY_POPULARITY, 2);
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> objects, ParseException e) {
                if (e == null) {
                    setGridItems(objects);
                }
            }
        });
    }

    private void doTrendingOfferQuery() {
        ParseQuery<Offer> query = Offer.getQuery();
        query.whereEqualTo(ParseConstants.KEY_POPULARITY, 1);
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> objects, ParseException e) {
                if (e == null) {
                    setGridItems(objects);
                }
            }
        });
    }

    private void doNewestOfferQuery() {
        ParseQuery<Offer> query = Offer.getQuery();
        query.whereEqualTo(ParseConstants.KEY_POPULARITY, 0);
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> list, ParseException e) { //Get the offer list for the grid adapter
                if (e == null) {
                    setGridItems(list);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doFollowingOfferQuery() {
        final List<Store> stores = new ArrayList<>();

        ParseQuery<Follow> followingQuery = Follow.getQuery();
        followingQuery.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        followingQuery.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> objects, ParseException e) {
                if (e == null) {
                    for (Follow follow : objects) {
                        stores.add(follow.getStore());
                    }
                    doOfferCompoundQuery(stores);
                }
            }
        });
    }

    private void doOfferCompoundQuery(List<Store> stores) {
        List<ParseQuery<Offer>> queries = new ArrayList<>();

        for (Store store : stores){
            ParseQuery<Offer> query = Offer.getQuery();
            query.whereEqualTo(ParseConstants.KEY_STORE, store);
            queries.add(query);
        }

        ParseQuery<Offer> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByDescending(ParseConstants.KEY_CREATED_AT);
        mainQuery.findInBackground(new FindCallback<Offer>() {
            @Override
            public void done(List<Offer> objects, ParseException e) {
                if (e == null) {
                    setGridItems(objects);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_search){
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, null);
            startActivity(intent);
        }
        else if (id == R.id.action_map) {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        }
        else if (id == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setGridItems(List<Offer> offers) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(layoutManager);

        OfferAdapter offerAdapter = new OfferAdapter(MainActivity.this, offers, "", mLikes);
        mRecyclerView.setAdapter(offerAdapter);

        mCircularProgressView.setVisibility(View.GONE);

        mRecyclerView.setVisibility(View.VISIBLE);
    }
}