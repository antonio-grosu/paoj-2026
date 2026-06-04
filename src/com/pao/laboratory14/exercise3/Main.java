package com.pao.laboratory14.exercise3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Bonus — Alocare Automata de Sali pentru Evenimente
 * <p>
 * Problema clasica de interviu: date N evenimente cu intervale [start, end],
 * gaseste numarul minim de sali necesare si atribuie fiecare eveniment la o sala.
 * <p>
 * Doua variante demonstrate:
 * Varianta 1 — greedy simplu O(N^2): prima sala disponibila
 * Varianta 2 — PriorityQueue O(N log N): min-heap de ore de final
 */
public class Main {

    record Eveniment(String nume, int startMin, int endMin) {
    }

    /**
     * Converteste "HH:MM" in minute intregi de la miezul noptii.
     */
    private static int toMin(String hhmm) {
        String[] p = hhmm.split(":");
        return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
    }

    /**
     * Converteste minute intregi inapoi in "HH:MM".
     */
    private static String toHHMM(int min) {
        return String.format("%02d:%02d", min / 60, min % 60);
    }

    public static void main(String[] args) {
        List<Eveniment> evenimente = new ArrayList<>(List.of(
                new Eveniment("ConcertRock",  toMin("09:00"), toMin("10:30")),
                new Eveniment("Conferinta",   toMin("09:30"), toMin("11:00")),
                new Eveniment("Workshop",     toMin("10:00"), toMin("12:00")),
                new Eveniment("Teatru",       toMin("10:45"), toMin("12:30")),
                new Eveniment("Stand-up",     toMin("12:00"), toMin("13:00")),
                new Eveniment("Balet",        toMin("12:30"), toMin("14:00")),
                new Eveniment("Opera",        toMin("13:30"), toMin("16:00")),
                new Eveniment("GalaFilm",     toMin("15:00"), toMin("17:30"))
        ));

        // Sortarea dupa ora de start e premisa ambelor variante.
        evenimente.sort(Comparator.comparingInt(Eveniment::startMin));

        int saliV1 = greedyOnSquared(evenimente);
        int saliV2 = priorityQueue(evenimente);

        System.out.println("\nNumar minim de sali (greedy O(N^2)):       " + saliV1);
        System.out.println("Numar minim de sali (PriorityQueue O(NlogN)): " + saliV2);
        System.out.println(saliV1 == saliV2 ? "Cele doua variante coincid." : "DISCREPANTA intre variante!");
    }

    /**
     * Varianta 1 — greedy O(N^2): pentru fiecare eveniment, cauta liniar prima sala
     * a carei ora de final e <= ora de start curenta (libera). Daca nu exista, deschide o sala noua.
     */
    private static int greedyOnSquared(List<Eveniment> evenimente) {
        System.out.println("=== Varianta 1: greedy O(N^2) ===");
        List<Integer> oreFinalSali = new ArrayList<>();   // oreFinalSali.get(i) = endMin al salii i+1

        for (Eveniment ev : evenimente) {
            int sala = -1;
            for (int i = 0; i < oreFinalSali.size(); i++) {
                if (oreFinalSali.get(i) <= ev.startMin()) {
                    oreFinalSali.set(i, ev.endMin());
                    sala = i + 1;
                    break;
                }
            }
            if (sala == -1) {
                oreFinalSali.add(ev.endMin());
                sala = oreFinalSali.size();
            }
            System.out.printf("%-14s (%s - %s)  ->  Sala #%d%n",
                    ev.nume(), toHHMM(ev.startMin()), toHHMM(ev.endMin()), sala);
        }
        return oreFinalSali.size();
    }

    /**
     * Varianta 2 — PriorityQueue O(N log N): min-heap cu orele de final ale salilor ocupate.
     * Daca cea mai devreme sala eliberata e libera la startul curent, o reutilizam; altfel deschidem una noua.
     */
    private static int priorityQueue(List<Eveniment> evenimente) {
        PriorityQueue<Integer> oreFinalSali = new PriorityQueue<>();
        for (Eveniment ev : evenimente) {
            if (!oreFinalSali.isEmpty() && oreFinalSali.peek() <= ev.startMin()) {
                oreFinalSali.poll();
            }
            oreFinalSali.offer(ev.endMin());
        }
        return oreFinalSali.size();
    }
}

