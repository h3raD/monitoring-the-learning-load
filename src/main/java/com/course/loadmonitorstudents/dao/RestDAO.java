package com.course.loadmonitorstudents.dao;

import com.course.loadmonitorstudents.model.Rest;

public interface RestDAO {
    void create(Rest rest) throws Exception;
    void update(Rest rest) throws Exception;
    void delete(Long id) throws Exception;
}
