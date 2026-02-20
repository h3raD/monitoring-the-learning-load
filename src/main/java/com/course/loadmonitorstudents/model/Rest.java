package com.course.loadmonitorstudents.model;

import java.time.LocalDate;

public class Rest {
    private Long id;
    private LocalDate date;
    private Integer hours;
    private Long studentId;

    /**
     * Конструктор по умолчанию.
     */
    public Rest() {}

    /**
     * Конструктор с инициализацией всех полей.
     *
     * @param id идентификатор записи об отдыхе
     * @param date дата отдыха
     * @param hours количество часов отдыха/сна
     * @param studentId идентификатор студента
     */
    public Rest(Long id, LocalDate date, Integer hours, Long studentId) {
        this.id = id;
        this.date = date;
        this.hours = hours;
        this.studentId = studentId;
    }

    /**
     * Получает ID рекорда.
     *
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает ID рекорда.
     *
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Получает дату отдыха.
     *
     * @return дата отдыха
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Устанавливает дату отдыха.
     *
     * @param date дата отдыха
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Получает ID студента.
     *
     * @return student ID
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Устанавливает ID студента.
     *
     * @param studentId student ID
     */
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    /**
     * Получает количество часов отдыха/сна.
     *
     * @return количество часов
     */
    public Integer getHours() {
        return hours;
    }

    /**
     * Устанавливает количество часов отдыха/сна.
     *
     * @param hours количество часов
     */
    public void setHours(Integer hours) {
        this.hours = hours;
    }
}
