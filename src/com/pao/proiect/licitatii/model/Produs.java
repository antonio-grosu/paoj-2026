package com.pao.proiect.licitatii.model;

import java.util.Objects;

public class Produs implements Comparable<Produs> {
    private CodProdus cod;
    private String denumire;
    private String descriere;
    private CategorieProdus categorie;
    private double pretMinim;
    private Vanzator vanzator;

    public Produs(CodProdus cod, String denumire, String descriere, CategorieProdus categorie, double pretMinim, Vanzator vanzator) {
        this.cod = cod;
        this.denumire = denumire;
        this.descriere = descriere;
        this.categorie = categorie;
        this.pretMinim = pretMinim;
        this.vanzator = vanzator;
    }

    public CodProdus getCod() { return cod; }
    public String getDenumire() { return denumire; }
    public String getDescriere() { return descriere; }
    public CategorieProdus getCategorie() { return categorie; }
    public double getPretMinim() { return pretMinim; }
    public Vanzator getVanzator() { return vanzator; }

    public void setDenumire(String denumire) { this.denumire = denumire; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    public void setPretMinim(double pretMinim) { this.pretMinim = pretMinim; }

    @Override
    public int compareTo(Produs alt) {
        return Double.compare(this.pretMinim, alt.pretMinim);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produs)) return false;
        return Objects.equals(cod, ((Produs) o).cod);
    }

    @Override
    public int hashCode() { return Objects.hash(cod); }

    @Override
    public String toString() {
        return "Produs{cod=" + cod + ", denumire='" + denumire + "', categorie=" + categorie + ", pretMinim=" + pretMinim + "}";
    }
}
