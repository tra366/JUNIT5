package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.dmdev.entity.Subscription;
import com.dmdev.entity.Status;
import com.dmdev.entity.Provider;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(13)
                .name("Roman")
                .provider("GOOGLE")
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        Subscription actualResult = mapper.map(dto);

        Subscription expectedResult = Subscription.builder()
                .userId(13)
                .name("Roman")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}