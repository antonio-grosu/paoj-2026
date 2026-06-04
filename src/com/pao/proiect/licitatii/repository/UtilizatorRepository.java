package com.pao.proiect.licitatii.repository;

import com.pao.proiect.licitatii.model.Cumparator;
import com.pao.proiect.licitatii.model.Utilizator;
import com.pao.proiect.licitatii.model.Vanzator;
import com.pao.proiect.licitatii.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilizatorRepository implements Repository<Utilizator, Integer> {

    private Connection conn() throws SQLException {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    private Utilizator mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String rol = rs.getString("rol");
        String nume = rs.getString("nume");
        String email = rs.getString("email");
        Utilizator u;
        if ("CUMPARATOR".equals(rol)) {
            u = new Cumparator(id, nume, email, rs.getDouble("buget"));
        } else {
            u = new Vanzator(id, nume, email, rs.getString("iban"));
        }
        return u;
    }

    @Override
    public void save(Utilizator u) throws SQLException {
        String sql = "INSERT INTO utilizator (rol, nume, email, buget, iban) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindUtilizator(ps, u);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public Optional<Utilizator> findById(Integer id) throws SQLException {
        String sql = "SELECT id, rol, nume, email, buget, iban FROM utilizator WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Utilizator> findAll() throws SQLException {
        String sql = "SELECT id, rol, nume, email, buget, iban FROM utilizator ORDER BY id";
        List<Utilizator> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Utilizator u) throws SQLException {
        String sql = "UPDATE utilizator SET rol = ?, nume = ?, email = ?, buget = ?, iban = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            bindUtilizator(ps, u);
            ps.setInt(6, u.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM utilizator WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void bindUtilizator(PreparedStatement ps, Utilizator u) throws SQLException {
        if (u instanceof Cumparator c) {
            ps.setString(1, "CUMPARATOR");
            ps.setString(2, c.getNume());
            ps.setString(3, c.getEmail());
            ps.setDouble(4, c.getBuget());
            ps.setNull(5, java.sql.Types.VARCHAR);
        } else {
            Vanzator v = (Vanzator) u;
            ps.setString(1, "VANZATOR");
            ps.setString(2, v.getNume());
            ps.setString(3, v.getEmail());
            ps.setNull(4, java.sql.Types.REAL);
            ps.setString(5, v.getIban());
        }
    }
}
