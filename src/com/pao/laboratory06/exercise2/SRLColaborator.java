package com.pao.laboratory06.exercise2;

import java.util.Scanner;

public class SRLColaborator extends PersoanaJuridica {
    private double cheltuieliLunare;

    public SRLColaborator() {}

    @Override
    public void citeste(Scanner in) {
        setNume(in.next());
        setPrenume(in.next());
        setVenitBrutLunar(in.nextDouble());
        cheltuieliLunare = in.nextDouble();
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double profit = (getVenitBrutLunar() - cheltuieliLunare) * 12;
        return profit * 0.84;
    }

    @Override
    public String tipContract() {
        return "SRL";
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.SRL;
    }
}
