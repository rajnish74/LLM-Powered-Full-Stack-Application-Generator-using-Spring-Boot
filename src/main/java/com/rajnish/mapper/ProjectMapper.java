package com.rajnish.mapper;

import com.rajnish.dto.project.response.ProjectResponse;
import com.rajnish.dto.project.response.ProjectSummaryResponse;
import com.rajnish.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    @Mapping(source = "name", target = "projectName")
    ProjectSummaryResponse  toProjectSummaryResponse(Project project);

    List<ProjectSummaryResponse>  toListOfProjectSummaryResponse(List<Project> projects);
}
