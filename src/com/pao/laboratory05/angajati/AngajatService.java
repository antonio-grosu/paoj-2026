package com.pao.laboratory05.angajati;
import java.util.*;



public class AngajatService {
    
    public static AngajatService instance;

    private Angajat[] angajati = new Angajat[0];

    private AngajatService() {
    }

    public static AngajatService getInstance() {
        if (instance == null) {
            instance = new AngajatService();
        }
        return instance;
    }

    void addAngajat(Angajat a){
        Angajat[] newAngajati = new Angajat[angajati.length + 1];
        System.arraycopy(angajati, 0, newAngajati, 0, angajati.length);
        newAngajati[angajati.length] = a;
        angajati = newAngajati;
        System.out.println("Angajat adăugat: " + a.nume);
    }

    // void printAll() — afișează toți angajații (ordinea din array, nesortat)
    void printAll(){
        for(Angajat a : angajati){
            System.out.println(a.nume + " - " + a.departament.nume() + " - " + a.salariu);
        }
    }
    // void listBySalary() — clonează, Arrays.sort(copy), afișează (descrescător, natural)

    void listBySalary(){
        Angajat[] copy = new Angajat[angajati.length];
        System.arraycopy(angajati, 0, copy, 0, angajati.length);
        Arrays.sort(copy);
        for(Angajat a : copy){
            System.out.println("" + a.nume + " - " + a.departament.nume() + " - " + a.salariu);
        }
    }
    // void findByDepartament(String numeDept) — parcurge array-ul, afișează toți angajații al
    //  căror angajat.getDepartament().nume().equalsIgnoreCase(numeDept);

    void findByDepartament(String numeDept){
        boolean found = false;
        for(Angajat a : angajati){
            if(a.departament.nume().equalsIgnoreCase(numeDept)){
                System.out.println("" + a.nume + " - " + a.departament.nume() + " - " + a.salariu);
                found = true;
            }
        }
        if(!found){
            System.out.println("Niciun angajat în departamentul: " + numeDept);
        }
    }
    //   dacă nu găsește niciun angajat, afișează "Niciun angajat în departamentul: <numeDept>"

}
