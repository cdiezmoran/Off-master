package com.offapps.off.UI;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.offapps.off.Adapters.StoreRecyclerGridAdapter;
import com.offapps.off.Data.Floor;
import com.offapps.off.Data.Mall_Store;
import com.offapps.off.Data.Store;
import com.offapps.off.Interfaces.MyButtonClickListener;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlosdiez on 9/24/15.
 *
 */
public class TabMallFragment extends Fragment implements MyButtonClickListener {

    private String mFloorId;
    private String mImageString;
    private String mMallId;
    private List<Store> mStores;
    private List<String> mImageStringList;

    private RecyclerView mRecyclerView;
    private ImageView mMapImageView;

    private MyButtonClickListener mMyButtonClickListener;
    private StoreRecyclerGridAdapter mAdapter;

    private CircularProgressView mCircularProgressView;

    private Boolean mIsFirstTime;

    public static TabMallFragment newInstance(String floorId, String imageString, String mallId){
        TabMallFragment fragment = new TabMallFragment();

        Bundle args = new Bundle(3);
        args.putString(ParseConstants.KEY_OBJECT_ID, floorId);
        args.putString("imageString", imageString);
        args.putString("mallId", mallId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFloorId = getArguments().getString(ParseConstants.KEY_OBJECT_ID);
        mImageString = getArguments().getString("imageString");
        mMallId = getArguments().getString("mallId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_mall_fragment, container, false);

        mMapImageView = (ImageView) view.findViewById(R.id.mapImageView);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.storeGridView);

        mCircularProgressView = (CircularProgressView) view.findViewById(R.id.progress_view);

        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);

        Picasso.with(getActivity()).load(mImageString).into(mMapImageView);

        mStores = new ArrayList<>();
        mImageStringList = new ArrayList<>();

        mIsFirstTime = true;
        buttonInRecyclerClicked(0);

        doFloorQuery();

        return view;
    }

    public void doFloorQuery(){
        ParseQuery<Floor> query = Floor.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mFloorId);
        query.getFirstInBackground(new GetCallback<Floor>() {
            @Override
            public void done(Floor floor, ParseException e) {
                doMallStoresQuery(floor);
            }
        });
    }

    private void doMallStoresQuery(Floor floor) {
        ParseQuery<Mall_Store> query = Mall_Store.getQuery();
        query.whereEqualTo(ParseConstants.KEY_FLOOR, floor);
        query.findInBackground(new FindCallback<Mall_Store>() {
            @Override
            public void done(List<Mall_Store> list, ParseException e) {
                if (e == null) {
                    for (Mall_Store object : list) {
                        if (object != null) {
                            mImageStringList.add(object.getImageString());
                            try {
                                mStores.add((Store) object.getStore().fetchIfNeeded());
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    mAdapter = new StoreRecyclerGridAdapter(getActivity(), mStores, mMyButtonClickListener, mMallId);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    @Override
    public void buttonInRecyclerClicked(int position) {
        if (!mIsFirstTime) {
            mCircularProgressView.setVisibility(View.VISIBLE);
            mCircularProgressView.startAnimation();
            mMapImageView.setVisibility(View.GONE);
            Picasso.with(getActivity()).load(mImageStringList.get(position)).into(mMapImageView, new Callback() {
                @Override
                public void onSuccess() {
                    mCircularProgressView.setVisibility(View.GONE);
                    mMapImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    //show error dialog
                }
            });
        }
        else {
            mMyButtonClickListener = this;
            mIsFirstTime = false;
        }
    }
}