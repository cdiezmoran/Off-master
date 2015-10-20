package com.offapps.off.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.offapps.off.Data.Comment;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Carlos Diez on 08/10/2015.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    public CommentsAdapter(Context context, List<Comment> comments){
        mContext = context;
        mComments = comments;
    }

    @Override
    public CommentsAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_recycler_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.CommentViewHolder holder, int position) {
        holder.bindComment(mComments.get(position));
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        CircularImageView mUserProfileImage;
        TextView mUsernameTextView;
        TextView mCommentTextTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            mUserProfileImage = (CircularImageView) itemView.findViewById(R.id.userProfileImage);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
            mCommentTextTextView = (TextView) itemView.findViewById(R.id.commentTextTextView);
        }

        public void bindComment(Comment comment) {
            ParseUser user = comment.getParseUser();

            ParseFile userFile = user.getParseFile(ParseConstants.KEY_IMAGE);
            Uri userFileUri = Uri.parse(userFile.getUrl());
            Picasso.with(mContext).load(userFileUri.toString()).into(mUserProfileImage);

            mUsernameTextView.setText(user.getUsername());
            mCommentTextTextView.setText(comment.getText());
        }
    }
}
