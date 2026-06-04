package com.pao.laboratory11.exercise1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class Main {
    private static final Set<String> HIGH_RISK_COUNTRIES =
            new HashSet<>(Arrays.asList("RU", "NG", "IR", "KP", "SY"));

    private static final Set<String> SUSPICIOUS_CHANNELS =
            new HashSet<>(Arrays.asList("WEB", "APP", "CRYPTO"));

    private static final int FLAG_THRESHOLD = 60;

    private static final Map<String, Integer> CHANNEL_SCORE = new HashMap<>();

    static {
        CHANNEL_SCORE.put("WEB", 15);
        CHANNEL_SCORE.put("APP", 10);
        CHANNEL_SCORE.put("CRYPTO", 30);
        CHANNEL_SCORE.put("POS", 5);
        CHANNEL_SCORE.put("ATM", 0);
    }

    // Reguli elementare (partea A): fiecare conditie de risc ca Predicate<Transaction>.
    private static final Predicate<Transaction> amountOverThreshold = tx -> tx.amount() >= 1000.0;
    private static final Predicate<Transaction> countryInRisk = tx -> HIGH_RISK_COUNTRIES.contains(tx.country());
    private static final Predicate<Transaction> channelSuspicious = tx -> SUSPICIOUS_CHANNELS.contains(tx.channel());

    // Compozitie (partea B): o tranzactie atinge pragul doar daca incalca o regula
    // elementara, deci compunem precondition-ul cu testul numeric pe scor.
    private static final Predicate<Transaction> matchesRiskRule =
            amountOverThreshold.or(countryInRisk).or(channelSuspicious);

    private static final Predicate<Transaction> isFlagged =
            matchesRiskRule.and(tx -> riskScore(tx) >= FLAG_THRESHOLD);

    private static final Comparator<Transaction> BY_RISK_DESC_THEN_ID_ASC =
            Comparator.comparingInt(Main::riskScore).reversed().thenComparingInt(Transaction::id);

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException e) {
            System.out.println("ERR IO");
        }
    }

    private static void run() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String first = readNonEmptyLine(br);
        if (first == null) {
            return;
        }

        int n = Integer.parseInt(first);
        Map<Integer, Transaction> byId = new HashMap<>();
        List<Transaction> all = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String line = readNonEmptyLine(br);
            if (line == null) {
                return;
            }

            String[] tok = line.split("\\s+");
            Transaction tx = new Transaction(
                    Integer.parseInt(tok[0]),
                    Double.parseDouble(tok[1]),
                    tok[2],
                    tok[3].toUpperCase(),
                    tok[4].toUpperCase());

            byId.put(tx.id(), tx);
            all.add(tx);
        }

        int q = Integer.parseInt(readNonEmptyLine(br));
        for (int i = 0; i < q; i++) {
            String cmdLine = readNonEmptyLine(br);
            if (cmdLine == null) {
                return;
            }

            String[] cmd = cmdLine.split("\\s+");
            switch (cmd[0].toUpperCase()) {
                case "CHECK":
                    check(byId, Integer.parseInt(cmd[1]));
                    break;
                case "LIST_FLAGGED":
                    listFlagged(all);
                    break;
                case "TOP_RISK":
                    topRisk(all, Integer.parseInt(cmd[1]));
                    break;
                default:
                    System.out.println("ERR UNKNOWN_COMMAND");
                    break;
            }
        }
    }

    private static void check(Map<Integer, Transaction> byId, int id) {
        Transaction tx = byId.get(id);
        if (tx == null) {
            System.out.println("CHECK " + id + " => NOT_FOUND");
            return;
        }
        int score = riskScore(tx);
        System.out.println("CHECK " + id + " => " + verdict(score) + " score=" + score);
    }

    private static void listFlagged(List<Transaction> all) {
        List<Transaction> flagged = new ArrayList<>();
        for (Transaction tx : all) {
            if (isFlagged.test(tx)) {
                flagged.add(tx);
            }
        }
        if (flagged.isEmpty()) {
            System.out.println("NONE");
            return;
        }
        flagged.sort(BY_RISK_DESC_THEN_ID_ASC);
        for (Transaction tx : flagged) {
            System.out.println(formatRiskLine(tx));
        }
    }

    private static void topRisk(List<Transaction> all, int k) {
        List<Transaction> ranked = new ArrayList<>(all);
        ranked.sort(BY_RISK_DESC_THEN_ID_ASC);
        int limit = Math.max(0, Math.min(k, ranked.size()));
        for (int i = 0; i < limit; i++) {
            System.out.println(formatRiskLine(ranked.get(i)));
        }
    }

    private static String readNonEmptyLine(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                return line.trim();
            }
        }
        return null;
    }

    private static int riskScore(Transaction tx) {
        int score = amountScore(tx.amount());
        if (countryInRisk.test(tx)) {
            score += 25;
        }
        score += CHANNEL_SCORE.getOrDefault(tx.channel(), 0);
        return score;
    }

    private static int amountScore(double amount) {
        int score;
        if (amount >= 5000.0) {
            score = 70;
        } else if (amount >= 1000.0) {
            score = 40;
        } else if (amount >= 500.0) {
            score = 20;
        } else {
            score = 0;
        }
        if (amount <= 100.0) {
            score += 5;
        }
        return score;
    }

    private static String verdict(int score) {
        return score >= FLAG_THRESHOLD ? "FLAG" : "ALLOW";
    }

    private static String formatRiskLine(Transaction tx) {
        int score = riskScore(tx);
        return "[" + tx.id() + "] " + verdict(score) + " score=" + score;
    }
}
