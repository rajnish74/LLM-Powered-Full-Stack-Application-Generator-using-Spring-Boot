package com.rajnish.service.impl;

import com.rajnish.common.enums.ProjectRole;
import com.rajnish.common.exceptions.ResourceNotFoundException;
import com.rajnish.common.security.AuthUtils;
import com.rajnish.dto.project.request.ProjectRequest;
import com.rajnish.dto.project.response.ProjectResponse;
import com.rajnish.dto.project.response.ProjectSummaryResponse;
import com.rajnish.entity.Project;
import com.rajnish.entity.ProjectMember;
import com.rajnish.entity.ProjectMemberId;
import com.rajnish.entity.User;
import com.rajnish.mapper.ProjectMapper;
import com.rajnish.repository.ProjectMemberRepository;
import com.rajnish.repository.ProjectRepository;
import com.rajnish.repository.UserRepository;
import com.rajnish.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper  projectMapper;
    private final ProjectMemberRepository  projectMemberRepository;
    private final AuthUtils authUtils;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        Long userId = authUtils.getCurrentUserId();
//        User owner = userRepository.findById(userId).orElseThrow(
//                ()->new ResourceNotFoundException("User not found with id: " + userId, "USER_NOT_FOUND")
//        );
        User owner = userRepository.getReferenceById(userId);

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();

        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember=ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();

        projectMemberRepository.save(projectMember);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtils.getCurrentUserId();

//        return projectRepository.findAllAccessibleByUser(userId)
//                .stream()
//                .map(project->projectMapper.toProjectSummaryResponse(project))
//                .collect(Collectors.toList());

        var projects=projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toListOfProjectSummaryResponse(projects);
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Long userId = authUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(id,userId);
        return projectMapper.toProjectResponse(project);
    }



    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Long userId = authUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(id,userId);


        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id) {
        Long userId = authUtils.getCurrentUserId();
        Project project = getAccessibleProjectById(id,userId);


        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }


//    internal function
    public Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(()-> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
