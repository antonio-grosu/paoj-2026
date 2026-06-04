package com.pao.laboratory14.exercise1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collector;

public class Main {
    public static void main(String[] args) {
        try {
            run();
        } catch (IOException e) {
            System.out.println("ERR IO");
        }
    }

    private static void run() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String first = nextNonEmpty(br);
        if (first == null) {
            return;
        }

        int n = Integer.parseInt(first);
        List<Bilet> bilete = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String[] p = nextNonEmpty(br).split("\\s+");
            bilete.add(new Bilet(
                    Integer.parseInt(p[0]),
                    p[1],
                    TipBilet.valueOf(p[2]),
                    Double.parseDouble(p[3])));
        }

        String comanda = nextNonEmpty(br);
        RaportVanzari raport = bilete.stream().collect(raportColector());

        printRaportSimplu(raport);
        if ("RAPORT_COMPLET".equals(comanda)) {
            System.out.println("---");
            System.out.printf(Locale.US, "Total: %.2f RON%n", raport.totalGlobal());
            System.out.printf(Locale.US, "Medie: %.2f RON%n", raport.medieGlobala());
            System.out.println("Cel mai popular: " + raport.tipCelMaiPopular());
        }
    }

    private static void printRaportSimplu(RaportVanzari raport) {
        for (TipBilet tip : TipBilet.values()) {
            Long count = raport.numarPerTip().get(tip);
            if (count == null) {
                continue;
            }
            System.out.printf(Locale.US, "%s: count=%d incasari=%.2f RON%n",
                    tip, count, raport.incasariPerTip().get(tip));
        }
    }

    private static String nextNonEmpty(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                return line.trim();
            }
        }
        return null;
    }

    /**
     * Colector custom care agrega biletele intr-un RaportVanzari imutabil.
     * Acumulatorul intern e un EnumMap<TipBilet, double[]>: [0]=count, [1]=suma incasari.
     */
    private static Collector<Bilet, ?, RaportVanzari> raportColector() {
        return Collector.of(
                () -> new EnumMap<TipBilet, double[]>(TipBilet.class),
                (acc, bilet) -> {
                    double[] celula = acc.computeIfAbsent(bilet.tip(), t -> new double[2]);
                    celula[0]++;
                    celula[1] += bilet.pret();
                },
                (a, b) -> {
                    b.forEach((tip, celula) -> a.merge(tip, celula, (x, y) -> {
                        x[0] += y[0];
                        x[1] += y[1];
                        return x;
                    }));
                    return a;
                },
                Main::finiseaza);
    }

    private static RaportVanzari finiseaza(Map<TipBilet, double[]> acc) {
        Map<TipBilet, Long> numarPerTip = new EnumMap<>(TipBilet.class);
        Map<TipBilet, Double> incasariPerTip = new EnumMap<>(TipBilet.class);
        double totalGlobal = 0.0;
        long totalBilete = 0;

        for (Map.Entry<TipBilet, double[]> e : acc.entrySet()) {
            long count = (long) e.getValue()[0];
            double incasari = e.getValue()[1];
            numarPerTip.put(e.getKey(), count);
            incasariPerTip.put(e.getKey(), incasari);
            totalGlobal += incasari;
            totalBilete += count;
        }

        double medieGlobala = totalBilete == 0 ? 0.0 : totalGlobal / totalBilete;
        // Cel mai popular: count maxim; la egalitate, primul tip in ordine alfabetica.
        TipBilet tipCelMaiPopular = numarPerTip.entrySet().stream()
                .min(Comparator.comparingLong((Map.Entry<TipBilet, Long> e) -> -e.getValue())
                        .thenComparing(e -> e.getKey().name()))
                .map(Map.Entry::getKey)
                .orElse(null);

        return new RaportVanzari(numarPerTip, incasariPerTip, totalGlobal, medieGlobala, tipCelMaiPopular);
    }

    private record Bilet(int id, String eveniment, TipBilet tip, double pret) {
    }

    /** Raport imutabil produs de finisher-ul colectorului. */
    private static final class RaportVanzari {
        private final Map<TipBilet, Long> numarPerTip;
        private final Map<TipBilet, Double> incasariPerTip;
        private final double totalGlobal;
        private final double medieGlobala;
        private final TipBilet tipCelMaiPopular;

        RaportVanzari(Map<TipBilet, Long> numarPerTip, Map<TipBilet, Double> incasariPerTip,
                      double totalGlobal, double medieGlobala, TipBilet tipCelMaiPopular) {
            this.numarPerTip = Collections.unmodifiableMap(new EnumMap<>(numarPerTip));
            this.incasariPerTip = Collections.unmodifiableMap(new EnumMap<>(incasariPerTip));
            this.totalGlobal = totalGlobal;
            this.medieGlobala = medieGlobala;
            this.tipCelMaiPopular = tipCelMaiPopular;
        }

        Map<TipBilet, Long> numarPerTip() { return numarPerTip; }
        Map<TipBilet, Double> incasariPerTip() { return incasariPerTip; }
        double totalGlobal() { return totalGlobal; }
        double medieGlobala() { return medieGlobala; }
        TipBilet tipCelMaiPopular() { return tipCelMaiPopular; }
    }
}
