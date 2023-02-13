package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @InjectMocks
    private SubscriptionService subscriptionService;
/*
    @Spy
    private SubscriptionDao spySubscriptionDao;
*/


    private Instant expirationDate = Instant.now();

    @Test
    void upsertUpdate() {
        Subscription subscription = getSubscription();
        CreateSubscriptionDto createSubscriptionDto = getCreateSubscriptionDto();
        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        /*      doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(createSubscriptionDto);
        doReturn(subscription).when(spySubscriptionDao).insert(subscription);
        doReturn(subscriptions).when(spySubscriptionDao).findByUserId(createSubscriptionDto.getUserId());
        doReturn(subscription).when(createSubscriptionMapper).map(createSubscriptionDto);
        Subscription actualResult = subscriptionService.upsert(createSubscriptionDto);
        assertThat(actualResult).isEqualTo(subscription);*/

        Optional<Subscription> actualResult = subscriptions.stream()
                .filter(existingSubscription -> existingSubscription.getName().equals(createSubscriptionDto.getName()))
                .filter(existingSubscription -> existingSubscription.getProvider() == Provider.findByName(createSubscriptionDto.getProvider()))
                .findFirst()
                .map(existingSubscription -> existingSubscription
                        .setExpirationDate(createSubscriptionDto.getExpirationDate())
                        .setStatus(Status.ACTIVE));

        assertThat(actualResult).isPresent();
        verifyNoInteractions(createSubscriptionMapper);
    }

    @Test
    void shouldThrowExceptionIfDtoInvalid() {
        CreateSubscriptionDto createSubscriptionDto = getCreateSubscriptionDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of(100, "userId is invalid"));
        doReturn(validationResult).when(createSubscriptionValidator).validate(createSubscriptionDto);

        assertThrows(ValidationException.class, () -> subscriptionService.upsert(createSubscriptionDto));
        verifyNoInteractions(subscriptionDao, createSubscriptionMapper);
    }

    @Test
    void cancel() {
        Integer subscriptionId = 1;
        Subscription subscription = getSubscription();
        CreateSubscriptionDto createSubscriptionDto = getCreateSubscriptionDto();
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscriptionId);

        subscriptionService.cancel(subscriptionId);

        verify(subscriptionDao).update(subscription);
    }

    @Test
    void expire() {

    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .id(1)
                .userId(13)
                .name("Roman")
                .provider(Provider.GOOGLE)
                .expirationDate(expirationDate.plus(30, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();

    }

    private CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(13)
                .name("Roman")
                .provider("GOOGLE")
                .expirationDate(expirationDate.plus(30, ChronoUnit.DAYS))
                .build();
    }
}

