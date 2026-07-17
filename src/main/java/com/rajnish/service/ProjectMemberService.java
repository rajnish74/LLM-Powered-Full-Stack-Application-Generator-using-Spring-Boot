package com.rajnish.service;

import com.rajnish.dto.member.InviteMemberRequest;
import com.rajnish.dto.member.MemberResponse;
import com.rajnish.dto.member.UpdateMemberRoleRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId, Long userId);

    MemberResponse inviteMember(Long projectId,  InviteMemberRequest request, Long userId);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId);

    void removeProjectMember(Long projectId, Long memberId, Long userId);
}
