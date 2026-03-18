package com.endea.demo.api;

import com.endea.demo.api.metrics.HttpUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.httpcomponents.MicrometerHttpRequestExecutor;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 18:15
 **/
public class MetricUtil {
    public static MicrometerHttpRequestExecutor httpRequestExecutor(String clientType) {
        MeterRegistry meterRegistry = ApplicationContextUtils.getApplicationContext().getBean(MeterRegistry.class);
        return MicrometerHttpRequestExecutor.builder(meterRegistry)
                .uriMapper(req -> normalizePath(HttpUtils.getPath(req)))
                .tags(Tags.of("client.type", clientType))
                .build();
    }

    // for testing
    static String normalizePath(String path) {
        if (path == null) {
            return null;
        }
        return path.replaceFirst("/clusters/[^/]+", "/clusters/:cluster");
    }
}
