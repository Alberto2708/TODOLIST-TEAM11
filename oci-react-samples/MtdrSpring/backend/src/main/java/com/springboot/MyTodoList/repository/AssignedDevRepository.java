package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
public interface AssignedDevRepository extends JpaRepository<AssignedDev, AssignedDevId> {

    @Query("SELECT d FROM AssignedDev d WHERE d.id.toDoItemId = :toDoItemId")
    List<AssignedDev> findByToDoItemId(@Param("toDoItemId") Integer toDoItemId);

    @Query("SELECT d FROM AssignedDev d WHERE d.id.employeeId = :employeeId")
    List<AssignedDev> findByEmployeeId(@Param("employeeId") Integer employeeId);

    @Modifying
    @Query(value = "DELETE FROM ASSIGNEDDEV WHERE TODOITEM_ID = :toDoItemId AND EMPLOYEE_ID = :employeeId", nativeQuery = true)
    void deleteByToDoItemIdAndEmployeeId(@Param("toDoItemId") Integer toDoItemId, @Param("employeeId") Integer employeeId);

    @Query("SELECT d FROM AssignedDev d WHERE d.id.toDoItemId = :toDoItemId AND d.id.employeeId = :employeeId")
    Optional<AssignedDev> findByToDoItemIdAndEmployeeId(@Param("toDoItemId") Integer toDoItemId, @Param("employeeId") Integer employeeId);

    @Modifying
    @Query(value = "DELETE FROM ASSIGNEDDEV WHERE TODOITEM_ID = :toDoItemId", nativeQuery = true)
    void deleteByToDoItemId(@Param("toDoItemId") Integer id);
}