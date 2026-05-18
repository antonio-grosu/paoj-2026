package com.pao.laboratory10.exercise3;

import java.util.*;
import java.util.stream.*;

public class Main {
    enum Tip { CREDIT, DEBIT }

    static class Tranzactie {
        int id;
        double suma;
        String data;
        Tip tip;
        String contSursa;

        Tranzactie(int id, double suma, String data, Tip tip, String contSursa) {
            this.id = id;
            this.suma = suma;
            this.data = data;
            this.tip = tip;
            this.contSursa = contSursa;
        }

        @Override
        public String toString() {
            return String.format("[%d] %s %s: %.2f RON", id, data, tip, suma);
        }
    }

    public static void main(String[] args) {
        List<Tranzactie> tranzactii = Arrays.asList(
                new Tranzactie(1,  1500.00, "2024-01-15", Tip.CREDIT, "RO01CONT1"),
                new Tranzactie(2,   750.50, "2024-01-22", Tip.DEBIT,  "RO02CONT2"),
                new Tranzactie(3,   200.00, "2024-02-05", Tip.CREDIT, "RO01CONT1"),
                new Tranzactie(4,  1200.00, "2024-02-18", Tip.DEBIT,  "RO03CONT3"),
                new Tranzactie(5,   500.00, "2024-03-10", Tip.CREDIT, "RO02CONT2"),
                new Tranzactie(6,   300.00, "2024-03-22", Tip.DEBIT,  "RO01CONT1"),
                new Tranzactie(7,  2000.00, "2024-01-30", Tip.CREDIT, "RO04CONT4"),
                new Tranzactie(8,   450.00, "2024-02-14", Tip.DEBIT,  "RO04CONT4"),
                new Tranzactie(9,   800.00, "2024-03-05", Tip.CREDIT, "RO03CONT3"),
                new Tranzactie(10,  100.00, "2024-03-28", Tip.DEBIT,  "RO02CONT2")
        );

        System.out.println("--- 1. Tranzactii CREDIT ---");
        tranzactii.stream()
                .filter(t -> t.tip == Tip.CREDIT)
                .forEach(System.out::println);

        System.out.println("--- 2. Total procesat ---");
        double total = tranzactii.stream().mapToDouble(t -> t.suma).sum();
        System.out.printf("Total procesat: %.2f RON%n", total);

        System.out.println("--- 3. Suma pe luni ---");
        new TreeMap<>(tranzactii.stream()
                .collect(Collectors.groupingBy(
                        t -> t.data.substring(0, 7),
                        Collectors.summingDouble(t -> t.suma)
                )))
                .forEach((luna, s) -> System.out.printf("%s: %.2f RON%n", luna, s));

        System.out.println("--- 4. Top 3 tranzactii ---");
        System.out.println("Top 3 tranzactii:");
        tranzactii.stream()
                .sorted(Comparator.comparingDouble((Tranzactie t) -> t.suma).reversed())
                .limit(3)
                .forEach(System.out::println);

        System.out.println("--- 5. Conturi sursa unice ---");
        List<String> conturi = tranzactii.stream()
                .map(t -> t.contSursa)
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Conturi sursa unice: " + conturi);

        System.out.println("--- 6. Suma medie ---");
        double medie = tranzactii.stream().mapToDouble(t -> t.suma).average().orElse(0.0);
        System.out.printf("Suma medie: %.2f RON%n", medie);

        System.out.println("--- 7. Extras de cont pe luni ---");
        new TreeMap<>(tranzactii.stream()
                .collect(Collectors.groupingBy(t -> t.data.substring(0, 7))))
                .forEach((luna, lista) -> {
                    double sumLuna = lista.stream().mapToDouble(t -> t.suma).sum();
                    System.out.printf("EXTRAS DE CONT - %s: %d tranzactii, total: %.2f RON%n",
                            luna, lista.size(), sumLuna);
                });
    }
}
