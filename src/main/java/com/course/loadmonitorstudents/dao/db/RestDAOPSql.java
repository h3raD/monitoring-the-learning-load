package com.course.loadmonitorstudents.dao.db;

import com.course.loadmonitorstudents.config.DatabaseConfig;
import com.course.loadmonitorstudents.dao.RestDAO;
import com.course.loadmonitorstudents.model.Rest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestDAOPSql implements RestDAO {

    public void create(Rest rest) throws SQLException {
        String sql = """
            INSERT INTO rests(date, student_id, hours)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(rest.getDate()));
            stmt.setLong(2, rest.getStudentId());
            stmt.setInt(3, rest.getHours());

            stmt.executeUpdate();
        }
    }

    public void update(Rest rest) throws SQLException {
        String sql = """
            UPDATE rests SET date=?, student_id=?, hours=?
            WHERE id=?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(rest.getDate()));
            stmt.setLong(2, rest.getStudentId());
            stmt.setInt(3, rest.getHours());
            stmt.setLong(4, rest.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM rests WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Rest> findByStudentId(Long studentId) throws SQLException {
        String sql = "SELECT id, date, student_id, hours FROM rests WHERE student_id = ? ORDER BY date DESC";
        List<Rest> rests = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rests.add(map(rs));
            }
        }

        return rests;
    }

    private Rest map(ResultSet rs) throws SQLException {
        Rest r = new Rest();
        r.setId(rs.getLong("id"));
        r.setDate(rs.getDate("date").toLocalDate());
        r.setStudentId(rs.getLong("student_id"));
        r.setHours(rs.getInt("hours"));  // Добавьте получение часов
        return r;
    }
}
