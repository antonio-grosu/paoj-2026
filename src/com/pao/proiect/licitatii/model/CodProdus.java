package com.pao.proiect.licitatii.model;

import java.util.Objects;

public final class CodProdus {
    private final String valoare;

    public CodProdus(String valoare) {
        if (valoare == null || valoare.isBlank()) {
            throw new IllegalArgumentException("Codul produsului nu poate fi gol");
        }
        this.valoare = valoare.toUpperCase();
    }

    public String getValoare() { return valoare; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodProdus)) return false;
        return Objects.equals(valoare, ((CodProdus) o).valoare);
    }

    @Override
    public int hashCode() { return Objects.hash(valoare); }

    @Override
    public String toString() { return valoare; }
}
