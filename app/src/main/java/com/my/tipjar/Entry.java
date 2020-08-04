package com.my.tipjar;

import android.widget.Button;

public class Entry {
    private Button button;
    private String date;
    private String total;

    public Entry(Button button, String date, String total) {
        this.button = button;
        this.date = date;
        this.total = total;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
