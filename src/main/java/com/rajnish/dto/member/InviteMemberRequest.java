package com.rajnish.dto.member;

import com.rajnish.common.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
