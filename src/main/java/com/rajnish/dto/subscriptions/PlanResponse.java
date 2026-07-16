package com.rajnish.dto.subscriptions;

public record PlanResponse(
        Long id,
        String name,
        Integer maxProject,
        Integer maxTokenPerDay,
        Boolean unlimitedAi,
        String price
) {
}
