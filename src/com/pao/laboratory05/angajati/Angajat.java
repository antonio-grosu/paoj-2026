package com.pao.laboratory05.angajati;

public class Angajat implements Comparable<Angajat>{
    String nume;
    Departament departament; 
    double salariu;

    @Override 
    public int compareTo(Angajat o) {
        return -Double.compare(this.salariu, o.salariu);
    }
}
