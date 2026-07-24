package com.rajnish.controller;


import com.rajnish.dto.member.InviteMemberRequest;
import com.rajnish.dto.member.MemberResponse;
import com.rajnish.dto.member.UpdateMemberRoleRequest;
import com.rajnish.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{id}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long id){
        return ResponseEntity.ok(projectMemberService.getProjectMembers(id));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable Long id,
            @RequestBody @Valid InviteMemberRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberService.inviteMember(id,request));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long memberId,
            @RequestBody @Valid UpdateMemberRoleRequest request
    ){
        return ResponseEntity.ok(projectMemberService.updateMemberRole(id,memberId,request));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeProjectMember(@PathVariable Long id, @PathVariable Long memberId){
        projectMemberService.removeProjectMember(id,memberId);
        return ResponseEntity.noContent().build();
    }
}
