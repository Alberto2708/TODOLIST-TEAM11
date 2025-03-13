package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    
    /**
     * Find all projects
     */
    public List<Project> findAll() {
        return projectRepository.findAll();
    }
    
    /**
     * Find project by ID
     */
    public ResponseEntity<Project> getProjectById(int id) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()) {
            return new ResponseEntity<>(projectData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Add a new project
     */
    public Project addProject(Project project) {
        return projectRepository.save(project);
    }
    
    /**
     * Delete a project by ID
     */
    public boolean deleteProject(int id) {
        try {
            projectRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Update an existing project
     */
    public Project updateProject(int id, Project project) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()) {
            Project existingProject = projectData.get();
            existingProject.setId(id);
            
            if (project.getName() != null) {
                existingProject.setName(project.getName());
            }
            
            return projectRepository.save(existingProject);
        } else {
            return null;
        }
    }
} 