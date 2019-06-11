package com.siddharth.ipay;

public class RequestModel {
    String email, amount, name;

    public RequestModel() {
    }

    public RequestModel(String email, String amount, String name) {
        this.email = email;
        this.amount = amount;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
