package com.pao.laboratory07.exercise2;

public non-sealed class ComandaGratuita extends Comanda {
    public ComandaGratuita(String nume) {
        this.nume = nume;
    }

    @Override
    public double pretFinal() {
        return 0;
    }

    @Override
    public String descriere() {
        return "GIFT: " + nume + ", gratuit [" + state + "]";
    }
    
}
