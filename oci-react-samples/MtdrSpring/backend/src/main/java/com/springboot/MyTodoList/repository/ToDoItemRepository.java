package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ToDoItemRepository extends JpaRepository<ToDoItem, Integer> {
    List<ToDoItem> findByStatus(String status);
    List<ToDoItem> findByManagerId(Integer managerId);
    List<ToDoItem> findByProjectId(Integer projectId);
    List<ToDoItem> findByDeadlineBefore(LocalDate date);
    List<ToDoItem> findByManagerIdAndStatus(Integer managerId, String status);
    List<ToDoItem> findByProjectIdAndStatus(Integer projectId, String status);
}
