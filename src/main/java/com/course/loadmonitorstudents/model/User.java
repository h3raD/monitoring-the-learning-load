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

    /**
     * Конструктор по умолчанию для создания нового объекта пользователя.
     */
    public User() {}

    /**
     * Конструктор для создания объекта пользователя со всеми параметрами.
     *
     * @param id идентификатор пользователя
     * @param email электронная почта пользователя
     * @param password хешированный пароль
     * @param firstName имя пользователя
     * @param lastName фамилия пользователя
     * @param role роль пользователя (CURATOR или STUDENT)
     * @param curatorId идентификатор куратора (для студентов)
     * @param telegramID идентификатор Telegram аккаунта
     * @param googleCalendarApiKey ключ API для Google Calendar
     */
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

    /**
     * Получает ID пользователя.
     *
     * @return ID пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает ID пользователя.
     *
     * @param id ID пользователя
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Получает email пользователя.
     *
     * @return email адрес
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email email адрес
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Получает хешированный пароль.
     *
     * @return хешированный пароль
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает хешированный пароль.
     *
     * @param password хешированный пароль
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Получает имя пользователя.
     *
     * @return имя
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param firstName имя
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Получает фамилию пользователя.
     *
     * @return фамилия
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию пользователя.
     *
     * @param lastName фамилия
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Получает роль пользователя.
     *
     * @return роль (CURATOR или STUDENT)
     */
    public Role getRole() {
        return role;
    }

    /**
     * Устанавливает роль пользователя.
     *
     * @param role роль (куратор или студент)
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Получает ID куратора.
     *
     * @return ID куратора
     */
    public Long getCuratorId() {
        return curatorId;
    }

    /**
     * Устанавливает ID куратора.
     *
     * @param curatorId ID куратора
     */
    public void setCuratorId(Long curatorId) {
        this.curatorId = curatorId;
    }

    /**
     * Получает ID Telegram аккаунта.
     *
     * @return Telegram ID
     */
    public Long getTelegramID() {
        return telegramID;
    }

    /**
     * Устанавливает ID Telegram аккаунта.
     *
     * @param telegramID Telegram ID
     */
    public void setTelegramID(Long telegramID) {
        this.telegramID = telegramID;
    }

    /**
     * Получает ключ API Google Calendar.
     *
     * @return ключ API
     */
    public String getGoogleCalendarApiKey() {
        return googleCalendarApiKey;
    }

    /**
     * Устанавливает ключ API Google Calendar.
     *
     * @param googleCalendarApiKey ключ API
     */
    public void setGoogleCalendarApiKey(String googleCalendarApiKey) {
        this.googleCalendarApiKey = googleCalendarApiKey;
    }
}
