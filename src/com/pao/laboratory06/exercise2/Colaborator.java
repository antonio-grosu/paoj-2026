package com.pao.laboratory06.exercise2;

public abstract class Colaborator implements IOperatiiCitireScriere {
    private String nume;
    private String prenume;
    private double venitBrutLunar;

    public Colaborator() {}

    public Colaborator(String nume, String prenume, double venitBrutLunar) {
        this.nume = nume;
        this.prenume = prenume;
        this.venitBrutLunar = venitBrutLunar;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public double getVenitBrutLunar() {
        return venitBrutLunar;
    }

    protected void setNume(String nume) {
        this.nume = nume;
    }

    protected void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    protected void setVenitBrutLunar(double venitBrutLunar) {
        this.venitBrutLunar = venitBrutLunar;
    }

    public abstract double calculeazaVenitNetAnual();

    public abstract TipColaborator getTip();

    @Override
    public void afiseaza() {
        System.out.printf("%s: %s %s, venit net anual: %.2f lei\n",
                tipContract(), nume, prenume, calculeazaVenitNetAnual());
    }
}
