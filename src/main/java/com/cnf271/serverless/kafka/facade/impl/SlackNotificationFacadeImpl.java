package com.cnf271.serverless.kafka.facade.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cnf271.serverless.kafka.facade.SlackNotificationFacade;
import com.cnf271.serverless.kafka.model.ConsumerLagData;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class SlackNotificationFacadeImpl implements SlackNotificationFacade {

  @Value("${LAG_THRESHOLD}")
  private Double consumerLagThreshold;

  @Value("${SLACK_WEBHOOK_URL}")
  private String slackWebhookUrl;

  @Override
  public void sendNotification(final ConsumerLagData consumerLagData) {
    final Slack slack = Slack.getInstance();

    final StringBuilder payloadText = new StringBuilder();
    payloadText.append(":alert: *[");
    payloadText.append(consumerLagData.getConsumerGroupId());
    payloadText.append("] consumer group in Kafka topic [");
    payloadText.append(consumerLagData.getTopic());
    payloadText.append("] has exceeded consumer lag threshold of [");
    payloadText.append(consumerLagThreshold.intValue());
    payloadText.append("]. Current lag is [");
    payloadText.append(consumerLagData.getValue().intValue());
    payloadText.append("]* :alert:");

    final WebhookResponse response;
    try {
      final Payload payloadObj = Payload.builder().text(payloadText.toString()).build();
      response = slack.send(slackWebhookUrl, payloadObj);
      log.debug("Slack response : {}", response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
