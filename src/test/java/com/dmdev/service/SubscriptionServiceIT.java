package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceIT extends IntegrationTestBase {

    private SubscriptionDao subscriptionDao;
    private SubscriptionService subscriptionService;
    private Clock clock;

    @BeforeEach
    void init() {
        subscriptionDao = SubscriptionDao.getInstance();
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                clock
        );
    }

    @Test
    void upsert() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription(13, "Roman"));

        Subscription actualResult = subscriptionService.upsert(getCreateSubscriptionDto(13, "Roman"));

        assertThat(actualResult.getUserId()).isEqualTo(subscription1.getUserId());
    }

    @Test
    void cancel() {
        Subscription subscription = getSubscription(13, "Roman");
        subscriptionDao.insert(subscription);

        subscription.setStatus(Status.CANCELED);

        subscriptionDao.update(subscription);

        Subscription actualResult = subscriptionDao.findById(subscription.getId()).get();
        assertThat(actualResult).isEqualTo(subscription);
    }

    @Test
    void expire() {
        Subscription subscription = getSubscription(13, "Roman");
        subscriptionDao.insert(subscription);

        subscription.setStatus(Status.EXPIRED);

        subscriptionDao.update(subscription);

        Subscription actualResult = subscriptionDao.findById(subscription.getId()).get();
        assertThat(actualResult).isEqualTo(subscription);
    }


    private Subscription getSubscription(Integer userId, String name) {
        return Subscription.builder()
                .userId(userId)
                .name(name)
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.now().truncatedTo(ChronoUnit.SECONDS).plus(30, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();

    }

    private CreateSubscriptionDto getCreateSubscriptionDto(Integer userId, String name) {
        return CreateSubscriptionDto.builder()
                .userId(userId)
                .name(name)
                .provider("GOOGLE")
                .expirationDate(Instant.now().truncatedTo(ChronoUnit.SECONDS).plus(30, ChronoUnit.DAYS))
                .build();
    }

}