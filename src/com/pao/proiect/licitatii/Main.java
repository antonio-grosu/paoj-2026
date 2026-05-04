package com.pao.proiect.licitatii;

import com.pao.proiect.licitatii.exception.LicitatieInchisaException;
import com.pao.proiect.licitatii.exception.OfertaInsuficientaException;
import com.pao.proiect.licitatii.exception.UtilizatorNegasitException;
import com.pao.proiect.licitatii.model.CategorieProdus;
import com.pao.proiect.licitatii.model.Cumparator;
import com.pao.proiect.licitatii.model.Licitatie;
import com.pao.proiect.licitatii.model.Oferta;
import com.pao.proiect.licitatii.model.Produs;
import com.pao.proiect.licitatii.model.Utilizator;
import com.pao.proiect.licitatii.model.Vanzator;
import com.pao.proiect.licitatii.service.LicitatieService;
import com.pao.proiect.licitatii.service.ProdusService;
import com.pao.proiect.licitatii.service.UtilizatorService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

public class Main {
    private static final UtilizatorService utilizatorService = UtilizatorService.getInstance();
    private static final ProdusService produsService = ProdusService.getInstance();
    private static final LicitatieService licitatieService = LicitatieService.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            afiseazaMeniu();
            int optiune = citesteInt("Optiune: ");
            System.out.println();
            switch (optiune) {
                case 1  -> inregistreazaUtilizator();
                case 2  -> adaugaProdus();
                case 3  -> creeazaLicitatie();
                case 4  -> plaseazaOferta();
                case 5  -> inchideLicitatie();
                case 6  -> cautaUtilizator();
                case 7  -> listeazaProduseSort();
                case 8  -> listeazaProduseCategorie();
                case 9  -> raportOferteCumparator();
                case 10 -> raportProduseActive();
                case 0  -> running = false;
                default -> System.out.println("Optiune invalida.\n");
            }
        }
        System.out.println("La revedere!");
        scanner.close();
    }

    private static void afiseazaMeniu() {
        System.out.println("========== SISTEM LICITATII ==========");
        System.out.println(" 1. Inregistrare utilizator");
        System.out.println(" 2. Adaugare produs");
        System.out.println(" 3. Creare licitatie");
        System.out.println(" 4. Plasare oferta");
        System.out.println(" 5. Inchidere licitatie + castigator");
        System.out.println(" 6. Cauta utilizator dupa ID");
        System.out.println(" 7. Listeaza produse sortate dupa pret");
        System.out.println(" 8. Listeaza produse pe categorie");
        System.out.println(" 9. Raport oferte per cumparator");
        System.out.println("10. Produse in licitatii active");
        System.out.println(" 0. Iesire");
        System.out.println("======================================");
    }

    // --- Actiunea 1 ---
    private static void inregistreazaUtilizator() {
        System.out.println("Tip utilizator: 1=Cumparator, 2=Vanzator");
        int tip = citesteInt("Tip: ");
        String nume = citesteString("Nume: ");
        String email = citesteString("Email: ");

        if (tip == 1) {
            double buget = citesteDouble("Buget (RON): ");
            Cumparator c = utilizatorService.adaugaCumparator(nume, email, buget);
            System.out.println("Inregistrat: " + c + "\n");
        } else if (tip == 2) {
            String iban = citesteString("IBAN: ");
            Vanzator v = utilizatorService.adaugaVanzator(nume, email, iban);
            System.out.println("Inregistrat: " + v + "\n");
        } else {
            System.out.println("Tip invalid.\n");
        }
    }

    // --- Actiunea 2 ---
    private static void adaugaProdus() {
        List<Vanzator> vanzatori = utilizatorService.listeazaVanzatori();
        if (vanzatori.isEmpty()) {
            System.out.println("Nu exista vanzatori inregistrati.\n");
            return;
        }
        System.out.println("Vanzatori disponibili:");
        for (Vanzator v : vanzatori) {
            System.out.println("  [" + v.getId() + "] " + v.getNume());
        }
        int idVanzator = citesteInt("ID vanzator: ");
        Vanzator vanzator;
        try {
            vanzator = (Vanzator) utilizatorService.cautaDupaId(idVanzator);
        } catch (UtilizatorNegasitException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
            return;
        }

        String cod = citesteString("Cod produs (ex: PRD001): ");
        String denumire = citesteString("Denumire: ");
        String descriere = citesteString("Descriere: ");

        System.out.println("Categorii: " + java.util.Arrays.toString(CategorieProdus.values()));
        String catStr = citesteString("Categorie: ").toUpperCase();
        CategorieProdus categorie;
        try {
            categorie = CategorieProdus.valueOf(catStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Categorie invalida.\n");
            return;
        }

        double pretMinim = citesteDouble("Pret minim (RON): ");

        try {
            Produs p = produsService.adaugaProdus(cod, denumire, descriere, categorie, pretMinim, vanzator);
            System.out.println("Produs adaugat: " + p + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // --- Actiunea 3 ---
    private static void creeazaLicitatie() {
        List<Produs> produse = produsService.listeazaToare();
        if (produse.isEmpty()) {
            System.out.println("Nu exista produse in sistem.\n");
            return;
        }
        System.out.println("Produse disponibile:");
        for (Produs p : produse) {
            System.out.println("  [" + p.getCod() + "] " + p.getDenumire() + " - pret minim: " + p.getPretMinim() + " RON");
        }
        String cod = citesteString("Cod produs: ");
        Produs produs;
        try {
            produs = produsService.cautaDupaCod(cod);
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
            return;
        }

        int zileDurata = citesteInt("Durata licitatie (zile): ");
        LocalDateTime acum = LocalDateTime.now();
        Licitatie l = licitatieService.creeazaLicitatie(produs, acum, acum.plusDays(zileDurata));
        System.out.println("Licitatie creata: " + l + "\n");
    }

    // --- Actiunea 4 ---
    private static void plaseazaOferta() {
        List<Licitatie> active = licitatieService.listeazaActive();
        if (active.isEmpty()) {
            System.out.println("Nu exista licitatii active.\n");
            return;
        }
        System.out.println("Licitatii active:");
        for (Licitatie l : active) {
            System.out.println("  [" + l.getId() + "] " + l.getProdus().getDenumire() + " - pret minim: " + l.getProdus().getPretMinim() + " RON");
        }
        int idLicitatie = citesteInt("ID licitatie: ");

        List<Cumparator> cumparatori = utilizatorService.listeazaCumparatori();
        if (cumparatori.isEmpty()) {
            System.out.println("Nu exista cumparatori inregistrati.\n");
            return;
        }
        System.out.println("Cumparatori:");
        for (Cumparator c : cumparatori) {
            System.out.println("  [" + c.getId() + "] " + c.getNume());
        }
        int idCumparator = citesteInt("ID cumparator: ");
        Cumparator cumparator;
        try {
            cumparator = (Cumparator) utilizatorService.cautaDupaId(idCumparator);
        } catch (UtilizatorNegasitException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
            return;
        }

        double suma = citesteDouble("Suma oferta (RON): ");
        try {
            Oferta o = licitatieService.plaseazaOferta(idLicitatie, cumparator, suma);
            System.out.println("Oferta plasata: " + o + "\n");
        } catch (LicitatieInchisaException | OfertaInsuficientaException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // --- Actiunea 5 ---
    private static void inchideLicitatie() {
        List<Licitatie> active = licitatieService.listeazaActive();
        if (active.isEmpty()) {
            System.out.println("Nu exista licitatii active.\n");
            return;
        }
        System.out.println("Licitatii active:");
        for (Licitatie l : active) {
            System.out.println("  [" + l.getId() + "] " + l.getProdus().getDenumire() + " (" + l.getOferte().size() + " oferte)");
        }
        int id = citesteInt("ID licitatie de inchis: ");
        try {
            licitatieService.inchideLicitatie(id);
            Oferta castigatoare = licitatieService.getOfertaCastigatoare(id);
            System.out.println("Licitatie inchisa.");
            if (castigatoare != null) {
                System.out.println("Castigator: " + castigatoare.getCumparator().getNume() + " cu " + castigatoare.getSuma() + " RON");
            } else {
                System.out.println("Nicio oferta plasata.");
            }
            System.out.println();
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // --- Actiunea 6 ---
    private static void cautaUtilizator() {
        int id = citesteInt("ID utilizator: ");
        try {
            Utilizator u = utilizatorService.cautaDupaId(id);
            System.out.println("Gasit: " + u + "\n");
        } catch (UtilizatorNegasitException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // --- Actiunea 7 ---
    private static void listeazaProduseSort() {
        List<Produs> produse = produsService.listeazaSortateDupaPreт();
        if (produse.isEmpty()) {
            System.out.println("Nu exista produse.\n");
            return;
        }
        System.out.println("Produse sortate dupa pret minim:");
        for (Produs p : produse) {
            System.out.println("  [" + p.getCod() + "] " + p.getDenumire() + " - " + p.getPretMinim() + " RON (vanzator: " + p.getVanzator().getNume() + ")");
        }
        System.out.println();
    }

    // --- Actiunea 8 ---
    private static void listeazaProduseCategorie() {
        Map<CategorieProdus, List<Produs>> grupe = produsService.grupeazaDupaCategorie();
        if (grupe.isEmpty()) {
            System.out.println("Nu exista produse.\n");
            return;
        }
        System.out.println("Produse pe categorii:");
        for (Map.Entry<CategorieProdus, List<Produs>> entry : grupe.entrySet()) {
            System.out.println("  " + entry.getKey() + ":");
            for (Produs p : entry.getValue()) {
                System.out.println("    - [" + p.getCod() + "] " + p.getDenumire() + " (" + p.getPretMinim() + " RON)");
            }
        }
        System.out.println();
    }

    // --- Actiunea 9 ---
    private static void raportOferteCumparator() {
        Map<Cumparator, List<Oferta>> raport = licitatieService.ofertelePerCumparator();
        if (raport.isEmpty()) {
            System.out.println("Nu exista oferte plasate.\n");
            return;
        }
        System.out.println("Oferte per cumparator:");
        for (Map.Entry<Cumparator, List<Oferta>> entry : raport.entrySet()) {
            System.out.println("  " + entry.getKey().getNume() + " (" + entry.getValue().size() + " oferta/e):");
            for (Oferta o : entry.getValue()) {
                System.out.println("    - " + o.getSuma() + " RON la " + o.getDataOferta());
            }
        }
        System.out.println();
    }

    // --- Actiunea 10 ---
    private static void raportProduseActive() {
        TreeSet<Produs> produse = licitatieService.produseInLicitatiiActive();
        if (produse.isEmpty()) {
            System.out.println("Nu exista licitatii active.\n");
            return;
        }
        System.out.println("Produse in licitatii active (sortate dupa pret minim):");
        for (Produs p : produse) {
            System.out.println("  - " + p.getDenumire() + " | pret minim: " + p.getPretMinim() + " RON | vanzator: " + p.getVanzator().getNume());
        }
        System.out.println();
    }

    private static int citesteInt(String mesaj) {
        while (true) {
            System.out.print(mesaj);
            String linie = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linie);
            } catch (NumberFormatException e) {
                System.out.println("Valoare invalida. Introduceti un numar intreg.");
            }
        }
    }

    private static double citesteDouble(String mesaj) {
        while (true) {
            System.out.print(mesaj);
            String linie = scanner.nextLine().trim();
            try {
                return Double.parseDouble(linie);
            } catch (NumberFormatException e) {
                System.out.println("Valoare invalida. Introduceti un numar.");
            }
        }
    }

    private static String citesteString(String mesaj) {
        String valoare = "";
        while (valoare.isEmpty()) {
            System.out.print(mesaj);
            valoare = scanner.nextLine().trim();
            if (valoare.isEmpty()) System.out.println("Campul nu poate fi gol.");
        }
        return valoare;
    }
}
