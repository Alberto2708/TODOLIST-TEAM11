package com.springboot.MyTodoList.repository;


import com.springboot.MyTodoList.model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.http.ResponseEntity;
import java.util.List;
import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface ToDoItemRepository extends JpaRepository<ToDoItem,Integer> {
    
    @Query("SELECT d FROM ToDoItem d WHERE d.id.managerId = :managerId AND d.id.sprintId = :sprintId")
    List<ToDoItem> findByManagerIdAndSprintId(@Param("managerId") Integer managerId, @Param("sprintId") Integer sprintId);


}
