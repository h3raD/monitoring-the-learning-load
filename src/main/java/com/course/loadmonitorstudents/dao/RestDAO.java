package com.course.loadmonitorstudents.dao;

import com.course.loadmonitorstudents.model.Rest;

import java.util.List;

public interface RestDAO {
    /**
     * Получает все записи об отдыхе студента.
     *
     * @param studentId ID студента
     * @return список записей об отдыхе
     * @throws Exception ошибка
     */
    List<Rest> findByStudentId(Long studentId) throws Exception;

    /**
     * Создает новую запись об отдыхе.
     *
     * @param rest объект для сохранения
     * @throws Exception ошибка
     */
    void create(Rest rest) throws Exception;

    /**
     * Обновляет запись об отдыхе.
     *
     * @param rest объект с новыми данными
     * @throws Exception ошибка
     */
    void update(Rest rest) throws Exception;

    /**
     * Удаляет запись об отдыхе по ID.
     *
     * @param id ID записи
     * @throws Exception ошибка
     */
    void delete(Long id) throws Exception;
}
