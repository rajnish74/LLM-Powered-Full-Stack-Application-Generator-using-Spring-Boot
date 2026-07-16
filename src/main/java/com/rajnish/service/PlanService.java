package com.rajnish.service;

import com.rajnish.dto.subscriptions.PlanResponse;

import java.util.List;

public interface PlanService {
    List<PlanResponse> getAllActivePlan();
}
