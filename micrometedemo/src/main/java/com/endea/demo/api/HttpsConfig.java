package com.endea.demo.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * HTTPS配置
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 17:43
 **/

@Component
@ConfigurationProperties
public class HttpsConfig {
    // HTTP 连接池中的连接数
    private int maxPoolTotal = 100;

    // 每个链接的最大连接数
    private int defaultMaxPerRoute = 10;

    // 建立连接的超时时间
    private int connectTimeout = 3000;

    // 读取数据的超时时间
    private int socketTimeout = 15000;

    // 从连接池中获取连接的超时时间
    private int requestConnectTimeout = 1000;

    public int getMaxPoolTotal() {
        return maxPoolTotal;
    }

    public void setMaxPoolTotal(int maxPoolTotal) {
        this.maxPoolTotal = maxPoolTotal;
    }

    public int getDefaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getRequestConnectTimeout() {
        return requestConnectTimeout;
    }

    public void setRequestConnectTimeout(int requestConnectTimeout) {
        this.requestConnectTimeout = requestConnectTimeout;
    }
}
