package com.springboot.MyTodoList.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;



@Repository
@Transactional
@EnableTransactionManagement
public interface SubToDoItemRepository extends JpaRepository<SubToDoItem, SubToDoItemId> {
    @Query("SELECT d FROM SubToDoItem d WHERE d.id.subToDoItemId = :subToDoItemId")
    List<SubToDoItem> findBySubToDoItemId(Integer subToDoItemId);

    @Query("SELECT d.id.subToDoItemId FROM SubToDoItem d WHERE d.id.toDoItemId = :toDoItemId")
    List<Integer> findAllSubToDoItemIdsByToDoItemId(@Param("toDoItemId") Integer toDoItemId);

    @Query("SELECT d FROM SubToDoItem d WHERE d.id.toDoItemId = :toDoItemId")
    List<SubToDoItem> findAllSubToDoItemsByToDoItemId(@Param("toDoItemId") Integer toDoItemId);

    @Modifying
    @Query(value = "DELETE FROM SUBTODOITEM WHERE TODOITEM_ID = :toDoItemId AND SUBTODOITEM_ID = :subToDoItemId", nativeQuery = true)
    void deleteByToDoItemIdAndSubToDoItemId(@Param("toDoItemId") Integer toDoItemId, @Param("subToDoItemId") Integer subToDoItemId);

    @Modifying
    @Query(value = "DELETE FROM SUBTODOITEM WHERE SUBTODOITEM_ID = :subToDoItemId", nativeQuery = true)
    void deleteBySubToDoItemId(@Param("subToDoItemId") Integer id);

    @Modifying
    @Query(value = "DELETE FROM SUBTODOITEM WHERE TODOITEM_ID = :toDoItemId", nativeQuery = true)
    void deleteByToDoItemId(@Param("toDoItemId") Integer id);
}