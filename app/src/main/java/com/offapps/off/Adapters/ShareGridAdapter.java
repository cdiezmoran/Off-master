package com.offapps.off.Adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Carlos Diez
 * on 14/11/2015.
 */
public class ShareGridAdapter extends RecyclerView.Adapter<ShareGridAdapter.ShareViewHolder> {

    private Context mContext;
    private List<String> mNames;
    private List<Drawable> mImages;
    private String mOfferId;
    private String mStoreName;

    public ShareGridAdapter(Context context, List<String> names, List<Drawable> images, String offerId, String storeName) {
        mContext = context;
        mNames = names;
        mImages = images;
        mOfferId = offerId;
        mStoreName = storeName;
    }

    @Override
    public ShareGridAdapter.ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.share_grid_item, parent, false);
        return new ShareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShareGridAdapter.ShareViewHolder holder, int position) {
        final String name = mNames.get(position);
        holder.bindShare(name, mImages.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                Uri uri = Uri.parse("http://offapps.com/" + mOfferId);
                String message = "Don't miss this offer from " + mStoreName + "! Check it out now on Off: " + uri;
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                shareIntent.setType("text/plain");

                switch (name) {
                    case "Whatsapp": {
                        PackageManager pm = v.getContext().getPackageManager();
                        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                        for (final ResolveInfo app : activityList) {
                            if ((app.activityInfo.name).contains("com.whatsapp")) {
                                final ActivityInfo activity = app.activityInfo;
                                final ComponentName compName = new ComponentName(
                                        activity.applicationInfo.packageName, activity.name);
                                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                shareIntent.setComponent(compName);
                                v.getContext().startActivity(shareIntent);
                                break;
                            }
                        }
                        break;
                    }
                    case "Twitter":
                        PackageManager packManager = v.getContext().getPackageManager();
                        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);

                        boolean resolved = false;
                        for (ResolveInfo resolveInfo : resolvedInfoList) {
                            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                                shareIntent.setClassName(
                                        resolveInfo.activityInfo.packageName,
                                        resolveInfo.activityInfo.name);
                                resolved = true;
                                break;
                            }
                        }
                        if (resolved) {
                            v.getContext().startActivity(shareIntent);
                        } else {
                            String twitterUser = ParseUser.getCurrentUser().getString(ParseConstants.KEY_TWITTER_USER);
                            Intent twitterIntent = new Intent();
                            twitterIntent.putExtra(Intent.EXTRA_TEXT, message);
                            twitterIntent.setAction(Intent.ACTION_VIEW);
                            twitterIntent.setData(Uri.parse("https://twitter.com/intent/tweet?text=message&via=" + twitterUser));
                            v.getContext().startActivity(twitterIntent);
                            Toast.makeText(mContext, "Twitter app isn't found", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "Gmail": {
                        PackageManager pm = v.getContext().getPackageManager();
                        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
                        for (final ResolveInfo app : activityList) {
                            if ((app.activityInfo.name).contains("android.gm")) {
                                final ActivityInfo activity = app.activityInfo;
                                final ComponentName compName = new ComponentName(activity.applicationInfo.packageName, activity.name);
                                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                shareIntent.setComponent(compName);
                                v.getContext().startActivity(shareIntent);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ShareViewHolder extends RecyclerView.ViewHolder {

        TextView mShareTextView;

        public ShareViewHolder(View itemView) {
            super(itemView);

            mShareTextView = (TextView) itemView.findViewById(R.id.shareTextView);
        }

        public void bindShare(String name, Drawable drawable) {
            mShareTextView.setText(name);

            switch (name) {
                case "Facebook":
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.FacebookColor), PorterDuff.Mode.SRC_ATOP);
                    break;
                case "Twitter":
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.TwitterColor), PorterDuff.Mode.SRC_ATOP);
                    break;
                case "Whatsapp":
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.WhatsappColor), PorterDuff.Mode.SRC_ATOP);
                    break;
                case "Google Plus":
                case "Gmail":
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.GoogleplusColor), PorterDuff.Mode.SRC_ATOP);
                    break;
            }

            mShareTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }
}
