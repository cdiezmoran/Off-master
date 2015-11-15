package com.offapps.off.Adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Mall;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Carlos Diez on 09/11/2015.
 *
 */
public class MallListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Mall> mMalls;
    private Location mLocation;

    public MallListAdapter(Context context, List<Mall> malls, Location location) {
        mContext = context;
        mMalls = malls;
        mLocation = location;
    }

    @Override
    public int getCount() {
        return mMalls.size();
    }

    @Override
    public Object getItem(int position) {
        return mMalls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.mall_map_item, null);

            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.distanceTextView = (TextView) convertView.findViewById(R.id.distanceTextView);
            holder.circleImageView = (CircularImageView) convertView.findViewById(R.id.circleImageView);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Mall mall = mMalls.get(position);

        holder.nameTextView.setText(mall.getName());

        Picasso.with(mContext).load(mall.getImageUri().toString()).into(holder.circleImageView);

        Location mallLocation = new Location("");
        mallLocation.setLatitude(mall.getParseGeoPoint(ParseConstants.KEY_LOCATION).getLatitude());
        mallLocation.setLongitude(mall.getParseGeoPoint(ParseConstants.KEY_LOCATION).getLongitude());

        int distanceInKm = Math.round(mLocation.distanceTo(mallLocation)/1000);

        String locationString =  distanceInKm + " KM from your location.";
        holder.distanceTextView.setText(locationString);

        return convertView;
    }

    private static class ViewHolder {
        TextView nameTextView;
        TextView distanceTextView;
        CircularImageView circleImageView;
    }
}
