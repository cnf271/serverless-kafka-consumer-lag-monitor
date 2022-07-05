package com.cnf271.serverless.kafka.facade;

import com.cnf271.serverless.kafka.model.ConsumerLagData;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
public interface SlackNotificationFacade {

  void sendNotification(ConsumerLagData consumerLagData);
}
