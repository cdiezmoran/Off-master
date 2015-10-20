package com.offapps.off.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.offapps.off.Adapters.CommentsAdapter;
import com.offapps.off.Data.Comment;
import com.offapps.off.Data.Offer;
import com.offapps.off.Misc.DividerItemDecoration;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentsActivity extends AppCompatActivity {

    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.tool_bar) Toolbar mToolbar;
    @InjectView(R.id.commentEditText) EditText mCommentEditText;
    @InjectView(R.id.characterCountTextView) TextView mCharacterCountTextView;
    @InjectView(R.id.FAB) FloatingActionButton mFloatingActionButton;

    private Offer mOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.inject(this);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        Intent intent = getIntent();
        String offerId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);

        mCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePostButtonState();
                updateCharacterCountTextViewText();
            }
        });

        updatePostButtonState();
        updateCharacterCountTextViewText();

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionButton.setEnabled(false);
                comment();
            }
        });
        doOfferQuery(offerId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doOfferQuery(String offerId) {
        ParseQuery<Offer> query = Offer.getQuery();
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, offerId);
        query.getFirstInBackground(new GetCallback<Offer>() {
            @Override
            public void done(Offer offer, ParseException e) {
                mOffer = offer;
                doCommentsQuery();
            }
        });
    }

    private void doCommentsQuery(){
        ParseQuery<Comment> commentQuery = Comment.getQuery();
        commentQuery.include(ParseConstants.KEY_USER);
        commentQuery.whereEqualTo(ParseConstants.KEY_OFFER, mOffer);
        commentQuery.orderByDescending(ParseConstants.KEY_CREATED_AT);

        commentQuery.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> list, ParseException e) {
                CommentsAdapter adapter = new CommentsAdapter(CommentsActivity.this, list);
                mRecyclerView.setAdapter(adapter);
            }
        });
    }

    private void comment(){
        String text = mCommentEditText.getText().toString().trim();

        Comment comment = new Comment();

        comment.setText(text);
        comment.setUser(ParseUser.getCurrentUser());
        comment.setOffer(mOffer);

        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mFloatingActionButton.setEnabled(true);
                    doCommentsQuery();
                    mCommentEditText.setText("");
                }
            }
        });
    }

    private String getCommentEditTextText () {
        return mCommentEditText.getText().toString().trim();
    }

    private void updatePostButtonState () {
        int length = getCommentEditTextText().length();
        boolean enabled = length > 0 && length < OfferActivity.MAX_CHARACTER_COUNT;
        if (enabled){
            mCharacterCountTextView.setVisibility(View.VISIBLE);
            mFloatingActionButton.setEnabled(true);
        } else {
            mCharacterCountTextView.setVisibility(View.GONE);
            mFloatingActionButton.setEnabled(false);
        }
    }

    private void updateCharacterCountTextViewText () {
        String characterCountString = String.format("%d/%d", mCommentEditText.length(), OfferActivity.MAX_CHARACTER_COUNT);
        mCharacterCountTextView.setText(characterCountString);
    }
}
