package com.offapps.off.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.offapps.off.Data.Offer;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OfferSearchAdapter extends RecyclerView.Adapter<OfferSearchAdapter.OfferViewHolder> {

    private Context mContext;
    private List<Offer> mOffers;

    public OfferSearchAdapter(Context context, List<Offer> offers){
        mContext = context;
        mOffers = offers;
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.offer_search_item, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, int position) {
        Offer offer = mOffers.get(position);
        holder.bindOffer(offer);
    }

    @Override
    public int getItemCount() {
        return mOffers.size();
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {

        ImageView mOfferImageView;
        TextView mOfferTitleTextView;
        TextView mStoreNameTextView;

        public OfferViewHolder(View itemView) {
            super(itemView);

            mOfferImageView = (ImageView) itemView.findViewById(R.id.offerImageView);
            mOfferTitleTextView = (TextView) itemView.findViewById(R.id.offerTitleTextView);
            mStoreNameTextView = (TextView) itemView.findViewById(R.id.storeNameTextView);
        }

        public void bindOffer(Offer offer){
            try {
                ParseObject store = offer.getParseObject(ParseConstants.KEY_STORE).fetchIfNeeded();
                mStoreNameTextView.setText(store.getString(ParseConstants.KEY_NAME));
            } catch (ParseException e) {
                //Alert dialogue
            }

            mOfferTitleTextView.setText(offer.getName());
            Picasso.with(mContext).load(offer.getImageString()).into(mOfferImageView);
        }
    }
}
