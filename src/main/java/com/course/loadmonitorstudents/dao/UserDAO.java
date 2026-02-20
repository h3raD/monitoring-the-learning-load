package com.course.loadmonitorstudents.dao;

import com.course.loadmonitorstudents.model.User;

import java.util.List;

public interface UserDAO {
    /**
     * При нового пользователя в базу данных.
     *
     * @param user объект для сохранения
     * @throws Exception ошибка базы
     */
    void create(User user) throws Exception;

    /**
     * Обновляет данные пользователя.
     *
     * @param user объект с новыми данными
     * @throws Exception ошибка базы
     */
    void update(User user) throws Exception;

    /**
     * Удаляет пользователя по ID.
     *
     * @param id ID пользователя
     * @throws Exception ошибка базы
     */
    void delete(Long id) throws Exception;

    /**
     * Находит пользователя по email и паролю.
     *
     * @param mail email пользователя
     * @param password пароль
     * @return найденный пользователь или null
     * @throws Exception ошибка базы
     */
    User findIdByEmailAndPassword(String mail, String password) throws Exception;

    /**
     * Находит студента по ID и ID куратора.
     *
     * @param id ID студента
     * @param curatorId ID куратора
     * @return найденный студент или null
     * @throws Exception ошибка базы
     */
    User findByIdAndCuratorId(Long id, Long curatorId) throws Exception;

    /**
     * Находит пользователя по ID.
     *
     * @param id ID пользователя
     * @return найденный пользователь или null
     * @throws Exception ошибка базы
     */
    User findById(Long id) throws Exception;

    /**
     * Получает всех студентов куратора.
     *
     * @param curatorId ID куратора
     * @return лист студентов
     * @throws Exception ошибка базы
     */
    List<User> findAllStudentsByCuratorId(Long curatorId) throws Exception;

    /**
     * Находит пользователя по email.
     *
     * @param mail email пользователя
     * @return найденный пользователь или null
     * @throws Exception ошибка базы
     */
    User findByEmail(String mail) throws Exception;
}
