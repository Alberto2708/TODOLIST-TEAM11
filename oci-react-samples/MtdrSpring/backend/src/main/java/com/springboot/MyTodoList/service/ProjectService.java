package com.springboot.MyTodoList.service;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.springboot.MyTodoList.repository.ProjectRepository;
import com.springboot.MyTodoList.model.Project;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> findAll(){
        List<Project> projects = projectRepository.findAll();
        return projects;
    }

    public Project findProjectById(Integer id) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()){
            return projectData.get();
        }else{
            return null;
        }
    }

    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject (Integer id, Project newProject){
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()){
            Project project = projectData.get();
            project.setID(id);
            project.setName(newProject.getName());
            return projectRepository.save(project);
        }else{
            return null;
        }
    }

    public Boolean deleteProject(Integer projectId){
        try {
            projectRepository.deleteById(projectId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
