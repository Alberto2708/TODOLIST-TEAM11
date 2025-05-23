// Repository interface for Employee entity
package com.springboot.MyTodoList.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.Employee;

// Provides methods to perform CRUD operations and custom queries
@Repository
@Transactional
@EnableTransactionManagement
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // ---------- GET employees by attributes ----------
    // Project ID
    List<Employee> findByProjectId(Integer projectId);

    // Manager ID
    List<Employee> findByManagerId(Integer managerId);

    // Email
    Employee findByEmail(String email);

    // Telegram ID
    Employee findByTelegramId(Long telegramId);

}
