package com.my.tipjar;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Data extends AppCompatActivity {
    DatabaseHelper database = new DatabaseHelper(this);
    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<Entry> entryList = new ArrayList<>();
    ArrayList<Long> entryListID = new ArrayList<>();
    ArrayList<Double> storeTotals = new ArrayList<>();
    ListView listView;
    ArrayAdapter<Entry> adapter;
    TextView totalValueTextView;
    TextView totalView;
    Button deleteBtn;
    View item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data);

        listView = findViewById(R.id.ListView);
        totalValueTextView = findViewById(R.id.totalValue);
        totalView = findViewById(R.id.totalView);
        Cursor allData = database.getAllData();
        Cursor dateQuery = database.dateQuery();
        Cursor getTotal = database.getTotal();
        Cursor getId = database.getId();
        Cursor sumTotal = database.totalSum();
        deleteBtn = findViewById(R.id.custom_button);

        //Change the action bar back arrow to black
        final Drawable backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        backArrow.setColorFilter(getResources().getColor(R.color.color_black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);


        //formats decimals 0.00
        final DecimalFormat df = new DecimalFormat("#0.00");


        if (allData.getCount() == 0) {
            Toast.makeText(Data.this, "Nothing to show yet.", Toast.LENGTH_SHORT).show();
        } else {
            while (dateQuery.moveToNext()) {
                dateList.add(dateQuery.getString(0));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor rowData = database.getRow(position);
                        StringBuilder builder = new StringBuilder();
                        while (rowData.moveToNext()) {
                            builder.append("Check Amount: $" + df.format(rowData.getDouble(0)) + "\n");
                            builder.append("Tip: $" + df.format(rowData.getDouble(1)) + "\n");
                            builder.append("Total: $" + df.format(rowData.getDouble(2)));
                        }
                        showMessage("Details", builder.toString());

                    }
                });
            }
            while (sumTotal.moveToNext()) {
                totalValueTextView.setText("$" + df.format(sumTotal.getDouble(0)));
            }


            SimpleDateFormat tempDateFormat = new SimpleDateFormat("M-d-y h:m", Locale.US);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
            for (String date : dateList) {
                try {
                    if (getTotal.moveToNext()) {
                        Date tempDate = tempDateFormat.parse(date);

                        Entry entry = new Entry(deleteBtn, dateFormat.format(tempDate), "$" + df.format(getTotal.getDouble(0)));

                        entryList.add(entry);
                        storeTotals.add(getTotal.getDouble(0));

                        for (getId.moveToFirst(); !getId.isAfterLast(); getId.moveToNext()) {
                            // The Cursor is now set to the right position
                            entryListID.add(getId.getLong(0));
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    item = listView.getChildAt(position);
                    deleteBtn = item.findViewById(R.id.custom_button);
                    if (deleteBtn.getVisibility() == View.GONE) {
                        deleteBtn.setVisibility(View.VISIBLE);
                    } else {
                        deleteBtn.setVisibility(View.GONE);
                    }


                    Button deleteButton = view.findViewById(R.id.custom_button);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            entryList.remove(position);
                            storeTotals.remove(position);
                            database.deleteRow(entryListID.get(position));
                            adapter.notifyDataSetChanged();

                            double newTotal = 0.0;
                            for (Double total : storeTotals) {
                                newTotal += total;
                            }
                            totalValueTextView.setText("$" + df.format(newTotal));


                        }

                    });

                    return true;
                }
            });
            adapter = new EntryListAdapter(this, R.layout.listview_layout, entryList);
            listView.setAdapter(adapter);
        }


    }


    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

}