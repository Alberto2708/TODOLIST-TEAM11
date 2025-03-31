package com.springboot.MyTodoList.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;



@Repository
@Transactional
@EnableTransactionManagement
public interface SubToDoItemRepository extends JpaRepository<SubToDoItem, SubToDoItemId> {
    
}