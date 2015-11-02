package com.offapps.off.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Store;
import com.offapps.off.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoreSearchAdapter extends RecyclerView.Adapter<StoreSearchAdapter.StoreViewHolder> {

    private Context mContext;
    private List<Store> mStores;

    public StoreSearchAdapter(Context context, List<Store> stores) {
        mContext = context;
        mStores = stores;
    }

    @Override
    public StoreSearchAdapter.StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_search_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreSearchAdapter.StoreViewHolder holder, int position) {
        Store store = mStores.get(position);
        holder.bindStore(store);
    }

    @Override
    public int getItemCount() {
        return mStores.size();
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mStoreImageView;
        TextView mStoreNameTextView;

        public StoreViewHolder(View itemView) {
            super(itemView);

            mStoreImageView = (CircularImageView) itemView.findViewById(R.id.storeImageView);
            mStoreNameTextView = (TextView) itemView.findViewById(R.id.storeNameTextView);
        }

        public void bindStore(Store store) {
            mStoreNameTextView.setText(store.getName());
            Picasso.with(mContext).load(store.getImageString()).into(mStoreImageView);
        }
    }
}
