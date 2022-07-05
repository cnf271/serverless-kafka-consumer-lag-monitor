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
public class Filter {
  private final String op;
  private final List<Filters> filters;
}
