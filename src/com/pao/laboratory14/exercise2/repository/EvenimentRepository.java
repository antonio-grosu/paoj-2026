package com.pao.laboratory14.exercise2.repository;

import com.pao.laboratory14.exercise1.TipBilet;
import com.pao.laboratory14.exercise2.model.Eveniment;
import com.pao.laboratory14.exercise2.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EvenimentRepository implements Repository<Eveniment, Integer> {

    private Connection getConn() throws SQLException {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    /** Recreeaza tabela la pornire pentru determinismul testelor. */
    public void initSchema() throws SQLException {
        try (Statement st = getConn().createStatement()) {
            st.execute("DROP TABLE IF EXISTS evenimente");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS evenimente (
                        id         INTEGER PRIMARY KEY AUTOINCREMENT,
                        nume       TEXT    NOT NULL,
                        data       TEXT    NOT NULL,
                        capacitate INTEGER,
                        tip        TEXT
                    )""");
        }
    }

    private Eveniment mapRow(ResultSet rs) throws SQLException {
        Eveniment e = new Eveniment();
        e.setId(rs.getInt("id"));
        e.setNume(rs.getString("nume"));
        e.setData(rs.getString("data"));
        e.setCapacitate(rs.getInt("capacitate"));
        e.setTip(TipBilet.valueOf(rs.getString("tip")));
        return e;
    }

    @Override
    public void save(Eveniment ev) throws SQLException {
        String sql = "INSERT INTO evenimente (nume, data, capacitate, tip) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ev.getNume());
            ps.setString(2, ev.getData());
            ps.setInt(3, ev.getCapacitate());
            ps.setString(4, ev.getTip().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ev.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public Optional<Eveniment> findById(Integer id) throws SQLException {
        String sql = "SELECT id, nume, data, capacitate, tip FROM evenimente WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Eveniment> findAll() throws SQLException {
        String sql = "SELECT id, nume, data, capacitate, tip FROM evenimente ORDER BY id";
        List<Eveniment> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Eveniment ev) throws SQLException {
        String sql = "UPDATE evenimente SET nume = ?, data = ?, capacitate = ?, tip = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, ev.getNume());
            ps.setString(2, ev.getData());
            ps.setInt(3, ev.getCapacitate());
            ps.setString(4, ev.getTip().name());
            ps.setInt(5, ev.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        deleteImpl(id);
    }

    /** DELETE care returneaza numarul de randuri sterse (0 = id inexistent). */
    public int deleteImpl(int id) throws SQLException {
        String sql = "DELETE FROM evenimente WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM evenimente";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
