package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;

@Repository
@Transactional
@EnableTransactionManagement
public interface AssignedDevRepository extends JpaRepository<AssignedDev, AssignedDevId> {

    // ---------- GET AssignedDev by IDs ----------
    // ToDoItemId
    @Query("SELECT d FROM AssignedDev d WHERE d.id.toDoItemId = :toDoItemId")
    List<AssignedDev> findByToDoItemId(@Param("toDoItemId") Integer toDoItemId);

    // EmployeeId
    @Query("SELECT d FROM AssignedDev d WHERE d.id.employeeId = :employeeId")
    List<AssignedDev> findByEmployeeId(@Param("employeeId") Integer employeeId);
    
    // ToDoItemId and EmployeeId  
    @Query("SELECT d FROM AssignedDev d WHERE d.id.toDoItemId = :toDoItemId AND d.id.employeeId = :employeeId")
    Optional<AssignedDev> findByToDoItemIdAndEmployeeId(@Param("toDoItemId") Integer toDoItemId, @Param("employeeId") Integer employeeId);
    
    // ---------- DELETE AssignedDev by IDs ----------
    // ToDoItemId
    @Modifying
    @Query(value = "DELETE FROM ASSIGNEDDEV WHERE TODOITEM_ID = :toDoItemId", nativeQuery = true)
    void deleteByToDoItemId(@Param("toDoItemId") Integer id);
    
    // EmployeeId
    @Modifying
    @Query(value = "DELETE FROM ASSIGNEDDEV WHERE EMPLOYEE_ID = :employeeId", nativeQuery = true)
    void deleteByEmployeeId(@Param("employeeId") Integer id);
    
    // ToDoItemId and EmployeeId
    @Modifying
    @Query(value = "DELETE FROM ASSIGNEDDEV WHERE TODOITEM_ID = :toDoItemId AND EMPLOYEE_ID = :employeeId", nativeQuery = true)
    void deleteByToDoItemIdAndEmployeeId(@Param("toDoItemId") Integer toDoItemId, @Param("employeeId") Integer employeeId);
}
