// Repository interface for ToDoItem entity
package com.springboot.MyTodoList.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.ToDoItem;

// This interface extends JpaRepository to provide CRUD operations
@Repository
@Transactional
@EnableTransactionManagement
public interface ToDoItemRepository extends JpaRepository<ToDoItem, Integer> {

    // ---------- GET ToDoItems by IDs ----------
    // SprintId
    List<ToDoItem> findBySprintId(@Param("sprintId") Integer sprintId);

    // ManagerId
    List<ToDoItem> findBySprintIdAndStatus(@Param("sprintId") Integer sprintId, @Param("status") String status);

    // ManagerId and SprintId
    @Query("SELECT d FROM ToDoItem d WHERE d.id.managerId = :managerId AND d.id.sprintId = :sprintId")
    List<ToDoItem> findByManagerIdAndSprintId(@Param("managerId") Integer managerId, @Param("sprintId") Integer sprintId);
}
