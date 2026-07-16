package com.rajnish.dto.subscriptions;

public record PlanLimitResponse(
        String planName,
        Integer maxTokenPerday,
        Integer maxProjects,
        Boolean unlimitedAi
) {
}
