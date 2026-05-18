package com.pao.laboratory12.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public final class AuditService {

    private static AuditService instance;
    private static final String AUDIT_FILE = "audit.csv";

    private final ReentrantLock lock = new ReentrantLock();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private AuditService() {}

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void log(String actionName) {
        lock.lock();
        try (PrintWriter pw = new PrintWriter(new FileWriter(AUDIT_FILE, true))) {
            pw.println(actionName + "," + LocalDateTime.now().format(formatter));
        } catch (IOException e) {
            System.err.println("[AUDIT] Eroare la scriere: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
