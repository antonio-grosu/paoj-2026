package com.pao.proiect.licitatii.service;

import com.pao.proiect.licitatii.model.CategorieProdus;
import com.pao.proiect.licitatii.model.CodProdus;
import com.pao.proiect.licitatii.model.Produs;
import com.pao.proiect.licitatii.model.Vanzator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdusService {
    private static ProdusService instance;

    private final Map<String, Produs> produse = new HashMap<>();

    private ProdusService() {}

    public static ProdusService getInstance() {
        if (instance == null) {
            instance = new ProdusService();
        }
        return instance;
    }

    public Produs adaugaProdus(String cod, String denumire, String descriere, CategorieProdus categorie, double pretMinim, Vanzator vanzator) {
        CodProdus codProdus = new CodProdus(cod);
        if (produse.containsKey(codProdus.getValoare())) {
            throw new IllegalArgumentException("Exista deja un produs cu codul: " + cod);
        }
        Produs p = new Produs(codProdus, denumire, descriere, categorie, pretMinim, vanzator);
        produse.put(codProdus.getValoare(), p);
        return p;
    }

    public Produs cautaDupaCod(String cod) {
        Produs p = produse.get(cod.toUpperCase());
        if (p == null) throw new IllegalArgumentException("Produsul cu codul " + cod + " nu exista.");
        return p;
    }

    public List<Produs> listeazaToare() {
        return new ArrayList<>(produse.values());
    }

    public List<Produs> listeazaSortateDupaPreт() {
        List<Produs> lista = new ArrayList<>(produse.values());
        Collections.sort(lista);
        return lista;
    }

    public List<Produs> cautaDupaCategorie(CategorieProdus categorie) {
        List<Produs> rezultat = new ArrayList<>();
        for (Produs p : produse.values()) {
            if (p.getCategorie() == categorie) rezultat.add(p);
        }
        return rezultat;
    }

    public Map<CategorieProdus, List<Produs>> grupeazaDupaCategorie() {
        Map<CategorieProdus, List<Produs>> grupuri = new HashMap<>();
        for (Produs p : produse.values()) {
            grupuri.computeIfAbsent(p.getCategorie(), k -> new ArrayList<>()).add(p);
        }
        return grupuri;
    }

    public void stergeProdus(String cod) {
        String cheie = cod.toUpperCase();
        if (!produse.containsKey(cheie)) {
            throw new IllegalArgumentException("Produsul cu codul " + cod + " nu exista.");
        }
        produse.remove(cheie);
    }
}
