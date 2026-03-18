package com.endea.demo.api.metrics;

import io.micrometer.core.instrument.Tags;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 18:16
 **/
public class HttpUtils {
    static Tags generateTagsForRoute(HttpContext context) {
        String targetScheme = "UNKNOWN";
        String targetHost = "UNKNOWN";
        String targetPort = "UNKNOWN";
        Object routeAttribute = context.getAttribute("http.route");
        if (routeAttribute instanceof HttpRoute) {
            HttpHost host = ((HttpRoute) routeAttribute).getTargetHost();
            targetScheme = host.getSchemeName();
            targetHost = host.getHostName();
            targetPort = String.valueOf(host.getPort());
        }
        return Tags.of(
                "target.scheme", targetScheme,
                "target.host", targetHost,
                "target.port", targetPort
        );
    }

    public static String getPath(HttpRequest request) {
        try {
            return new URI(request.getRequestLine().getUri()).getPath();
        } catch (URISyntaxException e) {
            return request.getRequestLine().getUri();
        }
    }
}
