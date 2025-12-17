package com.course.loadmonitorstudents.dao;

import com.course.loadmonitorstudents.dto.TaskWithCuratorDTO;
import com.course.loadmonitorstudents.dto.TaskWithStudentDTO;
import com.course.loadmonitorstudents.model.StatusTask;
import com.course.loadmonitorstudents.model.Task;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskDAO {
    void create(Task task) throws Exception;
    void update(Task task) throws Exception;
    void delete(Long id) throws Exception;
    LocalDateTime getDeadline(Long id) throws Exception;
    Task getById(Long id) throws Exception;
    Task getTaskByIdAndTaskId(Long idTask, Long id) throws Exception;
    List<TaskWithStudentDTO> findAllTasksWithStudentByCuratorId(Long curatorId) throws Exception;
    List<TaskWithCuratorDTO> findAllTasksByStudentId(Long studentId) throws Exception;
    List<TaskWithCuratorDTO> findTaskByIdForStudent(Long studentId, Long taskId, boolean descending) throws Exception;
    List<TaskWithCuratorDTO> findTasksByCuratorNameForStudent(Long studentId, String searchName, boolean descending) throws Exception;
    List<TaskWithCuratorDTO> findTasksByStatusForStudent(Long studentId, String status, boolean descending) throws Exception;
    List<TaskWithCuratorDTO> findTasksByDeadlineForStudent(Long studentId, boolean descending) throws Exception;
    List<TaskWithStudentDTO> findTasksByStatus(Long curatorId, String status, boolean descending) throws Exception;
    List<TaskWithStudentDTO> findTasksByDeadline(Long curatorId, boolean descending) throws Exception;
    List<TaskWithStudentDTO> findTasksByStudentName(Long curatorId, String searchName, boolean descending) throws Exception;
    List<TaskWithStudentDTO> findTasksById(Long curatorId, Long taskId, boolean descending) throws Exception;
}
