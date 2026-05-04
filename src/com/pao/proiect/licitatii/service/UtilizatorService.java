package com.pao.proiect.licitatii.service;

import com.pao.proiect.licitatii.exception.UtilizatorNegasitException;
import com.pao.proiect.licitatii.model.Cumparator;
import com.pao.proiect.licitatii.model.Utilizator;
import com.pao.proiect.licitatii.model.Vanzator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilizatorService {
    private static UtilizatorService instance;

    private final Map<Integer, Utilizator> utilizatori = new HashMap<>();
    private int nextId = 1;

    private UtilizatorService() {}

    public static UtilizatorService getInstance() {
        if (instance == null) {
            instance = new UtilizatorService();
        }
        return instance;
    }

    public Cumparator adaugaCumparator(String nume, String email, double buget) {
        Cumparator c = new Cumparator(nextId++, nume, email, buget);
        utilizatori.put(c.getId(), c);
        return c;
    }

    public Vanzator adaugaVanzator(String nume, String email, String iban) {
        Vanzator v = new Vanzator(nextId++, nume, email, iban);
        utilizatori.put(v.getId(), v);
        return v;
    }

    public Utilizator cautaDupaId(int id) throws UtilizatorNegasitException {
        Utilizator u = utilizatori.get(id);
        if (u == null) throw new UtilizatorNegasitException(id);
        return u;
    }

    public List<Utilizator> listeazaToti() {
        return new ArrayList<>(utilizatori.values());
    }

    public List<Cumparator> listeazaCumparatori() {
        List<Cumparator> rezultat = new ArrayList<>();
        for (Utilizator u : utilizatori.values()) {
            if (u instanceof Cumparator) rezultat.add((Cumparator) u);
        }
        return rezultat;
    }

    public List<Vanzator> listeazaVanzatori() {
        List<Vanzator> rezultat = new ArrayList<>();
        for (Utilizator u : utilizatori.values()) {
            if (u instanceof Vanzator) rezultat.add((Vanzator) u);
        }
        return rezultat;
    }

    public void stergeUtilizator(int id) throws UtilizatorNegasitException {
        if (!utilizatori.containsKey(id)) throw new UtilizatorNegasitException(id);
        utilizatori.remove(id);
    }

    public Map<String, List<Utilizator>> grupeazaDupaRol() {
        Map<String, List<Utilizator>> grupuri = new HashMap<>();
        for (Utilizator u : utilizatori.values()) {
            grupuri.computeIfAbsent(u.getRol(), k -> new ArrayList<>()).add(u);
        }
        return grupuri;
    }
}
