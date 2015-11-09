package com.offapps.off.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Mall;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.offapps.off.UI.MallActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MallSearchAdapter extends RecyclerView.Adapter<MallSearchAdapter.MallViewHolder> {

    private Context mContext;
    private List<Mall> mMalls;
    private List<String> mTags;

    public MallSearchAdapter(Context context, List<Mall> malls, List<String> tags) {
        mContext = context;
        mMalls = malls;
        mTags = tags;
    }

    @Override
    public MallSearchAdapter.MallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mall_search_item, parent, false);
        return new MallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MallSearchAdapter.MallViewHolder holder, int position) {
        final Mall mall = mMalls.get(position);
        holder.bindMall(mall);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MallActivity.class);
                intent.putExtra(ParseConstants.KEY_OBJECT_ID, mall.getObjectId());
                intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, new ArrayList<>(mTags));
                v.getContext().startActivity(intent);
            }
        });
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
