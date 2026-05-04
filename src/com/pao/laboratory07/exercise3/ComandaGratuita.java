package com.pao.laboratory07.exercise3;

public non-sealed class ComandaGratuita extends Comanda {

    public ComandaGratuita(String nume, String client) {
        this.nume = nume;
        this.client = client;
    }

    @Override
    public double pretFinal() {
        return 0;
    }

    @Override
    public String descriere() {
        return "GIFT: " + nume + ", gratuit [" + state + "] - client: " + client;
    }

    @Override
    public String descriereScurta() {
        return "GIFT: " + nume + ", gratuit - client: " + client;
    }
}
