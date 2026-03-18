package com.endea.demo.api;

import java.util.Map;

/**
 * HTTP API请求服务配置
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 17:40
 **/
public class HttpApiServerConfig {

    /**
     * API Server 地址，如 "https://10.12.12.12:28000"
     */
    private String address;
    /**
     * API 路径中的前缀，一般用来放版本号，如 "/v1.0"。必须以 '/' 开头
     */
    private String pathPrefix;
    /**
     * API Server 所接受的鉴权串，如 "Basic dHNmOkFTREZhc2RmMTIzNA=="
     */
    private String authorization;
    /**
     * 额外的固定 HTTP 头
     */
    private Map<String, String> addtionalHeaders;
    /**
     * 额外的固定 query param
     */
    private Map<String, String> addtionalQueryParams;
    /**
     * 如果有，通过代理访问该 API，如 "http://127.0.0.1:8118"
     */
    private String proxy;
    /**
     * 是否检查证书
     */
    private Boolean ifCheckCertificate;

    public HttpApiServerConfig(String address, String pathPrefix, String authorization, Map<String, String> addtionalHeaders, Map<String, String> addtionalQueryParams, String proxy, Boolean ifCheckCertificate) {
        this.address = address;
        this.pathPrefix = pathPrefix;
        this.authorization = authorization;
        this.addtionalHeaders = addtionalHeaders;
        this.addtionalQueryParams = addtionalQueryParams;
        this.proxy = proxy;
        this.ifCheckCertificate = ifCheckCertificate;
    }

    public HttpApiServerConfig() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public Map<String, String> getAddtionalHeaders() {
        return addtionalHeaders;
    }

    public void setAddtionalHeaders(Map<String, String> addtionalHeaders) {
        this.addtionalHeaders = addtionalHeaders;
    }

    public Map<String, String> getAddtionalQueryParams() {
        return addtionalQueryParams;
    }

    public void setAddtionalQueryParams(Map<String, String> addtionalQueryParams) {
        this.addtionalQueryParams = addtionalQueryParams;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public Boolean getIfCheckCertificate() {
        return ifCheckCertificate;
    }

    public void setIfCheckCertificate(Boolean ifCheckCertificate) {
        this.ifCheckCertificate = ifCheckCertificate;
    }
}
