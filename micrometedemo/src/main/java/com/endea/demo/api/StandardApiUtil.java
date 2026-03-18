package com.endea.demo.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

/**
 *  * Goff 标准容器 API 规范所使用的工具类
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/09 18:11
 **/
public class StandardApiUtil {
    public static final ObjectMapper OM = new ObjectMapper();

    static {
        // 反序列化时忽略不存在的属性
        OM.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 序列化 Date 对象时不序列化成 timestamp
        OM.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // ISO8601DateFormat 在后面被 deprecated 了，换成 StdDateFormat。但是这个版本中（2.8.11），
        //  StdDateFormat 只能处理 3 位小数的 microsecond 部分： "yyyy-MM-dd HH:mm:ss.SSSZ"（注意 SSS 的数量），如果使用更多位
        //  会有 bug，会使得 SSSSSSS 部分整体除以 1000 后被加进前面秒 / 分的时间中。这个 bug 在 2.9.1 版本中被修复：
        //  https://github.com/FasterXML/jackson-databind/issues/1745
        //
        //  ISO8601DateFormat 则不会有此问题，可以兼容任意位 microsecond。
        OM.setDateFormat(new ISO8601DateFormat());
    }

    public static final ObjectMapper OM_SNAKE_CASE = new ObjectMapper();

    static {
        // 反序列化时忽略不存在的属性
        OM_SNAKE_CASE.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 序列化 Date 对象时不序列化成 timestamp
        OM_SNAKE_CASE.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 反序列化时将 user_name 转成 userName 对应到 Entity 上
        OM_SNAKE_CASE.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }
}
