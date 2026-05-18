package com.pao.laboratory10.exercise2;

import com.pao.laboratory10.exercise1.Tranzactie;
import com.pao.laboratory10.exercise1.TipTranzactie;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());
        List<Tranzactie> lista = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String[] p = scanner.nextLine().trim().split(" ");
            lista.add(new Tranzactie(
                    Integer.parseInt(p[0]),
                    Double.parseDouble(p[1]),
                    p[2],
                    TipTranzactie.valueOf(p[3])
            ));
        }

        while (scanner.hasNextLine()) {
            String linie = scanner.nextLine().trim();
            if (linie.isEmpty()) continue;

            if (linie.equals("UNIQUE_IDS")) {
                LinkedHashSet<Integer> ids = new LinkedHashSet<>();
                for (Tranzactie t : lista) ids.add(t.getId());
                System.out.println("IDs unice (" + ids.size() + "): " + ids);

            } else if (linie.equals("MONTHLY_REPORT")) {
                TreeMap<String, double[]> raport = new TreeMap<>();
                for (Tranzactie t : lista) {
                    String luna = t.getData().substring(0, 7);
                    raport.putIfAbsent(luna, new double[]{0.0, 0.0});
                    if (t.getTip() == TipTranzactie.CREDIT) {
                        raport.get(luna)[0] += t.getSuma();
                    } else {
                        raport.get(luna)[1] += t.getSuma();
                    }
                }
                for (Map.Entry<String, double[]> e : raport.entrySet()) {
                    System.out.printf("%s: CREDIT %.2f RON, DEBIT %.2f RON%n",
                            e.getKey(), e.getValue()[0], e.getValue()[1]);
                }

            } else if (linie.startsWith("TOP ")) {
                int k = Integer.parseInt(linie.split(" ")[1]);
                List<Tranzactie> copie = new ArrayList<>(lista);
                copie.sort(Comparator.comparingDouble(Tranzactie::getSuma).reversed());
                System.out.println("Top " + k + ":");
                for (int i = 0; i < k && i < copie.size(); i++) {
                    System.out.println(copie.get(i));
                }

            } else if (linie.equals("SORT_ASC")) {
                lista.sort(Comparator.comparingDouble(Tranzactie::getSuma));
                for (Tranzactie t : lista) System.out.println(t);

            } else if (linie.equals("SORT_DESC")) {
                lista.sort(Comparator.comparingDouble(Tranzactie::getSuma).reversed());
                for (Tranzactie t : lista) System.out.println(t);

            } else if (linie.equals("REVERSE")) {
                Collections.reverse(lista);
                for (Tranzactie t : lista) System.out.println(t);

            } else if (linie.equals("MIN_MAX")) {
                Tranzactie min = Collections.min(lista, Comparator.comparingDouble(Tranzactie::getSuma));
                Tranzactie max = Collections.max(lista, Comparator.comparingDouble(Tranzactie::getSuma));
                System.out.println("MIN: " + min);
                System.out.println("MAX: " + max);

            } else if (linie.equals("CME_DEMO")) {
                try {
                    for (Tranzactie t : lista) {
                        lista.remove(t);
                    }
                } catch (ConcurrentModificationException e) {
                    System.out.println("ConcurrentModificationException prins: modificare in iteratie detectata.");
                }
            }
        }
        scanner.close();
    }
}
