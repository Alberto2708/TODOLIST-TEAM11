package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List <Employee> findByProjectId(int projectId);
    List <Employee> findByManagerId(int managerId);
}
