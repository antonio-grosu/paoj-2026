package com.pao.proiect.licitatii.repository;

import com.pao.proiect.licitatii.model.CategorieProdus;
import com.pao.proiect.licitatii.model.CodProdus;
import com.pao.proiect.licitatii.model.Produs;
import com.pao.proiect.licitatii.model.Vanzator;
import com.pao.proiect.licitatii.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Produsul e identificat prin codul sau de business (unic in schema). */
public class ProdusRepository implements Repository<Produs, String> {

    private final UtilizatorRepository utilizatorRepo = new UtilizatorRepository();

    private Connection conn() throws SQLException {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    private Produs mapRow(ResultSet rs) throws SQLException {
        Vanzator vanzator = (Vanzator) utilizatorRepo.findById(rs.getInt("vanzator_id"))
                .orElseThrow(() -> new SQLException("Vanzator inexistent pentru produs"));
        return new Produs(
                new CodProdus(rs.getString("cod")),
                rs.getString("denumire"),
                rs.getString("descriere"),
                CategorieProdus.valueOf(rs.getString("categorie")),
                rs.getDouble("pret_minim"),
                vanzator);
    }

    @Override
    public void save(Produs p) throws SQLException {
        String sql = "INSERT INTO produs (cod, denumire, descriere, categorie, pret_minim, vanzator_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getCod().getValoare());
            ps.setString(2, p.getDenumire());
            ps.setString(3, p.getDescriere());
            ps.setString(4, p.getCategorie().name());
            ps.setDouble(5, p.getPretMinim());
            ps.setInt(6, p.getVanzator().getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Produs> findById(String cod) throws SQLException {
        String sql = "SELECT cod, denumire, descriere, categorie, pret_minim, vanzator_id " +
                "FROM produs WHERE cod = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, cod.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Produs> findAll() throws SQLException {
        String sql = "SELECT cod, denumire, descriere, categorie, pret_minim, vanzator_id " +
                "FROM produs ORDER BY pret_minim";
        List<Produs> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Produs p) throws SQLException {
        String sql = "UPDATE produs SET denumire = ?, descriere = ?, categorie = ?, pret_minim = ?, vanzator_id = ? " +
                "WHERE cod = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getDenumire());
            ps.setString(2, p.getDescriere());
            ps.setString(3, p.getCategorie().name());
            ps.setDouble(4, p.getPretMinim());
            ps.setInt(5, p.getVanzator().getId());
            ps.setString(6, p.getCod().getValoare());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String cod) throws SQLException {
        String sql = "DELETE FROM produs WHERE cod = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, cod.toUpperCase());
            ps.executeUpdate();
        }
    }

    /** Id-ul intern (BD) al produsului, necesar la crearea licitatiilor. */
    public int findIdByCod(String cod) throws SQLException {
        String sql = "SELECT id FROM produs WHERE cod = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, cod.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
                throw new SQLException("Produsul cu codul " + cod + " nu exista.");
            }
        }
    }
}
