package com.pao.laboratory12.repository;

import com.pao.laboratory12.model.Author;
import com.pao.laboratory12.util.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorRepository implements Repository<Author, Long> {

    private Connection getConn() throws SQLException {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    private Author mapRow(ResultSet rs) throws SQLException {
        Author a = new Author();
        a.setId(rs.getLong("id"));
        a.setName(rs.getString("name"));
        a.setCountry(rs.getString("country"));
        return a;
    }

    @Override
    public void save(Author author) throws SQLException {
        String sql = "INSERT INTO author (name, country) VALUES (?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getCountry());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) author.setId(keys.getLong(1));
            }
        }
    }

    @Override
    public Optional<Author> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, country FROM author WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Author> findAll() throws SQLException {
        String sql = "SELECT id, name, country FROM author ORDER BY id";
        List<Author> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Author author) throws SQLException {
        String sql = "UPDATE author SET name = ?, country = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getCountry());
            ps.setLong(3, author.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM author WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
