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
import com.pao.proiect.licitatii.service.AuditService;
import com.pao.proiect.licitatii.service.LicitatieService;
import com.pao.proiect.licitatii.service.ProdusService;
import com.pao.proiect.licitatii.service.UtilizatorService;
import com.pao.proiect.licitatii.util.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

public class Main {
    private static final UtilizatorService utilizatorService = UtilizatorService.getInstance();
    private static final ProdusService produsService = ProdusService.getInstance();
    private static final LicitatieService licitatieService = LicitatieService.getInstance();
    private static final AuditService audit = AuditService.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        DatabaseConnection db = DatabaseConnection.getInstance();
        String mod = args.length > 0 ? args[0] : "";

        if (mod.equals("--reset")) {
            db.resetSchema();
            System.out.println("Baza de date a fost resetata.");
            db.close();
            return;
        }

        if (mod.equals("--demo")) {
            db.resetSchema();   // demo determinist: porneste de la zero
            ruleazaDemo();
            db.close();
            return;
        }

        db.initSchema();   // pornire normala: pastreaza datele existente

        boolean running = true;
        while (running) {
            afiseazaMeniu();
            int optiune = citesteInt("Optiune: ");
            System.out.println();
            try {
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
            } catch (SQLException e) {
                System.out.println("Eroare baza de date: " + e.getMessage() + "\n");
            }
        }
        System.out.println("La revedere!");
        scanner.close();
        DatabaseConnection.getInstance().close();
    }

    /** Demo non-interactiv: exercita toate cele 10 actiuni pe baza de date. */
    private static void ruleazaDemo() throws Exception {
        System.out.println("=== DEMO SISTEM LICITATII (JDBC) ===\n");

        // 1. Inregistrare utilizatori
        Vanzator vanzator = utilizatorService.adaugaVanzator("Ana Pop", "ana@mail.com", "RO49AAAA1B31007593840000");
        Cumparator c1 = utilizatorService.adaugaCumparator("Ion Ionescu", "ion@mail.com", 10000);
        Cumparator c2 = utilizatorService.adaugaCumparator("Maria Dan", "maria@mail.com", 8000);
        audit.log("inregistrare_utilizator");
        System.out.println("1. Inregistrati: " + vanzator + ", " + c1 + ", " + c2);

        // 2. Adaugare produse
        Produs tablou = produsService.adaugaProdus("PRD001", "Tablou Grigorescu", "Pictura originala",
                CategorieProdus.ARTA, 5000, vanzator);
        Produs laptop = produsService.adaugaProdus("PRD002", "Laptop ThinkPad", "Stare buna",
                CategorieProdus.ELECTRONICE, 2000, vanzator);
        audit.log("adaugare_produs");
        System.out.println("2. Produse adaugate: " + tablou + ", " + laptop);

        // 3. Creare licitatii
        LocalDateTime acum = LocalDateTime.now();
        Licitatie licTablou = licitatieService.creeazaLicitatie(tablou, acum, acum.plusDays(7));
        Licitatie licLaptop = licitatieService.creeazaLicitatie(laptop, acum, acum.plusDays(3));
        audit.log("creare_licitatie");
        System.out.println("3. Licitatii create: " + licTablou + ", " + licLaptop);

        // 4. Plasare oferte (tranzactie)
        licitatieService.plaseazaOferta(licTablou.getId(), c1, 5500);
        licitatieService.plaseazaOferta(licTablou.getId(), c2, 6200);
        licitatieService.plaseazaOferta(licLaptop.getId(), c1, 2100);
        audit.log("plasare_oferta");
        System.out.println("4. Oferte plasate pe ambele licitatii.");

        // demonstrare rollback: oferta sub pretul minim => exceptie, nimic salvat
        try {
            licitatieService.plaseazaOferta(licTablou.getId(), c1, 100);
        } catch (OfertaInsuficientaException e) {
            System.out.println("   [rollback] respins: " + e.getMessage());
        }

        // 5. Inchidere licitatie + castigator
        licitatieService.inchideLicitatie(licTablou.getId());
        Oferta castigatoare = licitatieService.getOfertaCastigatoare(licTablou.getId());
        audit.log("inchidere_licitatie");
        System.out.println("5. Licitatie '" + tablou.getDenumire() + "' inchisa. Castigator: "
                + castigatoare.getCumparator().getNume() + " cu " + castigatoare.getSuma() + " RON");

        // 6. Cautare utilizator dupa id
        audit.log("cautare_utilizator");
        System.out.println("6. Cautare id=" + c1.getId() + ": " + utilizatorService.cautaDupaId(c1.getId()));

        // 7. Listare produse sortate dupa pret
        audit.log("listare_produse_sortate");
        System.out.println("7. Produse sortate dupa pret:");
        for (Produs p : produsService.listeazaSortateDupaPret()) {
            System.out.println("   [" + p.getCod() + "] " + p.getDenumire() + " - " + p.getPretMinim() + " RON");
        }

        // 8. Listare produse pe categorie
        audit.log("listare_produse_categorie");
        System.out.println("8. Produse pe categorii:");
        produsService.grupeazaDupaCategorie().forEach((cat, lista) -> {
            System.out.println("   " + cat + ": " + lista.size() + " produs(e)");
        });

        // 9. Raport oferte per cumparator (JOIN)
        audit.log("raport_oferte_cumparator");
        System.out.println("9. Oferte per cumparator:");
        licitatieService.ofertelePerCumparator().forEach((nume, oferte) -> {
            System.out.println("   " + nume + ": " + oferte);
        });

        // 10. Produse in licitatii active (JOIN)
        audit.log("raport_produse_active");
        System.out.println("10. Produse in licitatii active:");
        for (Produs p : licitatieService.produseInLicitatiiActive()) {
            System.out.println("   - " + p.getDenumire() + " (" + p.getPretMinim() + " RON)");
        }

        System.out.println("\n=== Top produse dupa oferte (JOIN) ===");
        licitatieService.topProduseDupaOferte().forEach(s -> System.out.println("   " + s));

        System.out.println("\n=== Demo finalizat. Verifica audit.csv ===");
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
    private static void inregistreazaUtilizator() throws SQLException {
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
            return;
        }
        audit.log("inregistrare_utilizator");
    }

    // --- Actiunea 2 ---
    private static void adaugaProdus() throws SQLException {
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
            audit.log("adaugare_produs");
            System.out.println("Produs adaugat: " + p + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // --- Actiunea 3 ---
    private static void creeazaLicitatie() throws SQLException {
        List<Produs> produse = produsService.listeazaToate();
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
        audit.log("creare_licitatie");
        System.out.println("Licitatie creata: " + l + "\n");
    }

    // --- Actiunea 4 ---
    private static void plaseazaOferta() throws SQLException {
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
            audit.log("plasare_oferta");
            System.out.println("Oferta plasata: " + o + "\n");
        } catch (LicitatieInchisaException | OfertaInsuficientaException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // --- Actiunea 5 ---
    private static void inchideLicitatie() throws SQLException {
        List<Licitatie> active = licitatieService.listeazaActive();
        if (active.isEmpty()) {
            System.out.println("Nu exista licitatii active.\n");
            return;
        }
        System.out.println("Licitatii active:");
        for (Licitatie l : active) {
            System.out.println("  [" + l.getId() + "] " + l.getProdus().getDenumire());
        }
        int id = citesteInt("ID licitatie de inchis: ");
        try {
            licitatieService.inchideLicitatie(id);
            Oferta castigatoare = licitatieService.getOfertaCastigatoare(id);
            audit.log("inchidere_licitatie");
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
    private static void cautaUtilizator() throws SQLException {
        int id = citesteInt("ID utilizator: ");
        try {
            Utilizator u = utilizatorService.cautaDupaId(id);
            audit.log("cautare_utilizator");
            System.out.println("Gasit: " + u + "\n");
        } catch (UtilizatorNegasitException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // --- Actiunea 7 ---
    private static void listeazaProduseSort() throws SQLException {
        List<Produs> produse = produsService.listeazaSortateDupaPret();
        if (produse.isEmpty()) {
            System.out.println("Nu exista produse.\n");
            return;
        }
        System.out.println("Produse sortate dupa pret minim:");
        for (Produs p : produse) {
            System.out.println("  [" + p.getCod() + "] " + p.getDenumire() + " - " + p.getPretMinim() + " RON (vanzator: " + p.getVanzator().getNume() + ")");
        }
        audit.log("listare_produse_sortate");
        System.out.println();
    }

    // --- Actiunea 8 ---
    private static void listeazaProduseCategorie() throws SQLException {
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
        audit.log("listare_produse_categorie");
        System.out.println();
    }

    // --- Actiunea 9 ---
    private static void raportOferteCumparator() throws SQLException {
        Map<String, List<String>> raport = licitatieService.ofertelePerCumparator();
        if (raport.isEmpty()) {
            System.out.println("Nu exista oferte plasate.\n");
            return;
        }
        System.out.println("Oferte per cumparator:");
        for (Map.Entry<String, List<String>> entry : raport.entrySet()) {
            System.out.println("  " + entry.getKey() + " (" + entry.getValue().size() + " oferta/e):");
            for (String detaliu : entry.getValue()) {
                System.out.println("    - " + detaliu);
            }
        }
        audit.log("raport_oferte_cumparator");
        System.out.println();
    }

    // --- Actiunea 10 ---
    private static void raportProduseActive() throws SQLException {
        TreeSet<Produs> produse = licitatieService.produseInLicitatiiActive();
        if (produse.isEmpty()) {
            System.out.println("Nu exista licitatii active.\n");
            return;
        }
        System.out.println("Produse in licitatii active (sortate dupa pret minim):");
        for (Produs p : produse) {
            System.out.println("  - " + p.getDenumire() + " | pret minim: " + p.getPretMinim() + " RON | vanzator: " + p.getVanzator().getNume());
        }
        audit.log("raport_produse_active");
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
