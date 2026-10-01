package com.demon_invasion.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling   // active les méthodes annotées @Scheduled
public class SchedulingConfig {
}
