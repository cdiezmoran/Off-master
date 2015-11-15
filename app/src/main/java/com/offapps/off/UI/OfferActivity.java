package com.offapps.off.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluejamesbond.text.DocumentView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.offapps.off.Adapters.CommentsListAdapter;
import com.offapps.off.Data.Comment;
import com.offapps.off.Data.Like;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ExpandableHeightListView;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OfferActivity extends AppCompatActivity {

    public static final String TAG = OfferActivity.class.getSimpleName();
    public static final int MAX_CHARACTER_COUNT = 420;
    public static Stack<Class<?>> parents = new Stack<>();

    @InjectView(R.id.tool_bar) android.support.v7.widget.Toolbar mToolbar;
    @InjectView(R.id.itemImageView) ImageView mItemImageView;
    @InjectView(R.id.descriptionDocumentView) DocumentView mDescriptionTextView;
    @InjectView(R.id.storeNameTextView) TextView mStoreNameTextView;
    @InjectView(R.id.storeImageView) CircularImageView mStoreImageView;
    @InjectView(R.id.offerTitleTextView) TextView mOfferTitleTextView;
    @InjectView(R.id.heartImageButton) ImageButton mHeartImageButton;
    @InjectView(R.id.commentEditText) EditText mCommentEditText;
    @InjectView(R.id.postCommentImageButton) ImageButton mPostCommentImageButton;
    @InjectView(R.id.characterCountTextView) TextView mCharacterCountTextView;
    @InjectView(R.id.list_view) ExpandableHeightListView mListView;
    @InjectView(R.id.startDateTextView) TextView mStartDateTextView;
    @InjectView(R.id.endDateTextView) TextView mEndDateTextView;
    @InjectView(R.id.likeCountTextView) TextView mLikeCountTextView;
    @InjectView(R.id.progress_view) CircularProgressView mCircularProgressView;
    @InjectView(R.id.scrollview) NestedScrollView mNestedScrollView;
    @InjectView(R.id.linearLayoutThird) LinearLayout mDatesLinearLayout;

    private Class<?> mParentClass;
    private String mMallObjectId;
    private Offer mOffer;
    private Like mLike;
    private ArrayList<String> mTags;

    private String mOfferId;
    private String mStoreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        ButterKnife.inject(this);
        StoreActivity.parents.add(getClass());

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mCircularProgressView.startAnimation();

        Intent intent = getIntent();
        mOfferId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);

        if (!parents.empty()) {
            mParentClass = parents.pop();
            if (mParentClass == OffersActivity.class) {
                mMallObjectId = intent.getStringExtra("extra");
            }
            else if (mParentClass == SearchResultsActivity.class || mParentClass == SearchActivity.class) {
                mTags = intent.getStringArrayListExtra(ParseConstants.KEY_TAGS);
            }
        }

        doOfferQuery();

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

        mPostCommentImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mOffer != null) {
                    comment(mOffer);
                    mCommentEditText.setText("");
                    mCommentEditText.clearFocus();
                } else {
                    Toast.makeText(OfferActivity.this, "Please try again when the offer loads.", Toast.LENGTH_LONG).show();
                }
            }
        });

        updatePostButtonState();
        updateCharacterCountTextViewText();

        mCommentEditText.setEnabled(true);
    }

    private void doOfferQuery() {
        ParseQuery<Offer> query = Offer.getQuery();
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, mOfferId);
        query.getFirstInBackground(new GetCallback<Offer>() {
            @Override
            public void done(Offer offer, ParseException e) {
                mOffer = offer;

                int viewCount = mOffer.getViewCount();
                mOffer.setViewCount(viewCount + 1);
                mOffer.saveInBackground();

                Picasso.with(OfferActivity.this).load(mOffer.getImageString()).into(mItemImageView);

                try {
                    Store store = mOffer.getParseObject(ParseConstants.KEY_STORE).fetchIfNeeded();
                    mStoreNameTextView.setText(store.getString(ParseConstants.KEY_NAME));
                    mStoreId = store.getObjectId();
                    Picasso.with(OfferActivity.this).load(store.getImageString()).into(mStoreImageView);
                } catch (ParseException e1) {
                    //Alert dialogue
                }

                mOfferTitleTextView.setText(mOffer.getName());
                mDescriptionTextView.setText(mOffer.getDescription());

                SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy \n k:m", Locale.US);

                String startDateText = "Starts: " + dateFormatter.format(mOffer.getFromDate());
                String endDateText = "Ends " + dateFormatter.format(mOffer.getToDate());

                mStartDateTextView.setText(startDateText);
                mEndDateTextView.setText(endDateText);

                doCommentsQuery();
                doLikesQuery();

                mCircularProgressView.resetAnimation();
                mCircularProgressView.setVisibility(View.GONE);

                mNestedScrollView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void doCommentsQuery(){
        ParseQuery<Comment> commentQuery = Comment.getQuery();
        commentQuery.include(ParseConstants.KEY_USER);
        commentQuery.setLimit(3);
        commentQuery.whereEqualTo(ParseConstants.KEY_OFFER, mOffer);

        commentQuery.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> list, ParseException e) {
                CommentsListAdapter adapter = new CommentsListAdapter(OfferActivity.this, list);
                mListView.setAdapter(adapter);
                mListView.setExpanded(true);
            }
        });
    }

    private void doLikesQuery(){
        ParseQuery<Like> query = Like.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OFFER, mOffer);
        query.whereEqualTo(ParseConstants.KEY_USER, ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<Like>() {
            @Override
            public void done(Like like, ParseException e) {
                mLike = like;

                final int likeCount = mOffer.getLikeCount();
                setLikeCountTextView(likeCount);

                if (mLike != null)
                    mHeartImageButton.setImageResource(R.drawable.ic_heart_full);
                else
                    mHeartImageButton.setImageResource(R.drawable.ic_heart_border);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mParentClass == OffersActivity.class) {
                Intent parentActivityIntent = new Intent(this, mParentClass);
                parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                parentActivityIntent.putExtra(ParseConstants.KEY_OBJECT_ID, mMallObjectId);
                startActivity(parentActivityIntent);
            }
            else if (mParentClass == SearchResultsActivity.class) {
                Intent intent =  new Intent(this, mParentClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("extra", "offers");
                intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, mTags);
                startActivity(intent);
            }
            else if (mParentClass == SearchActivity.class) {
                Intent intent = new Intent(this, mParentClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putStringArrayListExtra(ParseConstants.KEY_TAGS, mTags);
                startActivity(intent);
            }
            else{
                NavUtils.navigateUpFromSameTask(this);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void comment(Offer offer){
        String text = mCommentEditText.getText().toString().trim();

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.progress_comment));
        dialog.show();

        Comment comment = new Comment();

        comment.setText(text);
        comment.setUser(ParseUser.getCurrentUser());
        comment.setOffer(offer);

        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                doCommentsQuery();
            }
        });
    }

    private String getCommentEditTextText () {
        return mCommentEditText.getText().toString().trim();
    }

    private void updatePostButtonState () {
        int length = getCommentEditTextText().length();
        boolean enabled = length > 0 && length < MAX_CHARACTER_COUNT;
        mPostCommentImageButton.setEnabled(enabled);
    }

    private void updateCharacterCountTextViewText () {
        String characterCountString = String.format("%d/%d", mCommentEditText.length(), MAX_CHARACTER_COUNT);
        mCharacterCountTextView.setText(characterCountString);
    }

    @OnClick(R.id.showMoreButton)
    public void onClickShowMore(){
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtra(ParseConstants.KEY_OBJECT_ID, mOffer.getObjectId());
        startActivity(intent);
    }

    @OnClick(R.id.heartImageButton)
    public void onClickHeart() {
        int likeCount = mOffer.getLikeCount();
        mHeartImageButton.setEnabled(false);
        if (mLike != null) {
            mHeartImageButton.setImageResource(R.drawable.ic_heart_border);

            likeCount -= 1;
            mOffer.setLikeCount(likeCount);

            setLikeCountTextView(likeCount);

            mLike.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                        mOffer.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    mHeartImageButton.setEnabled(true);
                            }
                        });
                }
            });
            mLike = null;

        }
        else {
            mHeartImageButton.setImageResource(R.drawable.ic_heart_full);

            mLike = new Like();
            mLike.setOffer(mOffer);
            mLike.setUser(ParseUser.getCurrentUser());

            likeCount += 1;
            mOffer.setLikeCount(likeCount);

            setLikeCountTextView(likeCount);

            mLike.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                        mOffer.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    mHeartImageButton.setEnabled(true);
                            }
                        });
                }
            });
        }
    }

    private void setLikeCountTextView(int likeCount){
        String likeText = "Likes: " + likeCount;
        mLikeCountTextView.setText(likeText);
    }

    @OnClick(R.id.storeImageView)
    public void onClickStoreImageView() {
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra(ParseConstants.KEY_OBJECT_ID, mStoreId);
        intent.putExtra("offerId", mOfferId);
        startActivity(intent);
    }

    @OnClick(R.id.calendarImageButton)
    public void onClickCalendarImageButton() {
        if (mDatesLinearLayout.getVisibility() == View.GONE) {
            mDatesLinearLayout.setVisibility(View.VISIBLE);
        }
        else {
            mDatesLinearLayout.setVisibility(View.GONE);
        }
    }
}