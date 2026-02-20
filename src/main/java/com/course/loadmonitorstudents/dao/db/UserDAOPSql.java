package com.course.loadmonitorstudents.dao.db;

import com.course.loadmonitorstudents.config.DatabaseConfig;
import com.course.loadmonitorstudents.dao.UserDAO;
import com.course.loadmonitorstudents.model.Role;
import com.course.loadmonitorstudents.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAOPSql implements UserDAO {

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setFirstName(rs.getString("firstName"));
        u.setLastName(rs.getString("lastName"));
        u.setRole(Role.valueOf(rs.getString("role")));
        
        Long curatorId = rs.getLong("curator_id");
        u.setCuratorId(rs.wasNull() ? null : curatorId);
        
        Long telegramId = rs.getLong("telegram_id");
        u.setTelegramID(rs.wasNull() ? null : telegramId);
        
        u.setGoogleCalendarApiKey(rs.getString("google_calendar_api_key"));
        return u;
    }

    public void create(User user) throws SQLException {
        String sql = """
        INSERT INTO users(email, password, firstName, lastName, role, curator_id, telegram_id, google_calendar_api_key)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getRole().name());
            stmt.setObject(6, user.getCuratorId());

            stmt.setObject(7, user.getTelegramID());

            stmt.setString(8, user.getGoogleCalendarApiKey());

            stmt.executeUpdate();
        }
    }

    public void update(User user) throws SQLException {
        String sql = """
        UPDATE users SET email=?, password=?, firstName=?, lastName=?, role=?,
                         curator_id=?, telegram_id=?, google_calendar_api_key=?
        WHERE id=?
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getRole().name());
            stmt.setObject(6, user.getCuratorId());

            stmt.setObject(7, user.getTelegramID());

            stmt.setString(8, user.getGoogleCalendarApiKey());
            stmt.setLong(9, user.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public User findIdByEmailAndPassword(String mail, String password) throws SQLException{
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mail);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return map(rs);
            return null;
        }
    }

    public User findByEmail(String mail) throws SQLException{
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return map(rs);
            return null;
        }
    }

    public User findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return map(rs);
            return null;
        }
    }

    public User findByIdAndCuratorId(Long id, Long curatorId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? AND curator_id = ? AND role = 'STUDENT'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.setLong(2, curatorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return map(rs);
            return null;
        }
    }

    public List<User> findAllStudentsByCuratorId(Long curatorId) throws SQLException {
        String sql = "SELECT * FROM users WHERE curator_id = ? AND role = 'STUDENT' ORDER BY id";
        List<User> students = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, curatorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                students.add(map(rs));
            }
        }
        return students;
    }
}
