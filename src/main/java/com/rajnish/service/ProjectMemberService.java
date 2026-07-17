package com.rajnish.service;

import com.rajnish.dto.member.InviteMemberRequest;
import com.rajnish.dto.member.MemberResponse;
import com.rajnish.dto.member.UpdateMemberRoleRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long id, Long userId);

    MemberResponse inviteMember(Long id,  InviteMemberRequest request, Long userId);

    MemberResponse updateMemberRole(Long id, Long memberId, UpdateMemberRoleRequest request, Long userId);

    void deleteMember(Long id, Long memberId, Long userId);
}
