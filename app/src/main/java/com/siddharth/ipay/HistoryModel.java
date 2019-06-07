package com.siddharth.ipay;

public class HistoryModel {
    private String name, amount;

    public HistoryModel() {
    }

    public HistoryModel(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String number) {
        this.amount = number;
    }
}
