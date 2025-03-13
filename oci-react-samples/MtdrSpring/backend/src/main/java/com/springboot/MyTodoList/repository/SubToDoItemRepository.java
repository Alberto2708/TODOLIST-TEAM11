package com.springboot.MyTodoList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;
import java.util.List;

@Repository
public interface SubToDoItemRepository extends JpaRepository<SubToDoItem, SubToDoItemId> {
    List<SubToDoItem> findByTodoItemId(int todoItemId);
    List<SubToDoItem> findBySubTodoItemId(int subTodoItemId);
} 