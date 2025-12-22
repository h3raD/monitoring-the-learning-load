package com.course.loadmonitorstudents.dao.db;

import com.course.loadmonitorstudents.config.DatabaseConfig;
import com.course.loadmonitorstudents.dao.TaskDAO;
import com.course.loadmonitorstudents.dto.TaskWithCuratorDTO;
import com.course.loadmonitorstudents.dto.TaskWithStudentDTO;
import com.course.loadmonitorstudents.model.StatusTask;
import com.course.loadmonitorstudents.model.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAOPSql implements TaskDAO {

    private Task map(ResultSet rs) throws SQLException {
        Task t = new Task();
        t.setId(rs.getLong("id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setAddTime(rs.getTimestamp("add_time").toLocalDateTime());
        t.setDeadline(rs.getTimestamp("deadline").toLocalDateTime());

        Timestamp ts;

        ts = rs.getTimestamp("start_work");
        t.setStartWork(ts != null ? ts.toLocalDateTime() : null);

        ts = rs.getTimestamp("end_work");
        t.setEndWork(ts != null ? ts.toLocalDateTime() : null);

        t.setStatus(StatusTask.valueOf(rs.getString("status")));
        t.setStudentId(rs.getLong("student_id"));
        t.setCuratorId(rs.getLong("curator_id"));

        return t;
    }

    @Override
    public void create(Task task) throws SQLException {
        String sql = """
                        INSERT INTO tasks(title, description, deadline, start_work, end_work, status, student_id, curator_id) 
                        VALUES (?, ?, ?, ?, ?, ?::status_task_type, ?, ?)
                     """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
            stmt.setTimestamp(4, task.getStartWork() != null ? Timestamp.valueOf(task.getStartWork()) : null);
            stmt.setTimestamp(5, task.getEndWork() != null ? Timestamp.valueOf(task.getEndWork()) : null);
            stmt.setString(6, task.getStatus().name());
            stmt.setLong(7, task.getStudentId());
            stmt.setLong(8, task.getCuratorId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Task task) throws SQLException {
        String sql = """
                        UPDATE tasks SET title=?, description=?, deadline=?, start_work=?, end_work=?, 
                                         status=?::status_task_type, student_id=?, curator_id=? 
                        WHERE id=? 
                     """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
            stmt.setTimestamp(4, task.getStartWork() != null ? Timestamp.valueOf(task.getStartWork()) : null);
            stmt.setTimestamp(5, task.getEndWork() != null ? Timestamp.valueOf(task.getEndWork()) : null);
            stmt.setString(6, task.getStatus().name());
            stmt.setLong(7, task.getStudentId());
            stmt.setLong(8, task.getCuratorId());
            stmt.setLong(9, task.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public Task getById(Long id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    @Override
    public LocalDateTime getDeadline(Long id) throws SQLException {
        String sql = "SELECT deadline FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getTimestamp("deadline").toLocalDateTime();
            return null;
        }
    }

    @Override
    public Task getTaskByIdAndTaskId(Long idTask, Long id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ? AND student_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idTask);
            stmt.setLong(2, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    @Override
    public List<TaskWithStudentDTO> findAllTasksWithStudentByCuratorId(Long curatorId) throws SQLException {
        String sql = """
                        SELECT 
                            t.id, 
                            t.title, 
                            t.description, 
                            t.status, 
                            t.deadline, 
                            t.start_work, 
                            t.end_work, 
                            u.lastname as student_last_name, 
                            u.firstname as student_first_name, 
                            u.id as student_id 
                        FROM tasks t JOIN users u ON t.student_id = u.id 
                        WHERE t.curator_id = ? ORDER BY t.deadline, t.id
                       """;

        List<TaskWithStudentDTO> tasks = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, curatorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TaskWithStudentDTO task = new TaskWithStudentDTO(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        StatusTask.valueOf(rs.getString("status")),
                        rs.getTimestamp("deadline").toLocalDateTime(),
                        rs.getTimestamp("start_work") != null ?
                                rs.getTimestamp("start_work").toLocalDateTime() : null,
                        rs.getTimestamp("end_work") != null ?
                                rs.getTimestamp("end_work").toLocalDateTime() : null,
                        rs.getString("student_last_name"),
                        rs.getString("student_first_name"),
                        rs.getLong("student_id")
                );
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public List<TaskWithStudentDTO> findTasksByStatus(Long curatorId, String status, boolean descending) throws SQLException {
        String order = descending ? "DESC" : "ASC";
        String sql = String.format(""" 
                        SELECT 
                            t.id, 
                            t.title, 
                            t.description, 
                            t.status, 
                            t.deadline,
                            t.start_work, 
                            t.end_work, 
                            u.lastname, 
                            u.firstname, 
                            u.id as student_id 
                        FROM tasks t JOIN users u ON t.student_id = u.id 
                        WHERE t.curator_id = ? AND t.status = ?::status_task_type 
                        ORDER BY t.deadline %s, t.id %s " +
                        """, order, order);

        return executeTaskQuery(sql, curatorId, status);
    }

    @Override
    public List<TaskWithStudentDTO> findTasksByDeadline(Long curatorId, boolean descending) throws SQLException {
        String order = descending ? "DESC" : "ASC";
        String sql = String.format("""
                                        SELECT 
                                            t.id, t.title, t.description, t.status, t.deadline,
                                            t.start_work, t.end_work, u.lastname, u.firstname, u.id as student_id
                                        FROM tasks t
                                        JOIN users u ON t.student_id = u.id
                                        WHERE t.curator_id = ?
                                        ORDER BY t.deadline %s, t.id %s
                                    """, order, order);

        return executeTaskQuery(sql, curatorId);
    }

    @Override
    public List<TaskWithStudentDTO> findTasksByStudentName(Long curatorId, String searchName, boolean descending) throws SQLException {
        String order = descending ? "DESC" : "ASC";
        String sql = String.format("""
                                        SELECT 
                                            t.id, t.title, t.description, t.status, t.deadline,
                                            t.start_work, t.end_work, u.lastname, u.firstname, u.id as student_id
                                        FROM tasks t
                                        JOIN users u ON t.student_id = u.id
                                        WHERE t.curator_id = ? 
                                          AND (LOWER(u.lastname) LIKE LOWER(?) OR LOWER(u.firstname) LIKE LOWER(?))
                                        ORDER BY u.lastname %s, u.firstname %s, t.deadline %s
                                    """, order, order, order);

        return executeTaskQuery(sql, curatorId, "%" + searchName + "%", "%" + searchName + "%");
    }

    @Override
    public List<TaskWithStudentDTO> findTasksById(Long curatorId, Long taskId, boolean descending) throws SQLException {
        String order = descending ? "DESC" : "ASC";
        String sql = String.format("""
                                        SELECT 
                                            t.id, t.title, t.description, t.status, t.deadline,
                                            t.start_work, t.end_work, u.lastname, u.firstname, u.id as student_id
                                        FROM tasks t
                                        JOIN users u ON t.student_id = u.id
                                        WHERE t.curator_id = ? AND t.id = ?
                                        ORDER BY t.id %s
                                    """, order);

        return executeTaskQuery(sql, curatorId, taskId);
    }


    private List<TaskWithStudentDTO> executeTaskQuery(String sql, Object... params) throws SQLException {
        List<TaskWithStudentDTO> tasks = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TaskWithStudentDTO task = new TaskWithStudentDTO(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        StatusTask.valueOf(rs.getString("status")),
                        rs.getTimestamp("deadline").toLocalDateTime(),
                        rs.getTimestamp("start_work") != null ?
                                rs.getTimestamp("start_work").toLocalDateTime() : null,
                        rs.getTimestamp("end_work") != null ?
                                rs.getTimestamp("end_work").toLocalDateTime() : null,
                        rs.getString("lastname"),
                        rs.getString("firstname"),
                        rs.getLong("student_id")
                );
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public List<TaskWithCuratorDTO> findAllTasksByStudentId(Long studentId) throws SQLException {
        String sql = """
                        SELECT 
                            t.id, 
                            t.title, 
                            t.description, 
                            t.status, 
                            t.deadline,
                            t.start_work,
                            t.end_work,
                            t.curator_id,
                            u.firstname as curator_first_name,
                            u.lastname as curator_last_name
                        FROM tasks t
                        JOIN users u ON t.curator_id = u.id
                        WHERE t.student_id = ?
                        ORDER BY t.deadline, t.id
                    """;

        List<TaskWithCuratorDTO> tasks = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TaskWithCuratorDTO task = new TaskWithCuratorDTO(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        StatusTask.valueOf(rs.getString("status")),
                        rs.getTimestamp("deadline").toLocalDateTime(),
                        rs.getTimestamp("start_work") != null ?
                                rs.getTimestamp("start_work").toLocalDateTime() : null,
                        rs.getTimestamp("end_work") != null ?
                                rs.getTimestamp("end_work").toLocalDateTime() : null,
                        rs.getLong("curator_id"),
                        rs.getString("curator_first_name"),
                        rs.getString("curator_last_name")
                );
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public List<TaskWithCuratorDTO> findTasksByStatusForStudent(Long studentId, String status, boolean descending) throws SQLException {
        String order = descending ? "DESC" : "ASC";
        String sql = String.format("""
                                        SELECT 
                                            t.id, t.title, t.description, t.status, t.deadline,
                                            t.start_work, t.end_work, t.curator_id,
                                            u.firstname as curator_first_name, u.lastname as curator_last_name
                                        FROM tasks t
                                        JOIN users u ON t.curator_id = u.id
                                        WHERE t.student_id = ? AND t.status = ?::status_task_type
                                        ORDER BY t.deadline %s, t.id %s
                                    """, order, order);

        return executeStudentTaskQuery(sql, studentId, status);
    }

    @Override
    public List<TaskWithCuratorDTO> findTasksByDeadlineForStudent(Long studentId, boolean descending) throws SQLException {
        String order = descending ? "DESC" : "ASC";
        String sql = String.format("""
                                        SELECT 
                                            t.id, t.title, t.description, t.status, t.deadline,
                                            t.start_work, t.end_work, t.curator_id,
                                            u.firstname as curator_first_name, u.lastname as curator_last_name
                                        FROM tasks t
                                        JOIN users u ON t.curator_id = u.id
                                        WHERE t.student_id = ?
                                        ORDER BY t.deadline %s, t.id %s
                                    """, order, order);

        return executeStudentTaskQuery(sql, studentId);
    }

    @Override
    public List<TaskWithCuratorDTO> findTasksByCuratorNameForStudent(Long studentId, String searchName, boolean descending) throws SQLException {
        String order = descending ? "DESC" : "ASC";
        String sql = String.format("""
                                        SELECT 
                                            t.id, t.title, t.description, t.status, t.deadline,
                                            t.start_work, t.end_work, t.curator_id,
                                            u.firstname as curator_first_name, u.lastname as curator_last_name
                                        FROM tasks t
                                        JOIN users u ON t.curator_id = u.id
                                        WHERE t.student_id = ? 
                                          AND (LOWER(u.lastname) LIKE LOWER(?) OR LOWER(u.firstname) LIKE LOWER(?))
                                        ORDER BY u.lastname %s, u.firstname %s, t.deadline %s
                                    """, order, order, order);

        return executeStudentTaskQuery(sql, studentId, "%" + searchName + "%", "%" + searchName + "%");
    }

    @Override
    public List<TaskWithCuratorDTO> findTaskByIdForStudent(Long studentId, Long taskId, boolean descending) throws SQLException {
        String order = descending ? "DESC" : "ASC";
        String sql = String.format("""        
                                        SELECT 
                                            t.id, 
                                            t.title, 
                                            t.description, 
                                            t.status, 
                                            t.deadline,
                                            t.start_work, 
                                            t.end_work, 
                                            t.curator_id, 
                                            u.firstname as curator_first_name, 
                                            u.lastname as curator_last_name 
                                        FROM tasks t JOIN users u ON t.curator_id = u.id 
                                        WHERE t.student_id = ? AND t.id = ? 
                                        ORDER BY t.id %s
                                  """, order);

        return executeStudentTaskQuery(sql, studentId, taskId);
    }

    private List<TaskWithCuratorDTO> executeStudentTaskQuery(String sql, Object... params) throws SQLException {
        List<TaskWithCuratorDTO> tasks = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TaskWithCuratorDTO task = new TaskWithCuratorDTO(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        StatusTask.valueOf(rs.getString("status")),
                        rs.getTimestamp("deadline").toLocalDateTime(),
                        rs.getTimestamp("start_work") != null ?
                                rs.getTimestamp("start_work").toLocalDateTime() : null,
                        rs.getTimestamp("end_work") != null ?
                                rs.getTimestamp("end_work").toLocalDateTime() : null,
                        rs.getLong("curator_id"),
                        rs.getString("curator_first_name"),
                        rs.getString("curator_last_name")
                );
                tasks.add(task);
            }
        }
        return tasks;
    }

}
