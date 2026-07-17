package com.rajnish.service.impl;

import com.rajnish.dto.project.request.ProjectRequest;
import com.rajnish.dto.project.response.ProjectResponse;
import com.rajnish.dto.project.response.ProjectSummaryResponse;
import com.rajnish.entity.Project;
import com.rajnish.entity.User;
import com.rajnish.mapper.ProjectMapper;
import com.rajnish.repository.ProjectRepository;
import com.rajnish.repository.UserRepository;
import com.rajnish.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper  projectMapper;

    @Override
    public ProjectResponse createProject(Long userId, ProjectRequest request) {
        User owner = userRepository.findById(userId).orElseThrow();

        Project project = Project.builder()
                .name(request.name())
                .owner(owner)
                .isPublic(false)
                .build();

        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {

//        return projectRepository.findAllAccessibleByUser(userId)
//                .stream()
//                .map(project->projectMapper.toProjectSummaryResponse(project))
//                .collect(Collectors.toList());

        var projects=projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toListOfProjectSummaryResponse(projects);
    }

    @Override
    public ProjectResponse getProjectById(Long userId, Long id) {
        return null;
    }



    @Override
    public ProjectResponse updateProject(Long userId, Long id, ProjectRequest request) {
        return null;
    }

    @Override
    public void softDelete(Long userId, Long id) {

    }
}
