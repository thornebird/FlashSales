package com.flashsales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.flashsales.R;
public class AdapterSpinner extends BaseAdapter {

    private Context context;
    ArrayList<String> values;
    LayoutInflater inflater;

    public AdapterSpinner(Context context,ArrayList<String>values){
        this.context = context;
        this.values = values;
        inflater = (LayoutInflater.from(context));
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return values.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view  = inflater.inflate(R.layout.spinner_row, null);

        TextView tvValue = (TextView)view.findViewById(R.id.tv_value);
        tvValue.setText(values.get(i));
        return view;
    }
}
