package com.offapps.off.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.offapps.off.Data.Like;
import com.offapps.off.Data.Offer;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.offapps.off.UI.MallActivity;
import com.offapps.off.UI.OfferActivity;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlosdiez on 9/27/15.
 *
 */
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder>{

    private Context mContext;
    private List<Offer> mOfferList = new ArrayList<>();
    private String mExtra;
    private List<Like> mLikes;
    private Like mLike;

    public OfferAdapter(Context context, List<Offer> offerList, String extra, List<Like> likes){
        mContext = context;
        mOfferList = offerList;
        mExtra = extra;
        mLikes = likes;
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (mExtra.equals("Top")){
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.top_offers_item, viewGroup, false);
        }
        else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.grid_item, viewGroup, false);
        }
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OfferViewHolder mainViewHolder, int position) {
        final Offer offer = mOfferList.get(position);

        searchForLike(offer);

        mainViewHolder.bindOffer(offer);
        mainViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OfferActivity.class);
                intent.putExtra(ParseConstants.KEY_OBJECT_ID, offer.getObjectId());
                intent.putExtra("extra", mExtra);
                v.getContext().startActivity(intent);
            }
        });

        mainViewHolder.mHeartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForLike(offer);

                int likeCount = offer.getLikeCount();

                mainViewHolder.mHeartImageView.setEnabled(false);
                if (mLike != null) {
                    //It is liked, unlike it
                    mainViewHolder.mHeartImageView.setImageResource(R.drawable.ic_heart_border);

                    likeCount -= 1;
                    offer.setLikeCount(likeCount);

                    mLike.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                offer.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null)
                                            mainViewHolder.mHeartImageView.setEnabled(true);
                                    }
                                });
                        }
                    });
                    mLikes.remove(mLike);
                    mLike = null;
                    notifyDataSetChanged();
                }
                else {
                    //It is not liked, like it
                    mainViewHolder.mHeartImageView.setImageResource(R.drawable.ic_heart_full);
                    mLike = new Like();
                    mLike.setOffer(offer);
                    mLike.setUser(ParseUser.getCurrentUser());

                    likeCount += 1;
                    offer.setLikeCount(likeCount);

                    mLike.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                offer.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null)
                                            mainViewHolder.mHeartImageView.setEnabled(true);
                                    }
                                });
                        }
                    });
                    mLikes.add(mLike);
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void searchForLike(Offer offer) {
        Boolean isLiked = false;

        for (Like like : mLikes) {
            if (like.getOffer() == offer) {
                mLike = like;
                isLiked = true;
            }
        }
        if (!isLiked)
            mLike = null;
    }

    @Override
    public int getItemCount() {
        return mOfferList.size();
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        ImageView mDynamicHeightImageView;
        TextView mStoreNameTextView;
        TextView mPercentageTextView;
        TextView mOfferTitleTextView;
        ImageView mHeartImageView;

        public OfferViewHolder(View itemView) {
            super(itemView);

            mDynamicHeightImageView = (ImageView) itemView.findViewById(R.id.dynamicImageView);
            mOfferTitleTextView = (TextView) itemView.findViewById(R.id.offerTitleTextView);
            mStoreNameTextView = (TextView) itemView.findViewById(R.id.storeNameTextView);
            mPercentageTextView = (TextView) itemView.findViewById(R.id.percentageTextView);
            mHeartImageView = (ImageView) itemView.findViewById(R.id.heartImageView);
        }

        public void bindOffer(final Offer offer) {
            try {
                ParseObject store = offer.getParseObject(ParseConstants.KEY_STORE).fetchIfNeeded();
                mStoreNameTextView.setText(store.getString(ParseConstants.KEY_NAME));
            } catch (ParseException e) {
                //Alert dialogue
            }

            Picasso.with(mContext).load(offer.getImageString()).into(mDynamicHeightImageView);

            mOfferTitleTextView.setText(offer.getName());

            int toPercentage = offer.getToPercentage();
            int fromPercentage = offer.getFromPercentage();
            String percentageString;
            if (toPercentage == 0) {
                percentageString = fromPercentage + "%" + " Off";
            }
            else if (fromPercentage == 0) {
                percentageString = "Up to " + toPercentage + "%" + "Off";
            }
            else {
                percentageString = fromPercentage + "-" + toPercentage + "%" + " Off";
            }

            mPercentageTextView.setText(percentageString);

            if (mLike != null)
                mHeartImageView.setImageResource(R.drawable.ic_heart_full);
            else
                mHeartImageView.setImageResource(R.drawable.ic_heart_border);
        }
    }
}
