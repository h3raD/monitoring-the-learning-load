package com.course.loadmonitorstudents.dto;

import com.course.loadmonitorstudents.model.StatusTask;
import java.time.LocalDateTime;

public class TaskWithCuratorDTO {
    private Long id;
    private String title;
    private String description;
    private StatusTask status;
    private LocalDateTime deadline;
    private LocalDateTime startWork;
    private LocalDateTime endWork;
    private Long curatorId;
    private String curatorFirstName;
    private String curatorLastName;

    public TaskWithCuratorDTO(Long id, String title, String description, StatusTask status,
                              LocalDateTime deadline, LocalDateTime startWork, LocalDateTime endWork,
                              Long curatorId, String curatorFirstName, String curatorLastName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.startWork = startWork;
        this.endWork = endWork;
        this.curatorId = curatorId;
        this.curatorFirstName = curatorFirstName;
        this.curatorLastName = curatorLastName;
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

    public Long getCuratorId() { return
            curatorId;
    }

    public String getCuratorFirstName() {
        return curatorFirstName;
    }

    public String getCuratorLastName() {
        return curatorLastName;
    }


    public String getCuratorFullName() {
        return curatorLastName + " " + curatorFirstName;
    }
}