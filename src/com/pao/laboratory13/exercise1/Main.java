package com.pao.laboratory13.exercise1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        int q = Integer.parseInt(first);
        ProtocolEngine engine = new ProtocolEngine();
        for (int i = 0; i < q; i++) {
            String line = nextNonEmpty(br);
            if (line == null) {
                return;
            }
            System.out.println(engine.handle(line));
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
}
