package com.cnf271.serverless.kafka.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Naween Fonseka
 * @since 6/19/2022
 */
@Data
@Builder
public class Filters {
  private final String field;
  private final String op;
  private final String value;
}
