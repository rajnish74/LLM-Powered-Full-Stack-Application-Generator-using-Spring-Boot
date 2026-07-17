package com.rajnish.service.impl;

import com.rajnish.dto.subscriptions.PlanLimitResponse;
import com.rajnish.dto.subscriptions.UsageTodayResponse;
import com.rajnish.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
