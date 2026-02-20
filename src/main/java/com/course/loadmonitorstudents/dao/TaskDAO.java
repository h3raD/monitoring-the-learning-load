package com.course.loadmonitorstudents.dao;

import com.course.loadmonitorstudents.dto.TaskWithCuratorDTO;
import com.course.loadmonitorstudents.dto.TaskWithStudentDTO;
import com.course.loadmonitorstudents.model.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskDAO {
    /**
     * Сохраняет новую задачу.
     *
     * @param task задача
     * @throws Exception ошибка
     */
    void create(Task task) throws Exception;

    /**
     * Обновляет задачу.
     *
     * @param task задача с новыми данными
     * @throws Exception ошибка
     */
    void update(Task task) throws Exception;

    /**
     * Удаляет задачу.
     *
     * @param id ID задачи
     * @throws Exception ошибка
     */
    void delete(Long id) throws Exception;

    /**
     * Получает срок выполнения задачи.
     *
     * @param id ID задачи
     * @return срок выполнения
     * @throws Exception ошибка
     */
    LocalDateTime getDeadline(Long id) throws Exception;

    /**
     * Получает задачу по ID.
     *
     * @param id ID задачи
     * @return задача
     * @throws Exception ошибка
     */
    Task getById(Long id) throws Exception;

    /**
     * Получает задачу по ID задачи и ID.
     *
     * @param idTask ID задачи
     * @param id ID 
     * @return задача
     * @throws Exception ошибка
     */
    Task getTaskByIdAndTaskId(Long idTask, Long id) throws Exception;

    /**
     * Получает все задачи куратора со студентами.
     *
     * @param curatorId ID куратора
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithStudentDTO> findAllTasksWithStudentByCuratorId(Long curatorId) throws Exception;

    /**
     * Получает все задачи студента с данными куратора.
     *
     * @param studentId ID студента
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithCuratorDTO> findAllTasksByStudentId(Long studentId) throws Exception;

    /**
     * Находит задачу для студента по ID.
     *
     * @param studentId ID студента
     * @param taskId ID задачи
     * @param descending сортировка по убыванию
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithCuratorDTO> findTaskByIdForStudent(Long studentId, Long taskId, boolean descending) throws Exception;

    /**
     * Находит задачи для студента по имени куратора.
     *
     * @param studentId ID студента
     * @param searchName имя ндя поиска
     * @param descending сортировка по убыванию
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithCuratorDTO> findTasksByCuratorNameForStudent(Long studentId, String searchName, boolean descending) throws Exception;

    /**
     * Находит задачи для студента по статусу.
     *
     * @param studentId ID студента
     * @param status статус
     * @param descending сортировка по убыванию
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithCuratorDTO> findTasksByStatusForStudent(Long studentId, String status, boolean descending) throws Exception;

    /**
     * Находит задачи для студента по дедлайну.
     *
     * @param studentId ID студента
     * @param descending сортировка по убыванию
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithCuratorDTO> findTasksByDeadlineForStudent(Long studentId, boolean descending) throws Exception;

    /**
     * Находит задачи куратора по статусу.
     *
     * @param curatorId ID куратора
     * @param status статус
     * @param descending сортировка по убыванию
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithStudentDTO> findTasksByStatus(Long curatorId, String status, boolean descending) throws Exception;

    /**
     * Находит задачи куратора по дедлайну.
     *
     * @param curatorId ID куратора
     * @param descending сортировка по убыванию
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithStudentDTO> findTasksByDeadline(Long curatorId, boolean descending) throws Exception;

    /**
     * Находит задачи куратора по имени студента.
     *
     * @param curatorId ID куратора
     * @param searchName имя нля поиска
     * @param descending сортировка по убыванию
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithStudentDTO> findTasksByStudentName(Long curatorId, String searchName, boolean descending) throws Exception;

    /**
     * Находит задачи куратора по ID.
     *
     * @param curatorId ID куратора
     * @param taskId ID задачи
     * @param descending сортировка по убыванию
     * @return лист задач
     * @throws Exception ошибка
     */
    List<TaskWithStudentDTO> findTasksById(Long curatorId, Long taskId, boolean descending) throws Exception;
}
