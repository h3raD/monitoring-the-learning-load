package com.course.loadmonitorstudents.dao;

import com.course.loadmonitorstudents.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    void create(User user) throws Exception;
    void update(User user) throws Exception;
    void delete(Long id) throws Exception;
    User findIdByEmailAndPassword(String mail, String password) throws Exception;
    User findByIdAndCuratorId(Long id, Long curatorId) throws Exception;
    User findById(Long id) throws Exception;
    List<User> findAllStudentsByCuratorId(Long curatorId) throws Exception;
}
