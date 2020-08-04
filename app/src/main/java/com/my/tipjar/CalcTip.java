package com.my.tipjar;


public class CalcTip {
    private double baseAmount;
    private double tipPercent;
    private double tipAmount;

    CalcTip() {
    }
    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public double getTipPercent() {
        return tipPercent;
    }

    public void setTipPercent(double tipPercent) {
        this.tipPercent = tipPercent;
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }


}
