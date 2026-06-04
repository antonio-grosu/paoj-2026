package com.pao.laboratory14.exercise2;

import com.pao.laboratory14.exercise1.TipBilet;
import com.pao.laboratory14.exercise2.model.Eveniment;
import com.pao.laboratory14.exercise2.repository.EvenimentRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            run();
        } catch (SQLException | IOException e) {
            System.out.println("ERR " + e.getMessage());
        }
    }

    private static void run() throws SQLException, IOException {
        EvenimentRepository repo = new EvenimentRepository();
        repo.initSchema();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] p = line.trim().split("\\s+");
            switch (p[0]) {
                case "ADD":
                    add(repo, p);
                    break;
                case "LIST":
                    for (Eveniment ev : repo.findAll()) {
                        System.out.println(ev);
                    }
                    break;
                case "DELETE":
                    delete(repo, Integer.parseInt(p[1]));
                    break;
                case "COUNT":
                    System.out.println("Total: " + repo.count());
                    break;
                default:
                    break;
            }
        }
    }

    private static void add(EvenimentRepository repo, String[] p) throws SQLException {
        Eveniment ev = new Eveniment(p[1], p[2], Integer.parseInt(p[3]), TipBilet.valueOf(p[4]));
        repo.save(ev);
        System.out.println("Adaugat: [" + ev.getId() + "] " + ev.getNume());
    }

    private static void delete(EvenimentRepository repo, int id) throws SQLException {
        if (repo.deleteImpl(id) > 0) {
            System.out.println("Sters: " + id);
        } else {
            System.out.println("Nu exista: " + id);
        }
    }
}
