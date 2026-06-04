package com.pao.proiect.licitatii.repository;

import com.pao.proiect.licitatii.model.Cumparator;
import com.pao.proiect.licitatii.model.Oferta;
import com.pao.proiect.licitatii.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OfertaRepository implements Repository<Oferta, Integer> {

    private final UtilizatorRepository utilizatorRepo = new UtilizatorRepository();

    private Connection conn() throws SQLException {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    private Oferta mapRow(ResultSet rs) throws SQLException {
        Cumparator cumparator = (Cumparator) utilizatorRepo.findById(rs.getInt("cumparator_id"))
                .orElseThrow(() -> new SQLException("Cumparator inexistent pentru oferta"));
        Oferta o = new Oferta(rs.getInt("id"), cumparator, rs.getDouble("suma"));
        o.setDataOferta(LocalDateTime.parse(rs.getString("data_oferta")));
        return o;
    }

    @Override
    public void save(Oferta o) throws SQLException {
        throw new UnsupportedOperationException(
                "Ofertele se insereaza tranzactional prin LicitatieService.plaseazaOferta");
    }

    @Override
    public Optional<Oferta> findById(Integer id) throws SQLException {
        String sql = "SELECT id, licitatie_id, cumparator_id, suma, data_oferta FROM oferta WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Oferta> findAll() throws SQLException {
        String sql = "SELECT id, licitatie_id, cumparator_id, suma, data_oferta FROM oferta ORDER BY id";
        List<Oferta> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Oferta o) throws SQLException {
        String sql = "UPDATE oferta SET suma = ?, data_oferta = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, o.getSuma());
            ps.setString(2, o.getDataOferta().toString());
            ps.setInt(3, o.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM oferta WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Ofertele unei licitatii, ordonate descrescator dupa suma. */
    public List<Oferta> findByLicitatie(int licitatieId) throws SQLException {
        String sql = "SELECT id, licitatie_id, cumparator_id, suma, data_oferta " +
                "FROM oferta WHERE licitatie_id = ? ORDER BY suma DESC";
        List<Oferta> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, licitatieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
