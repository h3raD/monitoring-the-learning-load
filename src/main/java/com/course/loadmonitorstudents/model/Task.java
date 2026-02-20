package com.course.loadmonitorstudents.model;

import java.time.LocalDateTime;

public class Task {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime addTime;
    private LocalDateTime deadline;
    private LocalDateTime startWork;
    private LocalDateTime endWork;
    private StatusTask status;
    private Long studentId;
    private Long curatorId;

    /**
     * Конструктор по умолчанию.
     */
    public Task() {}

    /**
     * Конструктор с инициализацией всех полей.
     *
     * @param id идентификатор задачи
     * @param title название задачи
     * @param description описание задачи
     * @param addTime время добавления задачи
     * @param deadline срок выполнения
     * @param startWork время начала работы
     * @param endWork время окончания работы
     * @param status статус выполнения задачи
     * @param studentId идентификатор студента
     * @param curatorId идентификатор куратора
     */
    public Task(Long id, String title, String description, LocalDateTime addTime,
                LocalDateTime deadline, LocalDateTime startWork, LocalDateTime endWork,
                StatusTask status, Long studentId, Long curatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.addTime = addTime;
        this.deadline = deadline;
        this.startWork = startWork;
        this.endWork = endWork;
        this.status = status;
        this.studentId = studentId;
        this.curatorId = curatorId;
    }

    /**
     * Получает идентификатор задачи.
     *
     * @return ID задачи
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор задачи.
     *
     * @param id ID задачи
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Получает название задачи.
     *
     * @return название задачи
     */
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает название задачи.
     *
     * @param title название задачи
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Получает описание задачи.
     *
     * @return описание
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание задачи.
     *
     * @param description описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Получает время при котором была добавлена задача.
     *
     * @return время добавления
     */
    public LocalDateTime getAddTime() {
        return addTime;
    }

    /**
     * Устанавливает время добавления задачи.
     *
     * @param addTime время добавления
     */
    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    /**
     * Получает срок выполнения задачи.
     *
     * @return крайний срок выполнения
     */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    /**
     * Устанавливает срок выполнения задачи.
     *
     * @param deadline крайний срок
     */
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    /**
     * Получает время начала работы по задаче.
     *
     * @return время начала работы
     */
    public LocalDateTime getStartWork() {
        return startWork;
    }

    /**
     * Устанавливает время начала работы.
     *
     * @param startWork время начала работы
     */
    public void setStartWork(LocalDateTime startWork) {
        this.startWork = startWork;
    }

    /**
     * Получает время окончания работы по задаче.
     *
     * @return время окончания
     */
    public LocalDateTime getEndWork() {
        return endWork;
    }

    /**
     * Устанавливает время окончания работы.
     *
     * @param endWork время окончания
     */
    public void setEndWork(LocalDateTime endWork) {
        this.endWork = endWork;
    }

    /**
     * Получает текущий статус задачи.
     *
     * @return статус задачи
     */
    public StatusTask getStatus() {
        return status;
    }

    /**
     * Устанавливает статус задачи.
     *
     * @param status новый статус
     */
    public void setStatus(StatusTask status) {
        this.status = status;
    }

    /**
     * Получает идентификатор студента.
     *
     * @return student ID
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Устанавливает идентификатор студента.
     *
     * @param studentId student ID
     */
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    /**
     * Получает идентификатор куратора.
     *
     * @return curator ID
     */
    public Long getCuratorId() {
        return curatorId;
    }

    /**
     * Устанавливает идентификатор куратора.
     *
     * @param curatorId curator ID
     */
    public void setCuratorId(Long curatorId) {
        this.curatorId = curatorId;
    }
}
