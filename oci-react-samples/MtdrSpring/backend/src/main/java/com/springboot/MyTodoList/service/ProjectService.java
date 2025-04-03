package com.springboot.MyTodoList.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.springboot.MyTodoList.repository.ProjectRepository;
import com.springboot.MyTodoList.model.Project;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public Project findProjectById(Integer id) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()){
            return projectData.get();
        }else{
            return null;
        }
    }
}
