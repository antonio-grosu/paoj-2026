package com.pao.laboratory14.exercise2.model;

import com.pao.laboratory14.exercise1.TipBilet;

public class Eveniment {
    private int id;
    private String nume;
    private String data;
    private int capacitate;
    private TipBilet tip;

    public Eveniment() {}

    public Eveniment(String nume, String data, int capacitate, TipBilet tip) {
        this.nume = nume;
        this.data = data;
        this.capacitate = capacitate;
        this.tip = tip;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public int getCapacitate() { return capacitate; }
    public void setCapacitate(int capacitate) { this.capacitate = capacitate; }
    public TipBilet getTip() { return tip; }
    public void setTip(TipBilet tip) { this.tip = tip; }

    @Override
    public String toString() {
        return "[" + id + "] " + nume + " | " + data + " | cap=" + capacitate + " | " + tip;
    }
}
