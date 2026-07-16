package com.rajnish.service;

import com.rajnish.dto.project.request.ProjectRequest;
import com.rajnish.dto.project.response.ProjectResponse;
import com.rajnish.dto.project.response.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects(Long userId);

    ProjectResponse getProjectById(Long userId, Long id);

    ProjectResponse createProject(Long userId, ProjectRequest request);

    ProjectResponse updateProject(Long userId, Long id, ProjectRequest request);

    void softDelete(Long userId, Long id);
}
