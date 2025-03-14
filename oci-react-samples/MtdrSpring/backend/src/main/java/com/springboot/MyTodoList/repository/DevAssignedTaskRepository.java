package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.DevAssignedTask;
import com.springboot.MyTodoList.model.DevAssignedTaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@EnableTransactionManagement
public interface DevAssignedTaskRepository extends JpaRepository<DevAssignedTask, DevAssignedTaskId> {

    @Query("SELECT d FROM DevAssignedTask d WHERE d.id.toDoItemId = :toDoItemId")
    List<DevAssignedTask> findByToDoItemId(@Param("toDoItemId") Integer toDoItemId);

    @Query("SELECT d FROM DevAssignedTask d WHERE d.id.assignedDevId = :assignedDevId")
    List<DevAssignedTask> findByAssignedDevId(@Param("assignedDevId") Integer assignedDevId);

    @Query("DELETE FROM DevAssignedTask d WHERE d.id.toDoItemId = :toDoItemId AND d.id.assignedDevId = :assignedDevId")
    void deleteByToDoItemIdAndAssignedDevId(@Param("toDoItemId") Integer toDoItemId, @Param("assignedDevId") Integer assignedDevId);

    @Query("SELECT d FROM DevAssignedTask d WHERE d.id.toDoItemId = :toDoItemId AND d.id.assignedDevId = :assignedDevId")
    Optional<DevAssignedTask> findByToDoItemIdAndAssignedDevId(@Param("toDoItemId") Integer toDoItemId, @Param("assignedDevId") Integer assignedDevId);
}