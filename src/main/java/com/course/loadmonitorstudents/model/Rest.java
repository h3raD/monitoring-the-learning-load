package com.course.loadmonitorstudents.model;

import java.time.LocalDate;

public class Rest {
    private Long id;
    private LocalDate date;
    private Long studentId;

    public Rest() {}

    public Rest(Long id, LocalDate date, Long studentId) {
        this.id = id;
        this.date = date;
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
}
