package com.endea.demo.api;

import org.apache.commons.lang.BooleanUtils;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 18:05
 **/
public class ApiHelper {
    public static final String RequestIdHeader = "X-Request-ID";

    private static final Logger LOG = LoggerFactory.getLogger(ApiHelper.class);

    private HttpApiServerConfig apiConfig;

    public ApiHelper() {
    }

    public ApiHelper(HttpApiServerConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    public HttpApiServerConfig getApiConfig() {
        return apiConfig;
    }

    public void setApiConfig(HttpApiServerConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    /**
     * GET
     *
     * @param path       请求路径，请自行拼好路径参数
     * @param queryParam Query Params
     * @return 返回 HttpResponse
     */
    public CloseableHttpResponse get(String path, Map<String, Object> queryParam) {
        HttpGet httpGet = new HttpGet(joinPathAndBuildUri(path, queryParam));
        // 转换参数 queryParam
        return invoke(httpGet);
    }

    /**
     * POST
     *
     * @param path       请求路径，请自行拼好路径参数
     * @param queryParam Query Params
     * @param bodyParam  Body Params
     * @return 返回 HttpResponse
     */
    public CloseableHttpResponse post(String path, Map<String, Object> queryParam, Serializable bodyParam) {
        HttpPost httpPost = new HttpPost(joinPathAndBuildUri(path, queryParam));
        // 转换参数bodyParam
        if (null != bodyParam) {
            httpPost.setHeader("Content-type", "application/json");
            String jsonParam = null;
            // 如果传入string，则不进行序列化
            if (bodyParam instanceof String) {
                jsonParam = (String) bodyParam;
            } else {
                jsonParam = StandardApiUtil.OM.valueToTree(bodyParam).toString();
            }
            StringEntity entity = new StringEntity(jsonParam, "UTF-8");
            httpPost.setEntity(entity);
        }
        // 发起调用
        return invoke(httpPost);
    }

    /**
     * PUT
     *
     * @param path       请求路径，请自行拼好路径参数
     * @param queryParam Query Params
     * @param bodyParam  Body Params
     * @return 返回HttpResponse
     */
    public CloseableHttpResponse put(String path, Map<String, Object> queryParam, Serializable bodyParam) {
        HttpPut httpPut = new HttpPut(joinPathAndBuildUri(path, queryParam));
        // 转换参数bodyParam
        if (null != bodyParam) {
            httpPut.setHeader("Content-type", "application/json");
            String jsonParam = null;
            // 如果传入string，则不进行序列化
            if (bodyParam instanceof String) {
                jsonParam = (String) bodyParam;
            } else {
                jsonParam = StandardApiUtil.OM.valueToTree(bodyParam).toString();
            }
            StringEntity entity = new StringEntity(jsonParam, "UTF-8");
            httpPut.setEntity(entity);
        }
        // 发起调用
        return invoke(httpPut);
    }

    /**
     * PATCH
     *
     * @param path       请求路径，请自行拼好路径参数
     * @param queryParam Query Params
     * @param bodyParam  Body Params
     * @return 返回HttpResponse
     */
    public CloseableHttpResponse patch(String path, Map<String, Object> queryParam, Serializable bodyParam) {
        HttpPatch httpPatch = new HttpPatch(joinPathAndBuildUri(path, queryParam));
        // 转换参数bodyParam
        if (null != bodyParam) {
            httpPatch.setHeader("Content-type", "application/json");
            String jsonParam = null;
            // 如果传入string，则不进行序列化
            if (bodyParam instanceof String) {
                jsonParam = (String) bodyParam;
            } else {
                jsonParam = StandardApiUtil.OM.valueToTree(bodyParam).toString();
            }
            StringEntity entity = new StringEntity(jsonParam, "UTF-8");
            httpPatch.setEntity(entity);
        }
        // 发起调用
        return invoke(httpPatch);
    }

    /**
     * DELETE
     *
     * @param path       请求路径，请自行拼好路径参数
     * @param queryParam Query Params
     * @return 返回HttpResponse
     */
    public CloseableHttpResponse delete(String path, Map<String, Object> queryParam) {
        HttpDelete httpDelete = new HttpDelete(joinPathAndBuildUri(path, queryParam));
        // 发起调用
        return invoke(httpDelete);
    }

    /**
     * HEAD
     *
     * @param path       请求路径，请自行拼好路径参数
     * @param queryParam Query Params
     * @return 返回HttpResponse
     */
    public CloseableHttpResponse head(String path, Map<String, Object> queryParam) {
        HttpHead httpHead = new HttpHead(joinPathAndBuildUri(path, queryParam));
        // 发起调用
        return invoke(httpHead);
    }

    /**
     * 发起调用。会设置一些配置中的 header，但是如果 request 中已经有这些 header，它们不会被覆盖。
     *
     * @param request
     * @return
     */
    public CloseableHttpResponse invoke(HttpRequestBase request) {
        // 设置头部
        if (!StringUtils.isEmpty(apiConfig.getAuthorization())) {
            addHeaderIfNotExists(request, HttpHeaders.AUTHORIZATION, apiConfig.getAuthorization());
        }
        addHeaderIfNotExists(request, HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        addHeaderIfNotExists(request, RequestIdHeader, UUID.randomUUID().toString());
        if (!CollectionUtils.isEmpty(apiConfig.getAddtionalHeaders())) {
            for (Map.Entry<String, String> entry : apiConfig.getAddtionalHeaders().entrySet()) {
                addHeaderIfNotExists(request, entry.getKey(), entry.getValue());
            }
        }

        // 设置超时
        HttpsHelper.setConfigForRequest(request);

        // Logging
        LOG.info("Invoking API: {} {}", request.getMethod(), request.getURI());
        if (request instanceof HttpEntityEnclosingRequest) {
            // 有些 request 是不带 body 的，比如 HTTP GET 请求；
            // 如果 request 实现了 HttpEntityEnclosingRequest，说明它是 POST / PATCH / PUT / DELETE 请求，
            //  这类请求可以带请求体；当然也可以不带
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

            // HttpEntity 是一个 InputStream。如果它不是 repeatable 的，它无法被重复消费，那么大部分情况下不应该调用 EntityUtils.toString 去消费它
            if (entity != null && entity.isRepeatable()) {
                try {
                    LOG.info("Request body: {}", EntityUtils.toString(entity));
                } catch (IOException e) {
                    LOG.warn("Request entity can not be convert to string");
                }
            }
        }

        boolean withCA = BooleanUtils.isFalse(apiConfig.getIfCheckCertificate());
        HttpClientBuilder clientBuilder = HttpsHelper.getHttpsClientBuilder(withCA);
        if (!StringUtils.isEmpty(apiConfig.getProxy())) {
            HttpHost host = HttpHost.create(apiConfig.getProxy());
            clientBuilder.setProxy(host);
        }
        CloseableHttpClient client = clientBuilder
                .setRequestExecutor(MetricUtil.httpRequestExecutor(clientType()))
                .build();

        // 进行调用
        // 这里不打印回包的结构体，是因为 response.getEntity 并不是 repeatable 的，消费完一次就无法再读取
        try {
            return client.execute(request);
        } catch (IOException e) {
            LOG.error("Send request failed", e);
            throw new CommonException(CommonError.CONTAINER_API_REQUEST_FAILED, e.getMessage());
        }
    }

    protected String clientType() {
        return "api";
    }

    /**
     * 标准化后的的完整请求地址
     *
     * @param path       具体请求的URI
     * @param queryParam 请求参数
     * @return 完整的请求URL
     */
    public String joinPathAndBuildUri(String path, Map<String, Object> queryParam) {
        String address = StringUtils.trimTrailingCharacter(apiConfig.getAddress(), '/');

        String pathPrefix;
        if (StringUtils.isEmpty(apiConfig.getPathPrefix())) {
            pathPrefix = "";
        } else {
            pathPrefix = StringUtils.trimTrailingCharacter(apiConfig.getPathPrefix(), '/');
        }

        String uri = String.format("%s%s%s", address, pathPrefix, path);

        return buildUri(uri, queryParam);
    }

    public String buildUri(String uri, Map<String, Object> queryParam) {
        URIBuilder builder = null;
        try {
            builder = new URIBuilder(uri);
        } catch (URISyntaxException e) {
            LOG.error("Failed when building URL", e);
            throw new NamespaceException(NamespaceError.BUILD_HTTP_REQUEST_FAILED);
        }
        if (!CollectionUtils.isEmpty(queryParam)) {
            for (Map.Entry<String, Object> entry : queryParam.entrySet()) {
                builder.addParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        if (!CollectionUtils.isEmpty(apiConfig.getAddtionalQueryParams())) {
            for (Map.Entry<String, String> entry : apiConfig.getAddtionalQueryParams().entrySet()) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.toString();
    }

    private void addHeaderIfNotExists(HttpRequestBase request, String name, String value) {
        if (request.containsHeader(name)) {
            return;
        }
        request.addHeader(name, value);
    }

    public String getResponseString(HttpResponse response) {
        String responseString = null;
        try {
            responseString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            LOG.error("The response entity can not be read", e);
            String errorMessage = "回包无法被读取";
            throw new CommonException(CommonError.CONTAINER_API_RESPONSE_INVALID, errorMessage);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
        return responseString;
    }
}
