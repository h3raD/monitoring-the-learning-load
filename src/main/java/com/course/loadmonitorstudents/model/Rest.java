package com.course.loadmonitorstudents.model;

import java.time.LocalDate;

public class Rest {
    private Long id;
    private LocalDate date;
    private Integer hours;
    private Long studentId;

    public Rest() {}

    public Rest(Long id, LocalDate date, Integer hours, Long studentId) {
        this.id = id;
        this.date = date;
        this.hours = hours;
        this.studentId = studentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}
