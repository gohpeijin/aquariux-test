package com.example.demo.scheduler;

import com.example.demo.service.AggregationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriceAggregatorScheduler {

  private final AggregationService aggregationService;

  @Autowired
  public PriceAggregatorScheduler(AggregationService aggregationService) {
    this.aggregationService = aggregationService;
    log.info("PriceAggregatorScheduler bean is being initialized...");
  }

  // TODO: create lock
  @Scheduled(cron = "${cron.price-aggregator-scheduler}")
  public void aggregatePrice() {
    log.info("Start price aggregation...");
    aggregationService.saveBestAggregatedPrice();
    log.info("End price aggregation");
  }

}
