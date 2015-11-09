package com.offapps.off.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.offapps.off.UI.StoreActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StoreSearchAdapter extends RecyclerView.Adapter<StoreSearchAdapter.StoreViewHolder> {

    private Context mContext;
    private List<Store> mStores;
    private List<String> mTags;

    public StoreSearchAdapter(Context context, List<Store> stores, List<String> tags) {
        mContext = context;
        mStores = stores;
        mTags = tags;
    }

    @Override
    public StoreSearchAdapter.StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_search_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreSearchAdapter.StoreViewHolder holder, int position) {
        final Store store = mStores.get(position);
        holder.bindStore(store);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StoreActivity.class);
                intent.putExtra(ParseConstants.KEY_OBJECT_ID, store.getObjectId());
                intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, new ArrayList<>(mTags));
                v.getContext().startActivity(intent);
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
        TextView mCategoryTextView;

        public StoreViewHolder(View itemView) {
            super(itemView);

            mStoreImageView = (CircularImageView) itemView.findViewById(R.id.storeImageView);
            mStoreNameTextView = (TextView) itemView.findViewById(R.id.storeNameTextView);
            mCategoryTextView = (TextView) itemView.findViewById(R.id.categoryTextView);
        }

        public void bindStore(Store store) {
            mStoreNameTextView.setText(store.getName());
            mCategoryTextView.setText(store.getCategory());

            Picasso.with(mContext).load(store.getImageString()).into(mStoreImageView);
        }
    }
}
