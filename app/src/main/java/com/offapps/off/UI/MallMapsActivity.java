package com.offapps.off.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.offapps.off.Adapters.ViewPagerAdapter;
import com.offapps.off.Data.Floor;
import com.offapps.off.Data.Mall;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MallMapsActivity extends AppCompatActivity {

    @InjectView(R.id.tool_bar) Toolbar mToolbar;
    @InjectView(R.id.tabLayout) TabLayout mTabLayout;
    @InjectView(R.id.viewPager) ViewPager mViewPager;

    private String mMallId;
    private int mFloors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_maps);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

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
                    mFloors = mall.getInt(ParseConstants.KEY_FLOORS);
                    doFloorQuery(mall);
                } else {
                    showQueryAlertDialog();
                }
            }
        });
    }

    private void doFloorQuery(final Mall mall) {
        ParseQuery<Floor> query = Floor.getQuery();
        query.whereEqualTo(ParseConstants.KEY_MALL, mall);
        query.orderByAscending(ParseConstants.KEY_FLOOR_NUMBER);
        query.findInBackground(new FindCallback<Floor>() {
            @Override
            public void done(List<Floor> floorList, ParseException e) {
                if (e == null) {
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                    Floor floor;
                    for (int i = 0; i < mFloors; i++) {
                        floor = floorList.get(i);
                        adapter.addFrag(TabMallFragment.newInstance(floor.getObjectId(), floor.getImageString())
                                , floor.getName());
                    }
                    mViewPager.setAdapter(adapter);
                    mTabLayout.setupWithViewPager(mViewPager);
                }
                 else
                    showQueryAlertDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showQueryAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MallMapsActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Something went wrong!");
        builder.setPositiveButton("ACCEPT", null);
        builder.setMessage("There was an error retrieving the data, please try again later.");
        builder.show();
    }
}
