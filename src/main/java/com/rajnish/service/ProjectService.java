package com.rajnish.service;

import com.rajnish.dto.project.request.ProjectRequest;
import com.rajnish.dto.project.response.ProjectResponse;
import com.rajnish.dto.project.response.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectResponse getProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete( Long id);
}
