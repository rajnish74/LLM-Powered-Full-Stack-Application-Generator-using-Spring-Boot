package com.rajnish.dto.member;

import com.rajnish.common.enums.ProjectRole;
import org.antlr.v4.runtime.misc.NotNull;

public record InviteMemberRequest(
        String username,
        ProjectRole role
) {
}
