package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.repository.SubToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubToDoItemService {
    @Autowired
    private SubToDoItemRepository subToDoItemRepository;
    
    public Boolean checkIfIdIsntSubToDoItem(Integer subToDoItemId){
        try{
            if(subToDoItemRepository.findBySubToDoItemId(subToDoItemId).isEmpty()){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            return null;
        }
    }

    public List<Integer> findAllSubToDoItemsByToDoItemId(Integer toDoItemId){
        try{
            return subToDoItemRepository.findAllSubToDoItemIdsByToDoItemId(toDoItemId);
        }catch(Exception e){
            return null;
        }
    }
}
