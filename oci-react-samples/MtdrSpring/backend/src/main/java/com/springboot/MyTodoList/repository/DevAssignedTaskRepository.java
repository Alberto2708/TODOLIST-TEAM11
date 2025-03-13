package com.springboot.MyTodoList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.MyTodoList.model.DevAssignedTask;
import com.springboot.MyTodoList.model.DevAssignedTaskId;
import java.util.List;

@Repository
public interface DevAssignedTaskRepository extends JpaRepository<DevAssignedTask, DevAssignedTaskId> {
    List<DevAssignedTask> findByTodoItemId(int todoItemId);
    List<DevAssignedTask> findByAssignedDevId(int assignedDevId);
} 