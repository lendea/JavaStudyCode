package com.endea.demo.api.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 18:28
 **/
@Configuration
public class MetricConfig {

    @Value("${spring.application.name:}")
    private String name;

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", name).commonTags("module_type", "tsf-oss");
    }
}