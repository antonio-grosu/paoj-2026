package com.pao.laboratory07.exercise3;

public non-sealed class ComandaRedusa extends Comanda {
    private double pret;
    private int discount;

    public ComandaRedusa(String nume, double pret, int discount, String client) {
        this.nume = nume;
        this.pret = pret;
        this.discount = discount;
        this.client = client;
    }

    public int getDiscount() { return discount; }

    @Override
    public double pretFinal() {
        return pret * (100 - discount) / 100.0;
    }

    @Override
    public String descriere() {
        return "DISCOUNTED: " + nume + ", pret: " + String.format("%.2f", pretFinal()) + " lei (-" + discount + "%) [" + state + "] - client: " + client;
    }

    @Override
    public String descriereScurta() {
        return "DISCOUNTED: " + nume + ", pret: " + String.format("%.2f", pretFinal()) + " lei (-" + discount + "%) - client: " + client;
    }
}
