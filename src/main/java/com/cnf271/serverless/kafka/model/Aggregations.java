package com.cnf271.serverless.kafka.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
@Data
@Builder
public class Aggregations {
  private final String metric;
}
