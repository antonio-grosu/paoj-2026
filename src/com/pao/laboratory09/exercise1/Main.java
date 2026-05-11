package com.pao.laboratory09.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex1.ser";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());
        List<Tranzactie> tranzactii = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String[] parts = scanner.nextLine().trim().split(" ");
            int id = Integer.parseInt(parts[0]);
            double suma = Double.parseDouble(parts[1]);
            String data = parts[2];
            String contSursa = parts[3];
            String contDestinatie = parts[4];
            TipTranzactie tip = TipTranzactie.valueOf(parts[5]);
            Tranzactie t = new Tranzactie(id, suma, data, contSursa, contDestinatie, tip);
            t.setNote("procesat");
            tranzactii.add(t);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OUTPUT_FILE))) {
            oos.writeObject(tranzactii);
        }

        List<Tranzactie> deserializate;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(OUTPUT_FILE))) {
            @SuppressWarnings("unchecked")
            List<Tranzactie> tmp = (List<Tranzactie>) ois.readObject();
            deserializate = tmp;
        }

        while (scanner.hasNextLine()) {
            String linie = scanner.nextLine().trim();
            if (linie.isEmpty()) continue;

            if (linie.equals("LIST")) {
                for (Tranzactie t : deserializate) {
                    System.out.println(t);
                }
            } else if (linie.startsWith("FILTER ")) {
                String prefix = linie.substring(7).trim();
                List<Tranzactie> filtrate = new ArrayList<>();
                for (Tranzactie t : deserializate) {
                    if (t.getData().startsWith(prefix)) filtrate.add(t);
                }
                if (filtrate.isEmpty()) {
                    System.out.println("Niciun rezultat.");
                } else {
                    for (Tranzactie t : filtrate) System.out.println(t);
                }
            } else if (linie.startsWith("NOTE ")) {
                int id = Integer.parseInt(linie.substring(5).trim());
                Tranzactie found = null;
                for (Tranzactie t : deserializate) {
                    if (t.getId() == id) { found = t; break; }
                }
                if (found == null) {
                    System.out.println("NOTE[" + id + "]: not found");
                } else {
                    System.out.println("NOTE[" + id + "]: " + found.getNote());
                }
            }
        }
        scanner.close();
    }
}
