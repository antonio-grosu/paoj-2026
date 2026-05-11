package com.pao.laboratory09.exercise3;

public class Tranzactie {
    private final int id;
    private final double suma;
    private final String data;

    public Tranzactie(int id, double suma, String data) {
        this.id = id;
        this.suma = suma;
        this.data = data;
    }

    public int getId() { return id; }
    public double getSuma() { return suma; }
    public String getData() { return data; }
}
