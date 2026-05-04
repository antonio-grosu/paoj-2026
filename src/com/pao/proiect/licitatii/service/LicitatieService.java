package com.pao.proiect.licitatii.service;

import com.pao.proiect.licitatii.exception.LicitatieInchisaException;
import com.pao.proiect.licitatii.exception.OfertaInsuficientaException;
import com.pao.proiect.licitatii.model.Cumparator;
import com.pao.proiect.licitatii.model.Licitatie;
import com.pao.proiect.licitatii.model.Oferta;
import com.pao.proiect.licitatii.model.Produs;
import com.pao.proiect.licitatii.model.StareLicitatie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class LicitatieService {
    private static LicitatieService instance;

    private final Map<Integer, Licitatie> licitatii = new HashMap<>();
    private int nextLicitatieId = 1;
    private int nextOfertaId = 1;

    private LicitatieService() {}

    public static LicitatieService getInstance() {
        if (instance == null) {
            instance = new LicitatieService();
        }
        return instance;
    }

    public Licitatie creeazaLicitatie(Produs produs, LocalDateTime dataStart, LocalDateTime dataFinal) {
        Licitatie l = new Licitatie(nextLicitatieId++, produs, dataStart, dataFinal);
        licitatii.put(l.getId(), l);
        return l;
    }

    public Oferta plaseazaOferta(int idLicitatie, Cumparator cumparator, double suma)
            throws LicitatieInchisaException, OfertaInsuficientaException {
        Licitatie licitatie = cautaDupaId(idLicitatie);

        if (licitatie.getStare() != StareLicitatie.ACTIVA) {
            throw new LicitatieInchisaException(idLicitatie);
        }

        double pretMinim = licitatie.getProdus().getPretMinim();
        if (suma < pretMinim) {
            throw new OfertaInsuficientaException(suma, pretMinim);
        }

        Oferta oferta = new Oferta(nextOfertaId++, cumparator, suma);
        licitatie.adaugaOferta(oferta);
        return oferta;
    }

    public void inchideLicitatie(int idLicitatie) {
        Licitatie l = cautaDupaId(idLicitatie);
        l.setStare(StareLicitatie.INCHISA);
    }

    public void anuleazaLicitatie(int idLicitatie) {
        Licitatie l = cautaDupaId(idLicitatie);
        l.setStare(StareLicitatie.ANULATA);
    }

    public Licitatie cautaDupaId(int id) {
        Licitatie l = licitatii.get(id);
        if (l == null) throw new IllegalArgumentException("Licitatia cu id=" + id + " nu exista.");
        return l;
    }

    public List<Licitatie> listeazaToate() {
        return new ArrayList<>(licitatii.values());
    }

    public List<Licitatie> listeazaActive() {
        List<Licitatie> active = new ArrayList<>();
        for (Licitatie l : licitatii.values()) {
            if (l.getStare() == StareLicitatie.ACTIVA) active.add(l);
        }
        return active;
    }

    public Oferta getOfertaCastigatoare(int idLicitatie) {
        return cautaDupaId(idLicitatie).getOfertaCastigatoare();
    }

    public TreeSet<Produs> produseInLicitatiiActive() {
        TreeSet<Produs> produse = new TreeSet<>();
        for (Licitatie l : licitatii.values()) {
            if (l.getStare() == StareLicitatie.ACTIVA) {
                produse.add(l.getProdus());
            }
        }
        return produse;
    }

    public Map<Cumparator, List<Oferta>> ofertelePerCumparator() {
        Map<Cumparator, List<Oferta>> rezultat = new HashMap<>();
        for (Licitatie l : licitatii.values()) {
            for (Oferta o : l.getOferte()) {
                rezultat.computeIfAbsent(o.getCumparator(), k -> new ArrayList<>()).add(o);
            }
        }
        return rezultat;
    }
}
