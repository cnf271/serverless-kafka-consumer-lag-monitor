package com.cnf271.serverless.kafka.facade.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.cnf271.serverless.kafka.facade.KafkaConsumerLagMonitorFacade;
import com.cnf271.serverless.kafka.facade.SlackNotificationFacade;
import com.cnf271.serverless.kafka.model.Aggregations;
import com.cnf271.serverless.kafka.model.ConsumerLagData;
import com.cnf271.serverless.kafka.model.ConsumerLagRequest;
import com.cnf271.serverless.kafka.model.ConsumerLagResponse;
import com.cnf271.serverless.kafka.model.Filter;
import com.cnf271.serverless.kafka.model.Filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class KafkaConsumerLagMonitorFacadeImpl implements KafkaConsumerLagMonitorFacade {

  private static final String CONSUMER_LAG_API_ENDPOINT =
      "https://api.telemetry.confluent.cloud/v2/metrics/cloud/query";
  private final SlackNotificationFacade slackNotificationFacade;

  @Value("${LAG_THRESHOLD}")
  private double consumerLagThreshold;

  @Value("${KAFKA_CLUSTER_ID}")
  private String clusterId;

  @Value("${KAFKA_MONITOR_API_KEY}")
  private String kafkaApiKey;

  @Value("${KAFKA_MONITOR_SECRET_KEY}")
  private String kafkaApiSecretKey;

  @Override
  public void evaluateTopicConsumerLag() {
    final WebClient client = WebClient.create(CONSUMER_LAG_API_ENDPOINT);
    final String credentials = kafkaApiKey.concat(":").concat(kafkaApiSecretKey);
    final String encodedString = Base64.getEncoder().encodeToString(credentials.getBytes());

    final ConsumerLagResponse consumerLagResponse =
        client
            .post()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Basic ".concat(encodedString))
            .body(Mono.just(getConsumerLagRequest()), ConsumerLagRequest.class)
            .retrieve()
            .bodyToMono(ConsumerLagResponse.class)
            .block();

    List<ConsumerLagData> flaggedConsumers = new ArrayList<>();
    if (consumerLagResponse != null) {
      flaggedConsumers =
          consumerLagResponse.getData().stream()
              .filter(consumerLagData -> consumerLagData.getValue() > consumerLagThreshold)
              .collect(Collectors.toList());
    }

    flaggedConsumers.forEach(
        consumerData -> {
          log.info(
              "Topic : {}, Consumer group : {}, Lag : {} ",
              consumerData.getTopic(),
              consumerData.getConsumerGroupId(),
              consumerData.getValue());

          slackNotificationFacade.sendNotification(consumerData);
        });
  }

  private ConsumerLagRequest getConsumerLagRequest() {
    final String startTime =
        DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
            ZonedDateTime.ofInstant(
                Instant.now().minus(5, ChronoUnit.MINUTES), ZoneId.systemDefault()));

    final String endTime =
        DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
            ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

    final String timeInterval = startTime.concat("/").concat(endTime);

    return ConsumerLagRequest.builder()
        .aggregations(
            Collections.singletonList(
                Aggregations.builder()
                    .metric("io.confluent.kafka.server/consumer_lag_offsets")
                    .build()))
        .filter(
            Filter.builder()
                .op("OR")
                .filters(
                    Collections.singletonList(
                        Filters.builder()
                            .field("resource.kafka.id")
                            .op("EQ")
                            .value(clusterId)
                            .build()))
                .build())
        .group_by(Arrays.asList("metric.topic", "metric.consumer_group_id"))
        .granularity("PT5M")
        .intervals(Collections.singletonList(timeInterval))
        .limit(1000)
        .build();
  }
}
