package com.pao.laboratory08.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String FILE_PATH = "src/com/pao/laboratory08/tests/studenti.txt";

    public static void main(String[] args) throws Exception {
        List<Student> studenti = readStudenti();
        String comanda;
        try (Scanner scanner = new Scanner(System.in)) {
            comanda = scanner.nextLine().trim();
        }

        if (comanda.equals("PRINT")) {
            for (Student s : studenti) {
                System.out.println(s);
            }
        } else if (comanda.startsWith("SHALLOW ")) {
            String nume = comanda.substring(8).trim();
            Student original = findByName(studenti, nume);
            Student clona = original.shallowClone();
            clona.getAdresa().setOras("MODIFICAT");
            System.out.println("Original: " + original);
            System.out.println("Clona: " + clona);
        } else if (comanda.startsWith("DEEP ")) {
            String nume = comanda.substring(5).trim();
            Student original = findByName(studenti, nume);
            Student clona = original.deepClone();
            clona.getAdresa().setOras("MODIFICAT");
            System.out.println("Original: " + original);
            System.out.println("Clona: " + clona);
        }
    }

    private static List<Student> readStudenti() throws IOException {
        List<Student> studenti = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));
        String linie;
        while ((linie = br.readLine()) != null) {
            linie = linie.trim();
            if (linie.isEmpty()) continue;
            String[] parts = linie.split(",");
            String nume = parts[0].trim();
            int varsta = Integer.parseInt(parts[1].trim());
            String oras = parts[2].trim();
            String strada = parts[3].trim();
            studenti.add(new Student(nume, varsta, new Adresa(oras, strada)));
        }
        br.close();
        return studenti;
    }

    private static Student findByName(List<Student> studenti, String nume) {
        for (Student s : studenti) {
            if (s.getNume().equals(nume)) return s;
        }
        return null;
    }
}
