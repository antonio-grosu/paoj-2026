package com.pao.proiect.licitatii.service;

import com.pao.proiect.licitatii.exception.UtilizatorNegasitException;
import com.pao.proiect.licitatii.model.Cumparator;
import com.pao.proiect.licitatii.model.Utilizator;
import com.pao.proiect.licitatii.model.Vanzator;
import com.pao.proiect.licitatii.repository.UtilizatorRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Operatii pe utilizatori, persistate prin UtilizatorRepository.
 * API-ul ramane identic cu Etapa I; doar sursa datelor s-a mutat in BD.
 */
public class UtilizatorService {
    private static UtilizatorService instance;

    private final UtilizatorRepository repository = new UtilizatorRepository();

    private UtilizatorService() {}

    public static UtilizatorService getInstance() {
        if (instance == null) {
            instance = new UtilizatorService();
        }
        return instance;
    }

    public Cumparator adaugaCumparator(String nume, String email, double buget) throws SQLException {
        Cumparator c = new Cumparator(0, nume, email, buget);
        repository.save(c);
        return c;
    }

    public Vanzator adaugaVanzator(String nume, String email, String iban) throws SQLException {
        Vanzator v = new Vanzator(0, nume, email, iban);
        repository.save(v);
        return v;
    }

    public Utilizator cautaDupaId(int id) throws UtilizatorNegasitException, SQLException {
        return repository.findById(id).orElseThrow(() -> new UtilizatorNegasitException(id));
    }

    public List<Utilizator> listeazaToti() throws SQLException {
        return repository.findAll();
    }

    public List<Cumparator> listeazaCumparatori() throws SQLException {
        List<Cumparator> rezultat = new ArrayList<>();
        for (Utilizator u : repository.findAll()) {
            if (u instanceof Cumparator c) rezultat.add(c);
        }
        return rezultat;
    }

    public List<Vanzator> listeazaVanzatori() throws SQLException {
        List<Vanzator> rezultat = new ArrayList<>();
        for (Utilizator u : repository.findAll()) {
            if (u instanceof Vanzator v) rezultat.add(v);
        }
        return rezultat;
    }

    public void stergeUtilizator(int id) throws UtilizatorNegasitException, SQLException {
        if (repository.findById(id).isEmpty()) throw new UtilizatorNegasitException(id);
        repository.delete(id);
    }
}
