package com.course.loadmonitorstudents.dto;

import com.course.loadmonitorstudents.model.StatusTask;

import java.time.LocalDateTime;

public class TaskWithStudentDTO {
    private Long id;
    private String title;
    private String description;
    private StatusTask status;
    private LocalDateTime deadline;
    private LocalDateTime startWork;
    private LocalDateTime endWork;
    private String studentLastName;
    private String studentFirstName;
    private Long studentId;

    public TaskWithStudentDTO(Long id, String title, String description, StatusTask status,
                           LocalDateTime deadline, LocalDateTime startWork, LocalDateTime endWork,
                           String studentLastName, String studentFirstName, Long studentId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.startWork = startWork;
        this.endWork = endWork;
        this.studentLastName = studentLastName;
        this.studentFirstName = studentFirstName;
        this.studentId = studentId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public StatusTask getStatus() {
        return status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public LocalDateTime getStartWork() {
        return startWork;
    }

    public LocalDateTime getEndWork() {
        return endWork;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getDeadlineString() {
        return deadline != null ? deadline.toString() : "";
    }

    public String getStartWorkString() {
        return startWork != null ? startWork.toString() : "";
    }

    public String getEndWorkString() {
        return endWork != null ? endWork.toString() : "";
    }

    public String getStatusString() {
        return status != null ? status.toString() : "";
    }
}