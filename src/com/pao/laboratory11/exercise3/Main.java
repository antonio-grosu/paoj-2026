package com.pao.laboratory11.exercise3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {

    static final class Transaction {
        final int id;
        final double amount;
        final String date;
        final String country;
        final String channel;

        Transaction(int id, double amount, String date, String country, String channel) {
            this.id = id;
            this.amount = amount;
            this.date = date;
            this.country = country;
            this.channel = channel;
        }

        @Override
        public String toString() {
            return String.format("[%d] %s %s %.2f", id, date, channel, amount);
        }
    }

    static final class Snapshot {
        private final Map<String, Long> countByCountry;
        private final Map<String, Long> countByChannel;
        private final double totalAmount;
        private final List<Transaction> topTransactions;

        Snapshot(Map<String, Long> byCountry, Map<String, Long> byChannel,
                 double total, List<Transaction> top) {
            this.countByCountry = Collections.unmodifiableMap(new HashMap<>(byCountry));
            this.countByChannel = Collections.unmodifiableMap(new HashMap<>(byChannel));
            this.totalAmount = total;
            this.topTransactions = Collections.unmodifiableList(new ArrayList<>(top));
        }

        Map<String, Long> getCountByCountry() { return countByCountry; }
        Map<String, Long> getCountByChannel() { return countByChannel; }
        double getTotalAmount() { return totalAmount; }
        List<Transaction> getTopTransactions() { return topTransactions; }
    }

    static Collector<Transaction, ?, Snapshot> toSnapshot(int topN) {
        class Agg {
            final Map<String, Long> byCountry = new HashMap<>();
            final Map<String, Long> byChannel = new HashMap<>();
            double total = 0.0;
            final List<Transaction> all = new ArrayList<>();

            void accept(Transaction tx) {
                byCountry.merge(tx.country, 1L, Long::sum);
                byChannel.merge(tx.channel, 1L, Long::sum);
                total += tx.amount;
                all.add(tx);
            }

            Agg combine(Agg other) {
                other.byCountry.forEach((k, v) -> byCountry.merge(k, v, Long::sum));
                other.byChannel.forEach((k, v) -> byChannel.merge(k, v, Long::sum));
                total += other.total;
                all.addAll(other.all);
                return this;
            }

            Snapshot finish() {
                List<Transaction> top = all.stream()
                        .sorted(Comparator.comparingDouble((Transaction t) -> t.amount).reversed()
                                .thenComparingInt(t -> t.id))
                        .limit(topN)
                        .collect(Collectors.toList());
                return new Snapshot(byCountry, byChannel, total, top);
            }
        }

        return Collector.of(Agg::new, Agg::accept, Agg::combine, Agg::finish);
    }

    public static void main(String[] args) {
        List<Transaction> data = Arrays.asList(
                new Transaction(1,  1200.00, "2026-05-01", "RO", "WEB"),
                new Transaction(2,   300.00, "2026-05-02", "RU", "ATM"),
                new Transaction(3,  6000.00, "2026-05-03", "NG", "APP"),
                new Transaction(4,   500.00, "2026-06-01", "RO", "WEB"),
                new Transaction(5,    80.00, "2026-06-02", "KP", "CRYPTO"),
                new Transaction(6,  2500.00, "2026-06-03", "RO", "POS"),
                new Transaction(7,   150.00, "2026-07-01", "IR", "ATM"),
                new Transaction(8,  3300.00, "2026-07-02", "RO", "APP"),
                new Transaction(9,   900.00, "2026-07-03", "SY", "WEB"),
                new Transaction(10,  450.00, "2026-07-04", "RO", "ATM")
        );

        Snapshot snap = data.stream().collect(toSnapshot(5));

        System.out.println("--- Top 5 tranzactii ---");
        snap.getTopTransactions().forEach(System.out::println);

        System.out.printf(Locale.US, "Total: %.2f RON%n", snap.getTotalAmount());

        System.out.println("--- Tranzactii pe tara ---");
        snap.getCountByCountry().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        System.out.println("--- Tranzactii pe canal ---");
        snap.getCountByChannel().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}
