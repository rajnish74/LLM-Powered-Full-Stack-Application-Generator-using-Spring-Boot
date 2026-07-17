package com.rajnish.service.impl;

import com.rajnish.dto.subscriptions.CheckoutRequest;
import com.rajnish.dto.subscriptions.CheckoutResponse;
import com.rajnish.dto.subscriptions.PortalResponse;
import com.rajnish.dto.subscriptions.SubscriptionResponse;
import com.rajnish.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
