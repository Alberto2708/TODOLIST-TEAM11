package com.springboot.MyTodoList.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
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
}