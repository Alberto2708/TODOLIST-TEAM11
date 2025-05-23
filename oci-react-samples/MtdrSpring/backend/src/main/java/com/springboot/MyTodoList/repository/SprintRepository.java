// Repository interface for Sprint entity
package com.springboot.MyTodoList.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.Sprint;

// Provides methods to perform CRUD operations and custom queries for Sprint entities
@Repository
@Transactional
@EnableTransactionManagement
public interface SprintRepository extends JpaRepository<Sprint, Integer> {

    // Finds all sprints by project ID
    List<Sprint> findSprintsByProjectId(Integer projectId);
}
