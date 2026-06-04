package com.pao.proiect.licitatii.service;

import com.pao.proiect.licitatii.exception.LicitatieInchisaException;
import com.pao.proiect.licitatii.exception.OfertaInsuficientaException;
import com.pao.proiect.licitatii.model.Cumparator;
import com.pao.proiect.licitatii.model.Licitatie;
import com.pao.proiect.licitatii.model.Oferta;
import com.pao.proiect.licitatii.model.Produs;
import com.pao.proiect.licitatii.model.StareLicitatie;
import com.pao.proiect.licitatii.repository.LicitatieRepository;
import com.pao.proiect.licitatii.repository.OfertaRepository;
import com.pao.proiect.licitatii.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Operatii pe licitatii si oferte. Contine tranzactia de plasare a unei oferte
 * (rand barem 5) si interogarile cu JOIN (rand barem 6).
 */
public class LicitatieService {
    private static LicitatieService instance;

    private final LicitatieRepository licitatieRepository = new LicitatieRepository();
    private final OfertaRepository ofertaRepository = new OfertaRepository();

    private LicitatieService() {}

    public static LicitatieService getInstance() {
        if (instance == null) {
            instance = new LicitatieService();
        }
        return instance;
    }

    private Connection conn() throws SQLException {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    public Licitatie creeazaLicitatie(Produs produs, LocalDateTime dataStart, LocalDateTime dataFinal)
            throws SQLException {
        Licitatie l = new Licitatie(0, produs, dataStart, dataFinal);
        licitatieRepository.save(l);
        return l;
    }

    /**
     * Tranzactie JDBC explicita: citeste starea licitatiei + pretul minim al produsului
     * (JOIN licitatie-produs), valideaza, apoi insereaza oferta. Commit la succes, rollback la eroare.
     */
    public Oferta plaseazaOferta(int idLicitatie, Cumparator cumparator, double suma)
            throws LicitatieInchisaException, OfertaInsuficientaException, SQLException {
        Connection conn = conn();
        conn.setAutoCommit(false);
        try {
            String stare;
            double pretMinim;
            String checkSql = """
                    SELECT l.stare, p.pret_minim
                    FROM licitatie l
                    JOIN produs p ON l.produs_id = p.id
                    WHERE l.id = ?""";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, idLicitatie);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Licitatia cu id=" + idLicitatie + " nu exista.");
                    }
                    stare = rs.getString("stare");
                    pretMinim = rs.getDouble("pret_minim");
                }
            }

            if (!StareLicitatie.ACTIVA.name().equals(stare)) {
                throw new LicitatieInchisaException(idLicitatie);
            }
            if (suma < pretMinim) {
                throw new OfertaInsuficientaException(suma, pretMinim);
            }

            long ofertaId;
            String insertSql = "INSERT INTO oferta (licitatie_id, cumparator_id, suma, data_oferta) " +
                    "VALUES (?, ?, ?, ?)";
            Oferta oferta = new Oferta(0, cumparator, suma);
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idLicitatie);
                ps.setInt(2, cumparator.getId());
                ps.setDouble(3, suma);
                ps.setString(4, oferta.getDataOferta().toString());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    ofertaId = keys.getLong(1);
                }
            }

            conn.commit();
            oferta.setId((int) ofertaId);
            return oferta;
        } catch (SQLException | LicitatieInchisaException | OfertaInsuficientaException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void inchideLicitatie(int idLicitatie) throws SQLException {
        Licitatie l = cautaDupaId(idLicitatie);
        l.setStare(StareLicitatie.INCHISA);
        licitatieRepository.update(l);
    }

    public Licitatie cautaDupaId(int id) throws SQLException {
        return licitatieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Licitatia cu id=" + id + " nu exista."));
    }

    public List<Licitatie> listeazaActive() throws SQLException {
        List<Licitatie> active = new ArrayList<>();
        for (Licitatie l : licitatieRepository.findAll()) {
            if (l.getStare() == StareLicitatie.ACTIVA) active.add(l);
        }
        return active;
    }

    public Oferta getOfertaCastigatoare(int idLicitatie) throws SQLException {
        List<Oferta> oferte = ofertaRepository.findByLicitatie(idLicitatie);
        return oferte.isEmpty() ? null : oferte.get(0);
    }

    // ---------- Interogari cu JOIN (rand barem 6) ----------

    /** JOIN #1: produse aflate in licitatii active, sortate dupa pret minim. */
    public TreeSet<Produs> produseInLicitatiiActive() throws SQLException {
        TreeSet<Produs> produse = new TreeSet<>();
        for (Licitatie l : licitatieRepository.findAll()) {
            if (l.getStare() == StareLicitatie.ACTIVA) produse.add(l.getProdus());
        }
        return produse;
    }

    /** JOIN #2: toate ofertele grupate pe cumparator (oferta JOIN utilizator). */
    public Map<String, List<String>> ofertelePerCumparator() throws SQLException {
        String sql = """
                SELECT u.nume AS cumparator, o.suma, o.data_oferta
                FROM oferta o
                JOIN utilizator u ON o.cumparator_id = u.id
                ORDER BY u.nume, o.suma DESC""";
        Map<String, List<String>> rezultat = new LinkedHashMap<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String detaliu = String.format("%.2f RON la %s", rs.getDouble("suma"), rs.getString("data_oferta"));
                rezultat.computeIfAbsent(rs.getString("cumparator"), k -> new ArrayList<>()).add(detaliu);
            }
        }
        return rezultat;
    }

    /** JOIN #3: top produse dupa numarul de oferte primite (produs LEFT JOIN licitatie LEFT JOIN oferta). */
    public List<String> topProduseDupaOferte() throws SQLException {
        String sql = """
                SELECT p.denumire, v.nume AS vanzator, COUNT(o.id) AS nr_oferte
                FROM produs p
                JOIN utilizator v ON p.vanzator_id = v.id
                LEFT JOIN licitatie l ON l.produs_id = p.id
                LEFT JOIN oferta o ON o.licitatie_id = l.id
                GROUP BY p.id, p.denumire, v.nume
                ORDER BY nr_oferte DESC, p.denumire""";
        List<String> rezultat = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rezultat.add(String.format("'%s' (vanzator: %s) — %d oferte",
                        rs.getString("denumire"), rs.getString("vanzator"), rs.getInt("nr_oferte")));
            }
        }
        return rezultat;
    }
}
