package com.endea.demo.api.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.httpcomponents.PoolingHttpClientConnectionManagerMetricsBinder;
import org.apache.http.HttpRequest;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 18:17
 **/
@Component
public class ConnectionPoolMetrics implements MeterBinder {

    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolMetrics.class);

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private Collection<DataSourcePoolMetadataProvider> metadataProviders;

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            // datasource metrics
            //判断数据源是否是TraceJdbcDataSource
            if (dataSource instanceof TraceJdbcDataSource) {
                DataSourcePoolMetadataProvider provider = new DataSourcePoolMetadataProviders(metadataProviders);
                Class<?> traceJdbcDataSourceClass = TraceJdbcDataSource.class;
                Field field = traceJdbcDataSourceClass.getDeclaredField("dataSource");
                field.setAccessible(true);
                DataSource originDataSource = (DataSource) field.get(dataSource);
                DataSourcePoolMetadata poolMetadata = provider.getDataSourcePoolMetadata(originDataSource);

                Gauge.builder("datasource.connections.active", this.dataSource, (dataSource) -> poolMetadata.getActive() != null ? (double) poolMetadata.getActive() : 0.0D)
                        .tags("client", "tsf-common")
                        .register(registry);
                Gauge.builder("datasource.connections.max", this.dataSource, (dataSource) -> (double) poolMetadata.getMax())
                        .tags("client", "tsf-common")
                        .register(registry);
                Gauge.builder("datasource.connections.min", this.dataSource, (dataSource) -> (double) poolMetadata.getMin())
                        .tags("client", "tsf-common")
                        .register(registry);
            } else {
                log.warn("datasource{} isn't TraceJdbcDataSource", dataSource);
            }

            // http client pool metrics
            Class<?> httpRequestClass = HttpRequest.class;
            Field field = httpRequestClass.getDeclaredField("CONN_MGR");
            field.setAccessible(true);
            PoolingHttpClientConnectionManager connMsr = (PoolingHttpClientConnectionManager) field.get(httpRequestClass);
            new PoolingHttpClientConnectionManagerMetricsBinder(connMsr, "tsf-common").bindTo(registry);

        } catch (Exception e) {
            log.warn("error in get ConnectionPoolMetrics :{}", e.getMessage());
        }
    }
}

