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

    public Task() {}

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getStartWork() {
        return startWork;
    }

    public void setStartWork(LocalDateTime startWork) {
        this.startWork = startWork;
    }

    public LocalDateTime getEndWork() {
        return endWork;
    }

    public void setEndWork(LocalDateTime endWork) {
        this.endWork = endWork;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCuratorId() {
        return curatorId;
    }

    public void setCuratorId(Long curatorId) {
        this.curatorId = curatorId;
    }
}
