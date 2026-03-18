package com.endea.demo.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 17:47
 **/
public class HttpsHelper {
    private static final Logger log = LoggerFactory.getLogger(HttpsHelper.class);

    private static final String HTTP = "http";

    private static final String HTTPS = "https";

    /**
     * 发起调用
     *
     * @param request 封装好的HTTP请求
     * @param withCA true：使用CA证书；false：不使用CA证书（忽略所有安全校验）
     * @return 标准HTTP Response对象，请求失败时返回null
     */
    public static HttpResponse invoke(HttpRequestBase request, Boolean withCA) {
        // 设置头
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        // 设置超时
        setConfigForRequest(request);
        // 进行调用
        HttpResponse response = null;
        try {
            HttpClient httpClient = getHttpsClient(withCA);
            if (null != httpClient) {
                response = httpClient.execute(request);
            }
        } catch (IOException e) {
            log.error("[Goff Common] Error on execute https request.", e);
        }
        return response;
    }

    /**
     * 设置超时
     *
     * @param request 封装好的 HTTP 请求
     */
    public static void setConfigForRequest(HttpRequestBase request) {
        // 获取https配置
        HttpsConfig httpsConfig = null;
        if (null == ApplicationContextUtils.getApplicationContext()) {
            httpsConfig = new HttpsConfig();
        } else {
            httpsConfig = ApplicationContextUtils.getApplicationContext().getBean(HttpsConfig.class);
        }
        // 设置超时：
        RequestConfig config = RequestConfig
                .custom()
                .setConnectTimeout(httpsConfig.getConnectTimeout())
                .setConnectionRequestTimeout(httpsConfig.getRequestConnectTimeout())
                .setSocketTimeout(httpsConfig.getSocketTimeout())
                .build();
        request.setConfig(config);
    }

    // 使用CA的常量寄存器
    private static SSLConnectionSocketFactory SSLSF_CA = null;
    private static PoolingHttpClientConnectionManager CM_CA = null;
    private static SSLContextBuilder BUILDER_CA = null;
    private static Registry<ConnectionSocketFactory> REGISTRY_CA = null;
    // 不使用CA的常量寄存器
    private static SSLConnectionSocketFactory SSLSF = null;
    private static PoolingHttpClientConnectionManager CM = null;
    private static SSLContextBuilder BUILDER = null;
    private static Registry<ConnectionSocketFactory> REGISTRY = null;

    /**
     * 获取一个 HttpClient，可以选择是否信任全部 CA
     *
     * @param withCA 是否信任全部 CA
     * @return HttpClient
     */
    public static HttpClient getHttpsClient(Boolean withCA) {
        return getHttpsClientBuilder(withCA).build();
    }

    /**
     * 获取一个 HttpClientBuilder，可以选择是否信任全部 CA
     *
     * @param withCA 是否信任全部 CA
     * @return HttpClientBuilder
     */
    public static HttpClientBuilder getHttpsClientBuilder(Boolean withCA) {
        // 获取https配置
        HttpsConfig httpsConfig = null;
        if (null == ApplicationContextUtils.getApplicationContext()) {
            httpsConfig = new HttpsConfig();
        } else {
            httpsConfig = ApplicationContextUtils.getApplicationContext().getBean(HttpsConfig.class);
        }
        try {
            if (withCA) {
                // 使用CA
                if (null == BUILDER_CA) {
                    BUILDER_CA = new SSLContextBuilder();
                }
                if (null == SSLSF_CA) {
                    // change protocol from "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" to "TLSv1", "TLSv1.1", "TLSv1.2"
                    SSLSF_CA = new SSLConnectionSocketFactory(BUILDER_CA.build(), new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null, NoopHostnameVerifier.INSTANCE);
                }
                if (null == REGISTRY_CA) {
                    REGISTRY_CA = RegistryBuilder.<ConnectionSocketFactory>create().register(HTTP, new PlainConnectionSocketFactory())
                            .register(HTTPS, SSLSF_CA).build();
                }
                if (null == CM_CA) {
                    CM_CA = new PoolingHttpClientConnectionManager(REGISTRY_CA);
                    CM_CA.setMaxTotal(httpsConfig.getMaxPoolTotal());
                    CM_CA.setDefaultMaxPerRoute(httpsConfig.getDefaultMaxPerRoute());
                }
                return HttpClients.custom().setSSLSocketFactory(SSLSF_CA).setConnectionManager(CM_CA).setConnectionManagerShared(true);
            } else {
                // 不使用CA
                if (null == BUILDER) {
                    BUILDER = new SSLContextBuilder();
                    // 全部信任 不做身份鉴定
                    BUILDER.loadTrustMaterial(null, new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            return true;
                        }
                    });
                }
                if (null == SSLSF) {
                    // change protocol from "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" to "TLSv1", "TLSv1.1", "TLSv1.2"
                    SSLSF = new SSLConnectionSocketFactory(BUILDER.build(), new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null, NoopHostnameVerifier.INSTANCE);
                }
                if (null == REGISTRY) {
                    REGISTRY = RegistryBuilder.<ConnectionSocketFactory>create().register(HTTP, new PlainConnectionSocketFactory())
                            .register(HTTPS, SSLSF).build();
                }
                if (null == CM) {
                    CM = new PoolingHttpClientConnectionManager(REGISTRY);
                    CM.setMaxTotal(httpsConfig.getMaxPoolTotal());
                    CM.setDefaultMaxPerRoute(httpsConfig.getDefaultMaxPerRoute());
                }
                return HttpClients.custom().setSSLSocketFactory(SSLSF).setConnectionManager(CM).setConnectionManagerShared(true);
            }
        } catch (Exception e) {
            log.error("[Goff Common] Error on create HttpClient.", e);
        }
        return null;
    }
}
