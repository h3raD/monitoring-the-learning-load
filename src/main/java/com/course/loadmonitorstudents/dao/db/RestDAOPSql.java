package com.course.loadmonitorstudents.dao.db;

import com.course.loadmonitorstudents.config.DatabaseConfig;
import com.course.loadmonitorstudents.dao.RestDAO;
import com.course.loadmonitorstudents.model.Rest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestDAOPSql implements RestDAO {

    /**
     * Устанавливает запись об отдыхе в базу данных.
     *
     * @param rest объект Rest для сохранения
     * @throws SQLException если ошибка базы данных
     */
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

    /**
     * Обновляет запись об отдыхе в базе.
     *
     * @param rest объект Rest с новыми данными
     * @throws SQLException если ошибка базы данных
     */
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

    /**
     * Удаляет запись об отдыхе по ID.
     *
     * @param id ID записи
     * @throws SQLException если ошибка базы данных
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM rests WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Находит все записи об отдыхе студента.
     *
     * @param studentId ID студента
     * @return лист Rest объектов
     * @throws SQLException если ошибка базы
     */
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

    /**
     * Мапит результат SQL запроса в объект Rest.
     *
     * @param rs ResultSet от SQL запроса
     * @return Rest объект
     * @throws SQLException если ошибка данных
     */
    private Rest map(ResultSet rs) throws SQLException {
        Rest r = new Rest();
        r.setId(rs.getLong("id"));
        r.setDate(rs.getDate("date").toLocalDate());
        r.setStudentId(rs.getLong("student_id"));
        r.setHours(rs.getInt("hours"));  // Добавьте получение часов
        return r;
    }
}
