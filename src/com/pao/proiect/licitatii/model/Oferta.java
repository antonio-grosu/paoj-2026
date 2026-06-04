package com.pao.proiect.licitatii.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Oferta {
    private int id;
    private Cumparator cumparator;
    private double suma;
    private LocalDateTime dataOferta;

    public Oferta(int id, Cumparator cumparator, double suma) {
        this.id = id;
        this.cumparator = cumparator;
        this.suma = suma;
        this.dataOferta = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cumparator getCumparator() { return cumparator; }
    public double getSuma() { return suma; }
    public LocalDateTime getDataOferta() { return dataOferta; }
    public void setDataOferta(LocalDateTime dataOferta) { this.dataOferta = dataOferta; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Oferta)) return false;
        return id == ((Oferta) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Oferta{id=" + id + ", cumparator='" + cumparator.getNume() + "', suma=" + suma + "}";
    }
}
