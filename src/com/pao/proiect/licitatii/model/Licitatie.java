package com.pao.proiect.licitatii.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Licitatie {
    private int id;
    private Produs produs;
    private LocalDateTime dataStart;
    private LocalDateTime dataFinal;
    private StareLicitatie stare;
    private List<Oferta> oferte;

    public Licitatie(int id, Produs produs, LocalDateTime dataStart, LocalDateTime dataFinal) {
        this.id = id;
        this.produs = produs;
        this.dataStart = dataStart;
        this.dataFinal = dataFinal;
        this.stare = StareLicitatie.ACTIVA;
        this.oferte = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Produs getProdus() { return produs; }
    public LocalDateTime getDataStart() { return dataStart; }
    public LocalDateTime getDataFinal() { return dataFinal; }
    public StareLicitatie getStare() { return stare; }
    public List<Oferta> getOferte() { return oferte; }

    public void setStare(StareLicitatie stare) { this.stare = stare; }

    public void adaugaOferta(Oferta oferta) {
        oferte.add(oferta);
    }

    public Oferta getOfertaCastigatoare() {
        return oferte.stream()
                .max(java.util.Comparator.comparingDouble(Oferta::getSuma))
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Licitatie)) return false;
        return id == ((Licitatie) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Licitatie{id=" + id + ", produs=" + produs.getDenumire() + ", stare=" + stare + ", oferte=" + oferte.size() + "}";
    }
}
