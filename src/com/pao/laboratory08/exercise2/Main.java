package com.pao.laboratory08.exercise2;

import com.pao.laboratory08.exercise1.Adresa;
import com.pao.laboratory08.exercise1.Student;

import java.io.*;
import java.util.*;

public class Main {
    private static final String FILE_PATH = "src/com/pao/laboratory08/tests/studenti.txt";

    public static void main(String[] args) throws Exception {
        List<Student> studenti = readStudenti();

        int prag;
        try (Scanner scanner = new Scanner(System.in)) {
            prag = Integer.parseInt(scanner.nextLine().trim());
        }

        List<Student> filtrati = new ArrayList<>();
        for (Student s : studenti) {
            if (s.getVarsta() >= prag) filtrati.add(s);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("rezultate.txt"))) {
            for (Student s : filtrati) {
                bw.write(s.toString());
                bw.newLine();
            }
        }

        System.out.println("Filtru: varsta >= " + prag);
        System.out.println("Rezultate: " + filtrati.size() + " studenti");
        System.out.println();
        for (Student s : filtrati) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("Scris in: rezultate.txt");
    }

    private static List<Student> readStudenti() throws IOException {
        List<Student> studenti = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
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
        }
        return studenti;
    }
}
