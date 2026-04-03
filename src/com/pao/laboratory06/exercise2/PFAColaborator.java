package com.pao.laboratory06.exercise2;

import java.util.Scanner;

public class PFAColaborator extends PersoanaFizica {
    private double cheltuieliLunare;

    public PFAColaborator() {}

    @Override
    public void citeste(Scanner in) {
        setNume(in.next());
        setPrenume(in.next());
        setVenitBrutLunar(in.nextDouble());
        cheltuieliLunare = in.nextDouble();
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double saliariuMinimAnual = 4050 * 12;

        double venitNet = (getVenitBrutLunar() - cheltuieliLunare) * 12;
        double impozit = venitNet * 0.10;

        double cass;
        if (venitNet < 6 * saliariuMinimAnual) {
            cass = 6 * saliariuMinimAnual * 0.10;
        } else if (venitNet <= 72 * saliariuMinimAnual) {
            cass = venitNet * 0.10;
        } else {
            cass = 72 * saliariuMinimAnual * 0.10;
        }

        double cas;
        if (venitNet < 12 * saliariuMinimAnual) {
            cas = 0;
        } else if (venitNet <= 24 * saliariuMinimAnual) {
            cas = 12 * saliariuMinimAnual * 0.25;
        } else {
            cas = 24 * saliariuMinimAnual * 0.25;
        }

        return venitNet - impozit - cass - cas;
    }

    @Override
    public String tipContract() {
        return "PFA";
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.PFA;
    }
}
