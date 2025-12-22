package com.course.loadmonitorstudents.dao;

import com.course.loadmonitorstudents.model.Rest;

import java.util.List;

public interface RestDAO {
    List<Rest> findByStudentId(Long studentId) throws Exception;
    void create(Rest rest) throws Exception;
    void update(Rest rest) throws Exception;
    void delete(Long id) throws Exception;
}
