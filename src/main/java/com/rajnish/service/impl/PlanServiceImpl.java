package com.rajnish.service.impl;

import com.rajnish.dto.subscriptions.PlanResponse;
import com.rajnish.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlan() {
        return List.of();
    }
}
