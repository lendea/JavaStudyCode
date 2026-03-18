package com.endea.demo.api.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.httpcomponents.DefaultUriMapper;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 18:29
 **/
public class MicrometerHttpClientInterceptor {
    public static final Logger logger = LoggerFactory.getLogger(MicrometerHttpClientInterceptor.class);

    private static final String METER_NAME = "http.client.async";

    private static final String CONTEXT_KEY = "MICROMETER_METRICS";

    private final HttpRequestInterceptor requestInterceptor;
    private final HttpResponseInterceptor responseInterceptor;

    /**
     * Create a {@code MicrometerHttpClientInterceptor} instance.
     *
     * @param meterRegistry      meter registry to bind
     * @param uriMapper          URI mapper to create {@code uri} tag
     * @param extraTags          extra tags
     * @param exportTagsForRoute whether to export tags for route
     */
    public MicrometerHttpClientInterceptor(MeterRegistry meterRegistry,
                                           Function<HttpRequest, String> uriMapper,
                                           Iterable<Tag> extraTags,
                                           boolean exportTagsForRoute) {
        this.requestInterceptor = (request, context) -> {
            try {
                context.setAttribute(CONTEXT_KEY, Timer.resource(meterRegistry, METER_NAME)
                        .tags("method", request.getRequestLine().getMethod(), "uri", uriMapper.apply(request)));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        };

        this.responseInterceptor = (response, context) -> {
            try {
                ((Timer.ResourceSample) context.getAttribute(CONTEXT_KEY))
                        .tag("status", Integer.toString(response.getStatusLine().getStatusCode()))
                        .tags(exportTagsForRoute ? HttpUtils.generateTagsForRoute(context) : Tags.empty())
                        .tags(extraTags)
                        .close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        };
    }

    /**
     * Create a {@code MicrometerHttpClientInterceptor} instance with {@link DefaultUriMapper}.
     *
     * @param meterRegistry      meter registry to bind
     * @param extraTags          extra tags
     * @param exportTagsForRoute whether to export tags for route
     */
    public MicrometerHttpClientInterceptor(MeterRegistry meterRegistry,
                                           Iterable<Tag> extraTags,
                                           boolean exportTagsForRoute) {
        this(meterRegistry, new DefaultUriMapper(), extraTags, exportTagsForRoute);
    }

    public HttpRequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    public HttpResponseInterceptor getResponseInterceptor() {
        return responseInterceptor;
    }
}
