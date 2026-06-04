package com.pao.laboratory13.exercise2;

import com.pao.laboratory13.exercise1.ProtocolEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demo socket multi-client peste motorul de protocol din exercitiul 1.
 * Fiecare client are propria sesiune (ProtocolEngine izolat) rulata pe un thread separat.
 */
public class Main {
    private static final int PORT = 9000;

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : PORT;

        // Comenzi independente pentru doi clienti, pentru a demonstra sesiuni izolate.
        List<String> client1Commands = List.of("AUTH alice", "OPEN", "SEND hi", "HISTORY", "CLOSE");
        List<String> client2Commands = List.of("AUTH bob", "OPEN", "BROADCAST x", "SEND y", "HISTORY", "CLOSE");

        CountDownLatch clientsDone = new CountDownLatch(2);
        ExecutorService sessionPool = Executors.newCachedThreadPool();

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[SERVER] Listening on port " + port);
        Thread server = new Thread(() -> runServer(serverSocket, sessionPool, clientsDone), "server");
        server.start();

        Thread c1 = new Thread(() -> runClient("CLIENT-1", port, client1Commands), "client-1");
        Thread c2 = new Thread(() -> runClient("CLIENT-2", port, client2Commands), "client-2");
        c1.start();
        c2.start();

        c1.join();
        c2.join();
        clientsDone.await();

        // Inchiderea ServerSocket-ului deblocheaza accept() — interrupt() nu intrerupe I/O de socket.
        serverSocket.close();
        sessionPool.shutdown();
        server.join();
        System.out.println("[SERVER] All clients done. Shutting down.");
    }

    private static void runServer(ServerSocket serverSocket, ExecutorService sessionPool, CountDownLatch clientsDone) {
        try {
            while (!serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                sessionPool.submit(() -> handleSession(client, clientsDone));
            }
        } catch (IOException e) {
            // accept() arunca SocketException cand serverul e inchis controlat — normal.
        }
    }

    private static void handleSession(Socket socket, CountDownLatch clientsDone) {
        ProtocolEngine engine = new ProtocolEngine();
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                String response = engine.handle(line);
                out.println(response);
                if (engine.isClosed()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Session error: " + e.getMessage());
        } finally {
            clientsDone.countDown();
        }
    }

    private static void runClient(String name, int port, List<String> commands) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", port), 1000);
            socket.setSoTimeout(1000);
            System.out.println("[" + name + "] Connected");

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                for (String cmd : commands) {
                    out.println(cmd);
                    String response = in.readLine();
                    System.out.printf("[%s] >> %-14s => %s%n", name, cmd, response);
                }
            }
            System.out.println("[" + name + "] Disconnected");
        } catch (IOException e) {
            System.out.println("[" + name + "] Error: " + e.getMessage());
        }
    }
}
