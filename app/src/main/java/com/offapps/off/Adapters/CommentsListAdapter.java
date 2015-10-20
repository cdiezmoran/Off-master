package com.offapps.off.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Comment;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.offapps.off.UI.OfferActivity;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by carlosdiez on 9/5/15.
 */
public class CommentsListAdapter extends BaseAdapter {

    private List<Comment> mComments;
    private Context mContext;

    public CommentsListAdapter(Context context, List<Comment> comments){
        mContext = context;
        mComments = comments;
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Object getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_listview_item, null);

            holder = new ViewHolder();
            holder.userProfileImage = (CircularImageView) convertView.findViewById(R.id.userProfileImage);
            holder.usernameTextView = (TextView) convertView.findViewById(R.id.usernameTextView);
            holder.commentTextTextView = (TextView) convertView.findViewById(R.id.commentTextTextView);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = mComments.get(position);

        ParseUser user = comment.getParseUser();

        ParseFile userFile = user.getParseFile(ParseConstants.KEY_IMAGE);
        Uri userFileUri = Uri.parse(userFile.getUrl());
        Picasso.with(mContext).load(userFileUri.toString()).into(holder.userProfileImage);

        holder.usernameTextView.setText(user.getUsername());
        holder.commentTextTextView.setText(comment.getText());

        return convertView;
    }

    private static class ViewHolder{
        CircularImageView userProfileImage;
        TextView usernameTextView;
        TextView commentTextTextView;
    }
}
