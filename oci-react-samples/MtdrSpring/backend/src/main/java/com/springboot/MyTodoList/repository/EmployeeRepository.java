package com.springboot.MyTodoList.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.Employee;

@Repository
@Transactional
@EnableTransactionManagement
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List <Employee> findByProjectId(Integer projectId);
    List <Employee> findByManagerId(Integer managerId);
    Employee findByEmail(String email);
    Employee findByTelegramId(Long telegramId);
    
}
