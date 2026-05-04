package com.pao.proiect.licitatii.model;

import java.util.Objects;

public abstract class Utilizator {
    private int id;
    private String nume;
    private String email;

    public Utilizator(int id, String nume, String email) {
        this.id = id;
        this.nume = nume;
        this.email = email;
    }

    public abstract String getRol();

    public int getId() { return id; }
    public String getNume() { return nume; }
    public String getEmail() { return email; }

    public void setNume(String nume) { this.nume = nume; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator u = (Utilizator) o;
        return id == u.id && Objects.equals(email, u.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return getRol() + " {id=" + id + ", nume='" + nume + "', email='" + email + "'}";
    }
}
