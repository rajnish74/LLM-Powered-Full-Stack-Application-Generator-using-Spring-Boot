package com.rajnish.service;

import com.rajnish.dto.subscriptions.PlanLimitResponse;
import com.rajnish.dto.subscriptions.UsageTodayResponse;

public interface UsageService {
    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
