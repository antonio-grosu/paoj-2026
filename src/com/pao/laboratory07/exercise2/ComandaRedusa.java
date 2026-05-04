package com.pao.laboratory07.exercise2;

public non-sealed class ComandaRedusa extends Comanda {
    private double pret;
    private int discount;

    public ComandaRedusa(String nume, double pret, int discount) {
        this.nume = nume;
        this.pret = pret;
        this.discount = discount;
    }

    @Override
    public  double pretFinal(){
        return pret * (100 - discount) / 100.0;
    };


    @Override
    public String descriere(){
        return "DISCOUNTED: " + nume + ", pret: " + String.format("%.2f", pretFinal()) + " lei (-" + discount + "%) [" + state + "]";
    }
}
