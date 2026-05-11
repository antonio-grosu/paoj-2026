package com.pao.laboratory09.exercise2;

import com.pao.laboratory09.exercise1.TipTranzactie;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex2.bin";
    private static final int RECORD_SIZE = 32;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());

        int[] ids = new int[n];
        double[] sume = new double[n];
        String[] date = new String[n];
        TipTranzactie[] tipuri = new TipTranzactie[n];

        for (int i = 0; i < n; i++) {
            String[] parts = scanner.nextLine().trim().split(" ");
            ids[i] = Integer.parseInt(parts[0]);
            sume[i] = Double.parseDouble(parts[1]);
            date[i] = parts[2];
            tipuri[i] = TipTranzactie.valueOf(parts[3]);
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(OUTPUT_FILE))) {
            for (int i = 0; i < n; i++) {
                byte[] idBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ids[i]).array();
                dos.write(idBytes);

                byte[] sumaBytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(sume[i]).array();
                dos.write(sumaBytes);

                byte[] dataBytes = new byte[10];
                Arrays.fill(dataBytes, (byte) ' ');
                byte[] src = date[i].getBytes("ASCII");
                System.arraycopy(src, 0, dataBytes, 0, src.length);
                dos.write(dataBytes);

                dos.write(tipuri[i] == TipTranzactie.CREDIT ? 0 : 1);
                dos.write(0);
                dos.write(new byte[8]);
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(OUTPUT_FILE, "rw")) {
            while (scanner.hasNextLine()) {
                String linie = scanner.nextLine().trim();
                if (linie.isEmpty()) continue;

                if (linie.startsWith("READ ")) {
                    int idx = Integer.parseInt(linie.substring(5).trim());
                    System.out.println(readRecord(raf, idx));
                } else if (linie.startsWith("UPDATE ")) {
                    String[] parts = linie.split(" ");
                    int idx = Integer.parseInt(parts[1]);
                    String status = parts[2];
                    byte statusByte = statusToByte(status);
                    raf.seek((long) idx * RECORD_SIZE + 23);
                    raf.write(statusByte);
                    System.out.println("Updated [" + idx + "]: " + status);
                } else if (linie.equals("PRINT_ALL")) {
                    long count = raf.length() / RECORD_SIZE;
                    for (int i = 0; i < count; i++) {
                        System.out.println(readRecord(raf, i));
                    }
                }
            }
        }
        scanner.close();
    }

    private static String readRecord(RandomAccessFile raf, int idx) throws IOException {
        raf.seek((long) idx * RECORD_SIZE);
        byte[] bytes = new byte[RECORD_SIZE];
        raf.readFully(bytes);
        ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        int id = buf.getInt(0);
        double suma = buf.getDouble(4);
        String data = new String(bytes, 12, 10, "ASCII").trim();
        int tipByte = bytes[22] & 0xFF;
        int statusByte = bytes[23] & 0xFF;

        String tip = tipByte == 0 ? "CREDIT" : "DEBIT";
        String status = statusToString(statusByte);

        return String.format("[%d] id=%d data=%s tip=%s suma=%.2f RON status=%s",
                idx, id, data, tip, suma, status);
    }

    private static byte statusToByte(String status) {
        switch (status) {
            case "PENDING": return 0;
            case "PROCESSED": return 1;
            case "REJECTED": return 2;
            default: return 0;
        }
    }

    private static String statusToString(int b) {
        switch (b) {
            case 1: return "PROCESSED";
            case 2: return "REJECTED";
            default: return "PENDING";
        }
    }
}
