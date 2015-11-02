package com.offapps.off.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Mall;
import com.offapps.off.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MallSearchAdapter extends RecyclerView.Adapter<MallSearchAdapter.MallViewHolder> {

    private Context mContext;
    private List<Mall> mMalls;

    public MallSearchAdapter(Context context, List<Mall> malls) {
        mContext = context;
        mMalls = malls;
    }

    @Override
    public MallSearchAdapter.MallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mall_search_item, parent, false);
        return new MallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MallSearchAdapter.MallViewHolder holder, int position) {
        Mall mall = mMalls.get(position);
        holder.bindMall(mall);
    }

    @Override
    public int getItemCount() {
        return mMalls.size();
    }

    public class MallViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mMallImageView;
        TextView mMallNameTextView;
        TextView mDistanceInKMTextView;

        public MallViewHolder(View itemView) {
            super(itemView);

            mMallNameTextView = (TextView) itemView.findViewById(R.id.mallNameTextView);
            mDistanceInKMTextView = (TextView) itemView.findViewById(R.id.distanceInKMTextView);
            mMallImageView = (CircularImageView) itemView.findViewById(R.id.mallImageView);
        }

        public void bindMall(Mall mall){
            mMallNameTextView.setText(mall.getName());
            Picasso.with(mContext).load(mall.getImageUri().toString()).into(mMallImageView);
            mDistanceInKMTextView.setText("Distance unavailable");
        }
    }
}
