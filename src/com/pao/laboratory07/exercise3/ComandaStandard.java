package com.pao.laboratory07.exercise3;

public non-sealed class ComandaStandard extends Comanda {
    private double pret;

    public ComandaStandard(String nume, double pret, String client) {
        this.nume = nume;
        this.pret = pret;
        this.client = client;
    }

    @Override
    public double pretFinal() {
        return pret;
    }

    @Override
    public String descriere() {
        return "STANDARD: " + nume + ", pret: " + String.format("%.2f", pret) + " lei [" + state + "] - client: " + client;
    }

    @Override
    public String descriereScurta() {
        return "STANDARD: " + nume + ", pret: " + String.format("%.2f", pret) + " lei - client: " + client;
    }
}
