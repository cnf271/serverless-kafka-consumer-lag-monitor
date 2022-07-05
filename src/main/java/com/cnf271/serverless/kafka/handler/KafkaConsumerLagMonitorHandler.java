package com.cnf271.serverless.kafka.handler;

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
@Slf4j
public class KafkaConsumerLagMonitorHandler extends SpringBootRequestHandler<Void, Void> {}
