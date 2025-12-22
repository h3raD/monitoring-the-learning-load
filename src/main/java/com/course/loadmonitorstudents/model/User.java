package com.course.loadmonitorstudents.model;

public class User {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private Long curatorId;
    private Long telegramID;
    private String googleCalendarApiKey;

    public User() {}

    public User(Long id, String email, String password, String firstName, String lastName,
                Role role, Long curatorId, Long telegramID, String googleCalendarApiKey) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.curatorId = curatorId;
        this.telegramID = telegramID;
        this.googleCalendarApiKey = googleCalendarApiKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getCuratorId() {
        return curatorId;
    }

    public void setCuratorId(Long curatorId) {
        this.curatorId = curatorId;
    }

    public Long getTelegramID() {
        return telegramID;
    }

    public void setTelegramID(Long telegramID) {
        this.telegramID = telegramID;
    }

    public String getGoogleCalendarApiKey() {
        return googleCalendarApiKey;
    }

    public void setGoogleCalendarApiKey(String googleCalendarApiKey) {
        this.googleCalendarApiKey = googleCalendarApiKey;
    }
}
