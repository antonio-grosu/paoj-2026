package com.pao.proiect.licitatii.model;

public class Cumparator extends Utilizator {
    private double buget;

    public Cumparator(int id, String nume, String email, double buget) {
        super(id, nume, email);
        this.buget = buget;
    }

    @Override
    public String getRol() { return "Cumparator"; }

    public double getBuget() { return buget; }
    public void setBuget(double buget) { this.buget = buget; }

    @Override
    public String toString() {
        return super.toString() + ", buget=" + buget + "}";
    }
}
