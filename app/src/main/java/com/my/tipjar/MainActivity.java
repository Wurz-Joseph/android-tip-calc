package com.my.tipjar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    TextView totalTextView;
    TextView tipAmountTextView;
    TextView setTextTip;
    TextView setTextTotal;
    EditText baseAmountEditText;
    EditText customAmountEditText;
    Button calcBtn;
    Button saveData;
    Button historyBtn;
    CheckBox roundUpCheck;
    CheckBox roundDownCheck;

    String storeBaseAmount;
    double tipAmount;
    double total;
    double remainder;


    RadioGroup radioGroup;
    int radioSelectedId;
    DatabaseHelper database;

    //database variables
    double floatBaseAmount;
    double floatTipAmount;
    double floatTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseHelper(this);
        tipAmountTextView = findViewById(R.id.tipAmountTextView);
        totalTextView = findViewById(R.id.totalTextView);
        setTextTip = findViewById(R.id.setTextTip);
        setTextTotal = findViewById(R.id.setTextTotal);
        baseAmountEditText = findViewById(R.id.baseAmountEditText);
        customAmountEditText = findViewById(R.id.customAmountEditText);
        historyBtn = findViewById(R.id.historyBtn);
        calcBtn = findViewById(R.id.calcBtn);
        radioGroup = findViewById(R.id.radioGroup);
        saveData = findViewById(R.id.saveDataBtn);
        roundUpCheck = findViewById(R.id.roundUpCheck);
        roundDownCheck = findViewById(R.id.roundDownCheck);


        //highlight check amount edit text box when focused
        baseAmountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.focusshape);
                } else {
                    v.setBackgroundResource(R.drawable.shape);
                }

            }
        });

        //highlight custom tip amount edit text box when focused
        customAmountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.focusshape);
                } else {
                    v.setBackgroundResource(R.drawable.shape);
                }
            }
        });


        //opens history activity
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataActivity();
            }
        });

        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalcTip tip = new CalcTip();
                tip.setBaseAmount(Float.parseFloat(baseAmountEditText.getText().toString()));
                radioSelectedId = radioGroup.getCheckedRadioButtonId();


                if (!customAmountEditText.getText().toString().trim().isEmpty()) {
                    tip.setTipAmount(Float.parseFloat(customAmountEditText.getText().toString()));
                    tipAmount = tip.getTipAmount();
                } else {
                    switch (radioSelectedId) {
                        case R.id.radio5:
                            tip.setTipPercent(5);
                            break;
                        case R.id.radio10:
                            tip.setTipPercent(10);
                            break;
                        case R.id.radio15:
                            tip.setTipPercent(15);
                            break;
                        case R.id.radio20:
                            tip.setTipPercent(20);
                            break;
                    }

                    tipAmount = tip.getBaseAmount() * (tip.getTipPercent() / 100);

                }
                total = tipAmount + tip.getBaseAmount();
                //isolate the cents from total.
                remainder = ((total * 100) % 100) / 100.0;

                /*Handle round up and round down selections*/
                if (roundUpCheck.isChecked()) {
                    //subtract remainder from 1.00 and add to tip to round up to nearest dollar.
                    tipAmount = tipAmount + (1.0 - remainder);
                    total = tipAmount + tip.getBaseAmount();
                }
                if (roundDownCheck.isChecked()) {
                    //subtract remainder from tipAmount to round down to nearest dollar.
                    tipAmount = tipAmount - remainder;
                    total = tipAmount + tip.getBaseAmount();
                }

                setTextTip.setVisibility(View.VISIBLE);
                setTextTotal.setVisibility(View.VISIBLE);
                totalTextView.setVisibility(View.VISIBLE);
                tipAmountTextView.setVisibility(View.VISIBLE);
                saveData.setVisibility(View.VISIBLE);

                //Convert tip and total to BigDecimal for formatting.
                BigDecimal tipAmountBD = new BigDecimal(tipAmount);
                BigDecimal totalAmountBD = new BigDecimal(total);

                String tipString = "$" + tipAmountBD.setScale(2, BigDecimal.ROUND_HALF_UP);
                String totalString = "$" + totalAmountBD.setScale(2, BigDecimal.ROUND_HALF_UP);


                setTextTip.setText(R.string.set_tip);
                setTextTotal.setText(R.string.set_total);
                tipAmountTextView.setText(tipString);
                totalTextView.setText(totalString);

                saveData.setEnabled(true);

                ScrollView sv = findViewById(R.id.scroll);
                sv.scrollTo(0, sv.getBottom());
                storeBaseAmount = baseAmountEditText.getText().toString();
                baseAmountEditText.clearFocus();
            }

        });


        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalTextView.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Insert Failed: insufficient data", Toast.LENGTH_LONG).show();
                } else {
                    floatBaseAmount = Float.parseFloat(storeBaseAmount);
                    floatTipAmount = Float.parseFloat(tipAmountTextView.getText().toString().replaceAll("[$]", ""));
                    floatTotal = Float.parseFloat(totalTextView.getText().toString().replaceAll("[$]", ""));

                    boolean isInserted = database.insertData(floatBaseAmount, floatTipAmount, floatTotal);
                    if (isInserted) {
                        Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        //Clear check amount EditText and Custom Tip Edit after DB insert
                        baseAmountEditText.setText("");
                        customAmountEditText.setText("");
                        setTextTip.setVisibility(View.GONE);
                        setTextTotal.setVisibility(View.GONE);
                        tipAmountTextView.setVisibility(View.GONE);
                        totalTextView.setVisibility(View.GONE);
                        saveData.setVisibility(View.GONE);

                    }
                }
            }
        });


        baseAmountEditText.addTextChangedListener(inputTextWatcher);
        customAmountEditText.addTextChangedListener(inputTextWatcher);


        //checkedListener allows the user to only select one check box
        roundUpCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    roundDownCheck.setChecked(false);
                }
            }
        });
        roundDownCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    roundUpCheck.setChecked(false);
                }
            }
        });
    }


    private TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Button calcBtn = findViewById(R.id.calcBtn);
            String baseUserInput = baseAmountEditText.getText().toString().trim();
            String tipUserInput = customAmountEditText.getText().toString().trim();
            if (baseUserInput.length() == 1 && (!Character.isDigit(baseUserInput.charAt(0)))) {
                calcBtn.setEnabled(false);
            } else if (!baseUserInput.isEmpty()) {
                calcBtn.setEnabled(true);
            } else {
                calcBtn.setEnabled(false);
            }

            if (tipUserInput.length() == 1 && (!Character.isDigit(tipUserInput.charAt(0)))) {
                calcBtn.setEnabled(false);
            }


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    public void showDataActivity() {
        Intent intent = new Intent(this, Data.class);
        startActivity(intent);
    }


}
