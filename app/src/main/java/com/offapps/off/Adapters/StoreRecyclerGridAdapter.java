package com.offapps.off.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.offapps.off.Interfaces.MyButtonClickListener;
import com.offapps.off.UI.StoreActivity;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by carlosdiez on 10/1/15.
 */
public class StoreRecyclerGridAdapter extends RecyclerView.Adapter<StoreRecyclerGridAdapter.StoreViewHolder> {

    private Context mContext;
    private List<Store> mStores;
    private MyButtonClickListener mItemListener;

    public StoreRecyclerGridAdapter(Context context, List<Store> stores, MyButtonClickListener itemListener) {
        mContext = context;
        mStores = stores;
        mItemListener = itemListener;
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_grid_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, final int position) {
        final Store store = mStores.get(position);
        holder.bindStore(store);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StoreActivity.class);
                intent.putExtra(ParseConstants.KEY_OBJECT_ID, store.getObjectId());
                v.getContext().startActivity(intent);
            }
        });

        holder.mMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.buttonInRecyclerClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStores.size();
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mStoreImageView;
        TextView mStoreNameTextView;
        Button mMarkerButton;

        public StoreViewHolder(View itemView) {
            super(itemView);

            mStoreImageView = (CircularImageView) itemView.findViewById(R.id.storeImageView);
            mStoreNameTextView = (TextView) itemView.findViewById(R.id.storeNameTextView);
            mMarkerButton = (Button) itemView.findViewById(R.id.markerButton);

            final Drawable markerDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_room_black_18dp);
            markerDrawable.setColorFilter(ContextCompat.getColor(mContext, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            mMarkerButton.setCompoundDrawablesWithIntrinsicBounds(markerDrawable, null, null, null);
        }

        public void bindStore(Store store) {
            mStoreNameTextView.setText(store.getName());
            Picasso.with(mContext).load(store.getImageString()).into(mStoreImageView);
        }
    }
}
