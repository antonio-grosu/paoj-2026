package com.pao.proiect.licitatii.repository;

import com.pao.proiect.licitatii.model.Licitatie;
import com.pao.proiect.licitatii.model.Produs;
import com.pao.proiect.licitatii.model.StareLicitatie;
import com.pao.proiect.licitatii.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LicitatieRepository implements Repository<Licitatie, Integer> {

    private Connection conn() throws SQLException {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    /** Reconstruieste o Licitatie pe baza produsului deja incarcat de apelant. */
    private Licitatie mapRow(ResultSet rs, Produs produs) throws SQLException {
        Licitatie l = new Licitatie(
                rs.getInt("id"),
                produs,
                LocalDateTime.parse(rs.getString("data_start")),
                LocalDateTime.parse(rs.getString("data_final")));
        l.setStare(StareLicitatie.valueOf(rs.getString("stare")));
        return l;
    }

    @Override
    public void save(Licitatie l) throws SQLException {
        String sql = "INSERT INTO licitatie (produs_id, data_start, data_final, stare) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, produsId(l));
            ps.setString(2, l.getDataStart().toString());
            ps.setString(3, l.getDataFinal().toString());
            ps.setString(4, l.getStare().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) l.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public Optional<Licitatie> findById(Integer id) throws SQLException {
        String sql = """
                SELECT l.id, l.data_start, l.data_final, l.stare,
                       p.cod, p.denumire, p.descriere, p.categorie, p.pret_minim, p.vanzator_id
                FROM licitatie l
                JOIN produs p ON l.produs_id = p.id
                WHERE l.id = ?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs, produsDinRand(rs)));
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Licitatie> findAll() throws SQLException {
        String sql = """
                SELECT l.id, l.data_start, l.data_final, l.stare,
                       p.cod, p.denumire, p.descriere, p.categorie, p.pret_minim, p.vanzator_id
                FROM licitatie l
                JOIN produs p ON l.produs_id = p.id
                ORDER BY l.id""";
        List<Licitatie> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs, produsDinRand(rs)));
        }
        return list;
    }

    @Override
    public void update(Licitatie l) throws SQLException {
        String sql = "UPDATE licitatie SET produs_id = ?, data_start = ?, data_final = ?, stare = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, produsId(l));
            ps.setString(2, l.getDataStart().toString());
            ps.setString(3, l.getDataFinal().toString());
            ps.setString(4, l.getStare().name());
            ps.setInt(5, l.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM licitatie WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Produs produsDinRand(ResultSet rs) throws SQLException {
        return new Produs(
                new com.pao.proiect.licitatii.model.CodProdus(rs.getString("cod")),
                rs.getString("denumire"),
                rs.getString("descriere"),
                com.pao.proiect.licitatii.model.CategorieProdus.valueOf(rs.getString("categorie")),
                rs.getDouble("pret_minim"),
                vanzatorDinId(rs.getInt("vanzator_id")));
    }

    private com.pao.proiect.licitatii.model.Vanzator vanzatorDinId(int id) throws SQLException {
        return (com.pao.proiect.licitatii.model.Vanzator) new UtilizatorRepository().findById(id)
                .orElseThrow(() -> new SQLException("Vanzator inexistent: " + id));
    }

    private int produsId(Licitatie l) throws SQLException {
        return new ProdusRepository().findIdByCod(l.getProdus().getCod().getValoare());
    }
}
