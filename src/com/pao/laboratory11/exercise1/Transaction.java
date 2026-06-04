package com.pao.laboratory11.exercise1;

public final class Transaction {
    private final int id;
    private final double amount;
    private final String date;
    private final String country;
    private final String channel;

    public Transaction(int id, double amount, String date, String country, String channel) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.country = country;
        this.channel = channel;
    }

    public int id() {
        return id;
    }

    public double amount() {
        return amount;
    }

    public String date() {
        return date;
    }

    public String country() {
        return country;
    }

    public String channel() {
        return channel;
    }
}
