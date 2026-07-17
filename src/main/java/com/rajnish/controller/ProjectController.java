package com.rajnish.controller;

import com.rajnish.dto.project.request.ProjectRequest;
import com.rajnish.dto.project.response.ProjectResponse;
import com.rajnish.dto.project.response.ProjectSummaryResponse;
import com.rajnish.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getProjects(){
        Long userId=1L;
        return ResponseEntity.ok(projectService.getUserProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id){
        Long userId=1L;
        return ResponseEntity.ok(projectService.getProjectById(userId,id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest request){
        Long userId=1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(userId,request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectRequest request){
        Long userId=1L;
        return ResponseEntity.ok(projectService.updateProject(userId,id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
        Long userId=1L;
        projectService.softDelete(userId,id);
        return ResponseEntity.noContent().build();
    }
}
