package com.pao.laboratory06.exercise2;

import java.util.Scanner;

public class CIMColaborator extends PersoanaFizica {
    private boolean bonus;

    public CIMColaborator() {}

    @Override
    public void citeste(Scanner in) {
        setNume(in.next());
        setPrenume(in.next());
        setVenitBrutLunar(in.nextDouble());
        String b = in.next();
        bonus = b.equals("DA");
    }

    @Override
    public double calculeazaVenitNetAnual() {
        double net = getVenitBrutLunar() * 12 * 0.55;
        if (bonus) {
            net = net * 1.1;
        }
        return net;
    }

    @Override
    public String tipContract() {
        return "CIM";
    }

    @Override
    public TipColaborator getTip() {
        return TipColaborator.CIM;
    }

    @Override
    public boolean areBonus() {
        return bonus;
    }
}
