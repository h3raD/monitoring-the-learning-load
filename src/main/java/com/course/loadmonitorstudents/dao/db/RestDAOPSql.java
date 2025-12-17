package com.course.loadmonitorstudents.dao.db;

import com.course.loadmonitorstudents.config.DatabaseConfig;
import com.course.loadmonitorstudents.dao.RestDAO;
import com.course.loadmonitorstudents.model.Rest;

import java.sql.*;

public class RestDAOPSql implements RestDAO {
    private Rest map(ResultSet rs) throws SQLException {
        Rest r = new Rest();
        r.setId(rs.getLong("id"));
        r.setDate(rs.getDate("date").toLocalDate());
        r.setStudentId(rs.getLong("student_id"));
        return r;
    }

    public void create(Rest rest) throws SQLException {
        String sql = """
            INSERT INTO rests(date, student_id)
            VALUES (?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(rest.getDate()));
            stmt.setLong(2, rest.getStudentId());

            stmt.executeUpdate();
        }
    }

    public void update(Rest rest) throws SQLException {
        String sql = """
            UPDATE rests SET date=?, student_id=?
            WHERE id=?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(rest.getDate()));
            stmt.setLong(2, rest.getStudentId());
            stmt.setLong(3, rest.getId());

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
}
