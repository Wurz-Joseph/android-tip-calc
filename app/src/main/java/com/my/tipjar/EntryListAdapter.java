package com.my.tipjar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EntryListAdapter extends ArrayAdapter<Entry> {
    private static final String TAG = "EntryListAdapter";
    private Context mContext;
    int mResource;



    public EntryListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Entry> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String date = getItem(position).getDate();
        String total = getItem(position).getTotal();
        Button button = getItem(position).getButton();
        Entry entry = new Entry(button, date, total);


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvDate = convertView.findViewById(R.id.text1);
        TextView tvTotal = convertView.findViewById(R.id.totalView);

        tvDate.setText(date);
        tvTotal.setText(total);

        return convertView;


    }

}
