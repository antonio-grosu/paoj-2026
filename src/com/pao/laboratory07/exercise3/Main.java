package com.pao.laboratory07.exercise3;

import java.util.*;
import java.util.stream.*;

public class Main {
    public static void main(String[] args) {
        List<Comanda> comenzi = new ArrayList<>();

        try (Scanner sc = new Scanner(System.in)) {
            int n = Integer.parseInt(sc.nextLine().trim());

            for (int i = 0; i < n; i++) {
                String[] tokens = sc.nextLine().trim().split(" ");
                switch (tokens[0]) {
                    case "STANDARD" -> {
                        String client = tokens[3];
                        comenzi.add(new ComandaStandard(tokens[1], Double.parseDouble(tokens[2]), client));
                    }
                    case "DISCOUNTED" -> {
                        String client = tokens[4];
                        comenzi.add(new ComandaRedusa(tokens[1], Double.parseDouble(tokens[2]), Integer.parseInt(tokens[3]), client));
                    }
                    case "GIFT" -> {
                        String client = tokens[2];
                        comenzi.add(new ComandaGratuita(tokens[1], client));
                    }
                }
            }

            for (Comanda c : comenzi) {
                System.out.println(c.descriere());
            }

            String cmd;
            while (sc.hasNextLine()) {
                cmd = sc.nextLine().trim();
                if (cmd.equals("QUIT")) break;

                if (cmd.equals("STATS")) {
                    System.out.println();
                    System.out.println("--- STATS ---");
                    Map<String, Double> medii = comenzi.stream().collect(
                        Collectors.groupingBy(c -> {
                            if (c instanceof ComandaStandard) return "STANDARD";
                            if (c instanceof ComandaRedusa)   return "DISCOUNTED";
                            return "GIFT";
                        },
                        Collectors.averagingDouble(Comanda::pretFinal)
                    ));
                    for (String tip : List.of("STANDARD", "DISCOUNTED", "GIFT")) {
                        if (medii.containsKey(tip)) {
                            System.out.printf("%s: medie = %.2f lei%n", tip, medii.get(tip));
                        }
                    }

                } else if (cmd.startsWith("FILTER")) {
                    double threshold = Double.parseDouble(cmd.split(" ")[1]);
                    System.out.println();
                    System.out.printf("--- FILTER (>= %.2f) ---%n", threshold);
                    comenzi.stream()
                        .filter(c -> c.pretFinal() >= threshold)
                        .forEach(c -> System.out.println(c.descriereScurta()));

                } else if (cmd.equals("SORT")) {
                    System.out.println();
                    System.out.println("--- SORT (by client, then by pret) ---");
                    comenzi.stream()
                        .sorted(Comparator.comparing(Comanda::getClient)
                            .thenComparingDouble(Comanda::pretFinal))
                        .forEach(c -> System.out.println(c.descriereScurta()));

                } else if (cmd.equals("SPECIAL")) {
                    System.out.println();
                    System.out.println("--- SPECIAL (discount > 15%) ---");
                    comenzi.stream()
                        .filter(c -> c instanceof ComandaRedusa cr && cr.getDiscount() > 15)
                        .forEach(c -> System.out.println(c.descriereScurta()));
                }
            }
        }
    }
}
