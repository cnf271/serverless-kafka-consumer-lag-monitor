package com.cnf271.serverless.kafka.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
@Data
@Builder
public class ConsumerLagRequest {
  private final List<Aggregations> aggregations;
  private final Filter filter;
  private final List<String> group_by;
  private final String granularity;
  private final List<String> intervals;
  private final int limit;
}
