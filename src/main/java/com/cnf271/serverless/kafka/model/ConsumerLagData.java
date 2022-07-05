package com.cnf271.serverless.kafka.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerLagData {

  @JsonProperty("timestamp")
  private String timestamp;

  @JsonProperty("value")
  private Double value;

  @JsonProperty("metric.topic")
  private String topic;

  @JsonProperty("metric.consumer_group_id")
  private String consumerGroupId;
}
