package com.offapps.off.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.offapps.off.Data.Mall;
import com.offapps.off.Data.Offer;
import com.offapps.off.Data.Store;
import com.offapps.off.Misc.ParseConstants;
import com.offapps.off.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddOfferActivity extends AppCompatActivity{

    @InjectView(R.id.mallEditText) EditText mMallEditText;
    @InjectView(R.id.storeEditText) EditText mStoreEditText;
    @InjectView(R.id.mainPercentageEditText) EditText mMainPercentageEditText;
    @InjectView(R.id.secondaryPercentageEditText) EditText mSecondaryPercentageEditText;
    @InjectView(R.id.descriptionEditText) EditText mDescriptionEditText;
    @InjectView(R.id.nameEditText) EditText mNameEditText;
    @InjectView(R.id.startDateEditText) EditText mFromDateEditText;
    @InjectView(R.id.endDateEditText) EditText mToDateEditText;
    @InjectView(R.id.startTimeEditText) EditText mStartTimeEditText;
    @InjectView(R.id.endTimeEditText) EditText mEndTimeEditText;
    @InjectView(R.id.tool_bar) Toolbar mToolbar;

    private Mall mMall;
    private Store mStore;
    private Offer mOffer;
    private SimpleDateFormat mDateFormatter;
    private SimpleDateFormat mDateTimeFormatter;
    private DatePickerDialog mFromDatePickerDialog;
    private DatePickerDialog mToDatePickerDialog;
    private TimePickerDialog mStartTimePickerDialog;
    private TimePickerDialog mEndTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        mDateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy k:m", Locale.US);

        mNameEditText.requestFocus();

        mFromDateEditText.setInputType(InputType.TYPE_NULL);
        mToDateEditText.setInputType(InputType.TYPE_NULL);
        mStartTimeEditText.setInputType(InputType.TYPE_NULL);
        mEndTimeEditText.setInputType(InputType.TYPE_NULL);

        setDateTimeField();
    }

    @OnClick(R.id.addButton)
    public void addOffer(View view) {
        String mallName = mMallEditText.getText().toString().trim();
        String storeName = mStoreEditText.getText().toString().trim();
        String name = mNameEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();
        String fromDateString = mFromDateEditText.getText().toString().trim();
        String toDateString = mToDateEditText.getText().toString().trim();
        String startTimeString = mStartTimeEditText.getText().toString().trim();
        String endTimeString = mEndTimeEditText.getText().toString().trim();
        int fromPercentage;
        int toPercentage;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setPositiveButton("OK", null);

        if (mMainPercentageEditText.getText().toString().isEmpty()){
            builder.setMessage("All fields must be filled except for the To Percentage field.");
            builder.show();
        }
        else if (mSecondaryPercentageEditText.getText().toString().isEmpty()){
            toPercentage = 0;
        }
        else {
            fromPercentage = Integer.parseInt(mMainPercentageEditText.getText().toString().trim());
            toPercentage = Integer.parseInt(mSecondaryPercentageEditText.getText().toString().trim());

            Date todayDate = new Date();

            try {
                builder.setTitle("Please check your data!");
                Date fromDateTime = mDateTimeFormatter.parse(fromDateString + " " + startTimeString);
                Date toDateTime = mDateTimeFormatter.parse(toDateString + " " + endTimeString);

                if (fromDateTime.after(toDateTime) || todayDate.after(toDateTime)) {
                    builder.setMessage("The end date can't be set before the start date or before today!");
                    builder.show();
                } else if (mallName.isEmpty() || storeName.isEmpty() || name.isEmpty() || description.isEmpty()
                        || fromDateString.isEmpty() || toDateString.isEmpty() || startTimeString.isEmpty()
                        || endTimeString.isEmpty()) {

                    builder.setMessage("All fields must be filled except for the To Percentage field.");
                    builder.show();
                } else if (fromPercentage > 100 || fromPercentage < 0 || toPercentage > 100 || toPercentage < 1) {
                    builder.setMessage("Invalid value for one or both percentage fields!");
                    builder.show();
                } else {
                    mOffer = new Offer();
                    mOffer.setName(name);
                    mOffer.setDescription(description);

                    if (fromPercentage < toPercentage) {
                        mOffer.setFromPercentage(fromPercentage);
                        mOffer.setToPercentage(toPercentage);
                    } else {
                        mOffer.setFromPercentage(toPercentage);
                        mOffer.setToPercentage(fromPercentage);
                    }

                    mOffer.setFromDate(fromDateTime);
                    mOffer.setToDate(toDateTime);

                    doStoreQuery(mallName, storeName);

                    ParseObject tags = new ParseObject(ParseConstants.CLASS_TAGS);
                    tags.put(ParseConstants.KEY_OFFER, mOffer);
                    ArrayList<String> categoriesList = new ArrayList<>();
                    categoriesList.add(modifyStringForCategory(mallName));
                    categoriesList.add(modifyStringForCategory(storeName));
                    tags.put(ParseConstants.KEY_TAGS, categoriesList);
                    tags.saveInBackground();

                    clearFields();
                }
            } catch (java.text.ParseException e) {
                builder.setTitle("Something went wrong!");
                builder.setMessage("Error retrieving date and time! Please try again later or contact us if the error persists.");
                builder.show();
            }
        }
    }

    private void clearFields() {
        mMallEditText.setText("");
        mStoreEditText.setText("");
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mMainPercentageEditText.setText("");
        mSecondaryPercentageEditText.setText("");
    }

    private void doStoreQuery(final String mallName, final String storeName) {
        ParseQuery<Store> storeQuery = Store.getQuery();
        storeQuery.whereEqualTo(ParseConstants.KEY_NAME, storeName);
        storeQuery.getFirstInBackground(new GetCallback<Store>() {
            @Override
            public void done(Store store, ParseException e) {
                if (store != null) {
                    mStore = store;
                } else {
                    mStore = new Store();
                    mStore.setName(storeName);
                    mStore.saveInBackground();
                }
                mOffer.setStore(mStore);
                mOffer.saveInBackground();
                doMallQuery(mallName);
            }
        });
    }

    private void doMallQuery(final String mallName) {
        ParseQuery<Mall> mallQuery = Mall.getQuery();
        mallQuery.whereEqualTo(ParseConstants.KEY_NAME, mallName);
        mallQuery.getFirstInBackground(new GetCallback<Mall>() {
            @Override
            public void done(Mall mall, ParseException e) {
                if (mall != null) {
                    mMall = mall;
                } else {
                    mMall = new Mall();
                    mMall.setName(mallName);
                    mMall.saveInBackground();
                }

                ParseObject tableMallOffers = new ParseObject(ParseConstants.TABLE_MALL_OFFER);
                tableMallOffers.put(ParseConstants.KEY_MALL, mMall);
                tableMallOffers.put(ParseConstants.KEY_OFFER, mOffer);
                tableMallOffers.saveInBackground();

                ParseObject tableMallStores = new ParseObject(ParseConstants.TABLE_MALL_STORES);
                tableMallStores.put(ParseConstants.KEY_MALL, mMall);
                tableMallStores.put(ParseConstants.KEY_STORE, mStore);
                tableMallStores.saveInBackground();
            }
        });
    }

    private String modifyStringForCategory(String string){
        String modString = string.trim().toLowerCase().replaceAll("\\s+"," ");
        return modString;
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        mFromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mFromDateEditText.setText(mDateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mFromDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mFromDatePickerDialog.show();
                v.clearFocus();
            }
        });

        mToDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mToDateEditText.setText(mDateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mToDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mToDatePickerDialog.show();
                v.clearFocus();
            }
        });
        mStartTimePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                mStartTimeEditText.setText(hourString + ":" + minuteString);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        mStartTimeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mStartTimePickerDialog.show(getFragmentManager(), "StartTimePickerDialog");
                v.clearFocus();
            }
        });

        mEndTimePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                mEndTimeEditText.setText(hourString + ":" + minuteString);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        mEndTimeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mEndTimePickerDialog.show(getFragmentManager(), "StartTimePickerDialog");
                v.clearFocus();
            }
        });
    }
}