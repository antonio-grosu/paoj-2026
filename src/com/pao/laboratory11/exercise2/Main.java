package com.pao.laboratory11.exercise2;

import com.pao.laboratory11.exercise1.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String line = nextNonEmpty(br);
            if (line == null) {
                return;
            }

            String[] p = line.split("\\s+");
            Transaction tx = new Transaction(
                    Integer.parseInt(p[0]),
                    Double.parseDouble(p[1]),
                    p[2],
                    p[3],
                    p[4]);
            entries.add(new Entry(tx, p[5]));
        }

        int q = Integer.parseInt(nextNonEmpty(br));
        for (int i = 0; i < q; i++) {
            String line = nextNonEmpty(br);
            if (line == null) {
                return;
            }

            String[] p = line.split("\\s+");
            switch (p[0]) {
                case "REPORT_MONTH":
                    reportMonth(entries, p[1]);
                    break;
                case "REPORT_ACCOUNT":
                    reportAccount(entries, p[1]);
                    break;
                case "TOP_CHANNELS":
                    topChannels(entries, Integer.parseInt(p[1]));
                    break;
                default:
                    break;
            }
        }
    }

    private static void reportMonth(List<Entry> entries, String month) {
        double total = 0.0;
        int count = 0;
        for (Entry e : entries) {
            if (e.tx.date().startsWith(month)) {
                total += e.tx.amount();
                count++;
            }
        }
        System.out.printf(Locale.US, "MONTH %s total=%.2f count=%d%n", month, total, count);
    }

    private static void reportAccount(List<Entry> entries, String account) {
        double total = 0.0;
        int count = 0;
        for (Entry e : entries) {
            if (e.accountId.equals(account)) {
                total += e.tx.amount();
                count++;
            }
        }
        System.out.printf(Locale.US, "ACCOUNT %s total=%.2f count=%d%n", account, total, count);
    }

    private static void topChannels(List<Entry> entries, int k) {
        if (entries.isEmpty()) {
            System.out.println("NONE");
            return;
        }

        Map<String, Long> counts = entries.stream()
                .collect(Collectors.groupingBy(e -> e.tx.channel(), Collectors.counting()));

        counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(k)
                .forEach(e -> System.out.println(e.getKey() + " " + e.getValue()));
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

    private static final class Entry {
        private final Transaction tx;
        private final String accountId;

        private Entry(Transaction tx, String accountId) {
            this.tx = tx;
            this.accountId = accountId;
        }
    }
}
