package com.pao.laboratory05.angajati;

import java.util.Scanner;

/**
 * Exercise 3 — Angajați
 *
 * Cerințele complete se află în:
 *   src/com/pao/laboratory05/Readme.md  →  secțiunea "Exercise 3 — Angajați"
 *
 * Creează fișierele de la zero în acest pachet, apoi rulează Main.java
 * pentru a verifica output-ul așteptat din Readme.
 */
public class Main {
    public static void main(String[] args) {
        while (true) {
        System.out.println("\n===== Gestionare Angajați =====");
        System.out.println("1. Adaugă angajat");
        System.out.println("2. Listare după salariu");
        System.out.println("3. Caută după departament");
        System.out.println("0. Ieșire");
        System.out.print("Opțiune: ");
    // citește opțiunea și execută acțiunea

    Scanner scanner = new Scanner(System.in);
    int optiune = scanner.nextInt();
    switch (optiune) {
        case 1 -> {
            System.out.print("Nume angajat: ");
            String nume = scanner.next();
            System.out.print("Nume departament: ");
            String numeDept = scanner.next();
            System.out.print("Locație departament: ");
            String locatieDept = scanner.next();
            System.out.print("Salariu: ");
            double salariu = scanner.nextDouble();

            Departament dept = new Departament(numeDept, locatieDept);
            Angajat a = new Angajat();
            a.nume = nume;
            a.departament = dept;
            a.salariu = salariu;

            AngajatService.getInstance().addAngajat(a);
        }
        case 2 -> AngajatService.getInstance().listBySalary();
        case 3 -> {
            System.out.print("Nume departament căutat: ");
            String numeDept = scanner.next();
            AngajatService.getInstance().findByDepartament(numeDept);
        }
        case 0 -> {
            System.out.println("La revedere!");
            return;
        }
        default -> System.out.println("Opțiune invalidă!");
    }
}
    }
}
