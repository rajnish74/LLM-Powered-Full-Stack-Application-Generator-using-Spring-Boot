package com.rajnish.service.impl;

import com.rajnish.dto.member.InviteMemberRequest;
import com.rajnish.dto.member.MemberResponse;
import com.rajnish.dto.member.UpdateMemberRoleRequest;
import com.rajnish.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {
    @Override
    public List<MemberResponse> getProjectMembers(Long id, Long userId) {
        return List.of();
    }

    @Override
    public MemberResponse inviteMember(Long id, InviteMemberRequest request, Long userId) {
        return null;
    }

    @Override
    public MemberResponse updateMemberRole(Long id, Long memberId, UpdateMemberRoleRequest request, Long userId) {
        return null;
    }

    @Override
    public void deleteMember(Long id, Long memberId, Long userId) {

    }
}
