// Repository interface for SubToDoItem entity
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

import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;

// Repository interface for SubToDoItem entity
// This interface extends JpaRepository to provide CRUD operations
@Repository
@Transactional
@EnableTransactionManagement
public interface SubToDoItemRepository extends JpaRepository<SubToDoItem, SubToDoItemId> {

    // ---------- GET SubToDoItem by IDs ----------
    // SubToDoItemId
    @Query("SELECT d FROM SubToDoItem d WHERE d.id.subToDoItemId = :subToDoItemId")
    List<SubToDoItem> findBySubToDoItemId(Integer subToDoItemId);

    // ToDoItemId (returns subtodoitems
    @Query("SELECT d FROM SubToDoItem d WHERE d.id.toDoItemId = :toDoItemId")
    List<SubToDoItem> findAllSubToDoItemsByToDoItemId(@Param("toDoItemId") Integer toDoItemId);

    // ToDoItemId (returns subToDoItemId list)
    @Query("SELECT d.id.subToDoItemId FROM SubToDoItem d WHERE d.id.toDoItemId = :toDoItemId")
    List<Integer> findAllSubToDoItemIdsByToDoItemId(@Param("toDoItemId") Integer toDoItemId);

    // ToDoItemId and SubToDoItemId
    @Query("SELECT d FROM SubToDoItem d WHERE d.id.toDoItemId = :toDoItemId AND d.id.subToDoItemId = :subToDoItemId")
    Optional<SubToDoItem> findByToDoItemIdAndSubToDoItemId(@Param("toDoItemId") Integer toDoItemId, @Param("subToDoItemId") Integer subToDoItemId);

    // ---------- DELETE SubToDoItem by IDs ----------
    // SubToDoItemId
    @Modifying
    @Query(value = "DELETE FROM SUBTODOITEM WHERE SUBTODOITEM_ID = :subToDoItemId", nativeQuery = true)
    void deleteBySubToDoItemId(@Param("subToDoItemId") Integer id);

    // ToDoItemId
    @Modifying
    @Query(value = "DELETE FROM SUBTODOITEM WHERE TODOITEM_ID = :toDoItemId", nativeQuery = true)
    void deleteByToDoItemId(@Param("toDoItemId") Integer id);

    // ToDoItemId and SubToDoItemId
    @Modifying
    @Query(value = "DELETE FROM SUBTODOITEM WHERE TODOITEM_ID = :toDoItemId AND SUBTODOITEM_ID = :subToDoItemId", nativeQuery = true)
    void deleteByToDoItemIdAndSubToDoItemId(@Param("toDoItemId") Integer toDoItemId, @Param("subToDoItemId") Integer subToDoItemId);
}
