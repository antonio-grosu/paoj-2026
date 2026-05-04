package com.pao.proiect.licitatii.model;

public class Vanzator extends Utilizator {
    private String iban;

    public Vanzator(int id, String nume, String email, String iban) {
        super(id, nume, email);
        this.iban = iban;
    }

    @Override
    public String getRol() { return "Vanzator"; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    @Override
    public String toString() {
        return super.toString() + ", iban='" + iban + "'}";
    }
}
