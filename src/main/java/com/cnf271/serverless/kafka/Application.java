package com.cnf271.serverless.kafka;

import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.cnf271.serverless.kafka.facade.KafkaConsumerLagMonitorFacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
@Log4j2
@RequiredArgsConstructor
@SpringBootApplication
public class Application {
  private final KafkaConsumerLagMonitorFacade kafkaConsumerLagMonitorFacade;

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public Supplier<String> kafkaConsumerLagMonitor() {
    return () -> {
      kafkaConsumerLagMonitorFacade.evaluateTopicConsumerLag();

      return "Kafka Topic Consumer Lag Evaluation Success.";
    };
  }
}
