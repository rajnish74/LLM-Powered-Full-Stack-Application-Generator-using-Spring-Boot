package com.rajnish.dto.member;

import com.rajnish.common.enums.ProjectRole;

public record UpdateMemberRoleRequest(
        ProjectRole role
) {
}
