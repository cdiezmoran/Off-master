package com.offapps.off.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.offapps.off.R;

/**
 * Created by carlosdiez on 8/23/15.
 */
public class CategoriesListAdapter extends BaseAdapter{

    private String[] mCategoriesList;
    private Context mContext;

    public CategoriesListAdapter (Context context, String[] categoriesList){
        mContext = context;
        mCategoriesList = categoriesList;
    }

    @Override
    public int getCount() {
        return mCategoriesList.length;
    }

    @Override
    public Object getItem(int i) {
        return mCategoriesList[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.categories_list_item, null);

            holder = new ViewHolder();
            holder.categoryTextView = (TextView) view.findViewById(R.id.categoryTextView);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        holder.categoryTextView.setText(mCategoriesList[i]);

        return view;
    }

    private static class ViewHolder{
        private TextView categoryTextView;
    }
}