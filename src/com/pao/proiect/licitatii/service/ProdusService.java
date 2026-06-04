package com.pao.proiect.licitatii.service;

import com.pao.proiect.licitatii.model.CategorieProdus;
import com.pao.proiect.licitatii.model.CodProdus;
import com.pao.proiect.licitatii.model.Produs;
import com.pao.proiect.licitatii.model.Vanzator;
import com.pao.proiect.licitatii.repository.ProdusRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Operatii pe produse, persistate prin ProdusRepository.
 */
public class ProdusService {
    private static ProdusService instance;

    private final ProdusRepository repository = new ProdusRepository();

    private ProdusService() {}

    public static ProdusService getInstance() {
        if (instance == null) {
            instance = new ProdusService();
        }
        return instance;
    }

    public Produs adaugaProdus(String cod, String denumire, String descriere,
                               CategorieProdus categorie, double pretMinim, Vanzator vanzator) throws SQLException {
        CodProdus codProdus = new CodProdus(cod);
        if (repository.findById(codProdus.getValoare()).isPresent()) {
            throw new IllegalArgumentException("Exista deja un produs cu codul: " + cod);
        }
        Produs p = new Produs(codProdus, denumire, descriere, categorie, pretMinim, vanzator);
        repository.save(p);
        return p;
    }

    public Produs cautaDupaCod(String cod) throws SQLException {
        return repository.findById(cod)
                .orElseThrow(() -> new IllegalArgumentException("Produsul cu codul " + cod + " nu exista."));
    }

    public List<Produs> listeazaToate() throws SQLException {
        return repository.findAll();
    }

    /** findAll() vine deja ordonat dupa pret_minim din SQL. */
    public List<Produs> listeazaSortateDupaPret() throws SQLException {
        return repository.findAll();
    }

    /** Grupare pe categorie, pastrand ordinea pe pret din findAll(). */
    public Map<CategorieProdus, List<Produs>> grupeazaDupaCategorie() throws SQLException {
        Map<CategorieProdus, List<Produs>> grupuri = new LinkedHashMap<>();
        for (Produs p : repository.findAll()) {
            grupuri.computeIfAbsent(p.getCategorie(), k -> new ArrayList<>()).add(p);
        }
        return grupuri;
    }

    public void stergeProdus(String cod) throws SQLException {
        if (repository.findById(cod).isEmpty()) {
            throw new IllegalArgumentException("Produsul cu codul " + cod + " nu exista.");
        }
        repository.delete(cod);
    }
}
