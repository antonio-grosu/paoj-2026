package com.pao.laboratory10.exercise1;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        LinkedList<Tranzactie> coada = new LinkedList<>();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String linie = scanner.nextLine().trim();
            if (linie.isEmpty()) continue;

            if (linie.startsWith("ENQUEUE ")) {
                String[] p = linie.split(" ");
                coada.addLast(new Tranzactie(
                        Integer.parseInt(p[1]),
                        Double.parseDouble(p[2]),
                        p[3],
                        TipTranzactie.valueOf(p[4])
                ));
            } else if (linie.equals("DEQUEUE")) {
                if (coada.isEmpty()) {
                    System.out.println("Coada goala.");
                } else {
                    System.out.println("Procesat: " + coada.removeFirst());
                }
            } else if (linie.startsWith("PUSH ")) {
                String[] p = linie.split(" ");
                coada.addFirst(new Tranzactie(
                        Integer.parseInt(p[1]),
                        Double.parseDouble(p[2]),
                        p[3],
                        TipTranzactie.valueOf(p[4])
                ));
            } else if (linie.equals("POP")) {
                if (coada.isEmpty()) {
                    System.out.println("Coada goala.");
                } else {
                    System.out.println("Extras: " + coada.removeFirst());
                }
            } else if (linie.equals("REMOVE_DEBIT")) {
                int count = 0;
                Iterator<Tranzactie> itr = coada.iterator();
                while (itr.hasNext()) {
                    if (itr.next().getTip() == TipTranzactie.DEBIT) {
                        itr.remove();
                        count++;
                    }
                }
                System.out.println("Eliminat " + count + " tranzactii DEBIT.");
            } else if (linie.startsWith("REMOVE_BELOW ")) {
                double threshold = Double.parseDouble(linie.split(" ")[1]);
                int count = 0;
                Iterator<Tranzactie> itr = coada.iterator();
                while (itr.hasNext()) {
                    if (itr.next().getSuma() < threshold) {
                        itr.remove();
                        count++;
                    }
                }
                System.out.printf("Eliminat %d tranzactii sub %.2f RON.%n", count, threshold);
            } else if (linie.equals("PRINT")) {
                for (Tranzactie t : coada) {
                    System.out.println(t);
                }
            } else if (linie.equals("SIZE")) {
                System.out.println("Dimensiune coada: " + coada.size());
            }
        }
        scanner.close();
    }
}
