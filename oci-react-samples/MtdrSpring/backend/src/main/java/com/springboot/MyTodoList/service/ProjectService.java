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
    private ProjectRepository ProjectRepository;

    public List<Project> findAll() {
        List<Project> Projects = ProjectRepository.findAll();
        return Projects;
    }

    public ResponseEntity<Project> getProjectById(int id) {
        Optional<Project> ProjectData = ProjectRepository.findById(id);
        if (ProjectData.isPresent()) {
            return new ResponseEntity<>(ProjectData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public Project addProject(Project Project) {
        return ProjectRepository.save(Project);
    }

    public boolean deleteProject(int id) {
        try {
            ProjectRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Project updateProject(int id, Project p) {
        Optional<Project> ProjectData = ProjectRepository.findById(id);
        if (ProjectData.isPresent()) {
            Project Project = ProjectData.get();
            Project.setID(id);
            Project.setName(p.getName());
            return ProjectRepository.save(Project);
        } else {
            return null;
        }
    }

}
