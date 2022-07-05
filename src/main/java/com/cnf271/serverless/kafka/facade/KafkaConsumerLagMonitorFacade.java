package com.cnf271.serverless.kafka.facade;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
public interface KafkaConsumerLagMonitorFacade {

  void evaluateTopicConsumerLag();
}
