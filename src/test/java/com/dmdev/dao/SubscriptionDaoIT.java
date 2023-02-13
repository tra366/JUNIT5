package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription(13, "Roman"));
        Subscription subscription2 = subscriptionDao.insert(getSubscription(14, "Ivan"));
        Subscription subscription3 = subscriptionDao.insert(getSubscription(15, "Alex"));

        List<Subscription>  actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(3);
        List<Integer> userIds = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(userIds).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        Subscription subscription = subscriptionDao.insert(getSubscription(13, "Roman"));

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);

    }

    @Test
    void deleteExistingEntity() {
        Subscription subscription = subscriptionDao.insert(getSubscription(13, "Roman"));

        Boolean actualResult = subscriptionDao.delete(subscription.getId());

        assertThat(actualResult).isTrue();

    }

    @Test
    void deleteNotExistingEntity() {
        subscriptionDao.insert(getSubscription(13, "Roman"));

        Boolean actualResult = subscriptionDao.delete(-1);

        assertThat(actualResult).isFalse();

    }

    @Test
    void update() {
        Subscription subscription = getSubscription(13, "Roman");
        subscriptionDao.insert(subscription);

        subscription.setName("Update");
        subscriptionDao.update(subscription);

        Subscription actualResult = subscriptionDao.findById(subscription.getId()).get();
        assertThat(actualResult).isEqualTo(subscription);
    }

    @Test
    void insert() {
        Subscription subscription = getSubscription(13, "Roman");
        Subscription actualResult = subscriptionDao.insert(subscription);


        assertThat(actualResult.getId()).isNotNull();
    }

    @Test
    void findByUserId() {
        Subscription subscription = subscriptionDao.insert(getSubscription(13, "Roman"));

        Optional<Subscription>  actualResult = subscriptionDao.findByUserId(subscription.getUserId()).stream().findFirst();

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void shouldNotFindByUserIdIfUserDoesNotExist() {
        subscriptionDao.insert(getSubscription(13, "Roman"));

        Optional<Subscription>  actualResult = subscriptionDao.findByUserId(-1).stream().findFirst();

        assertThat(actualResult).isEmpty();
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
}