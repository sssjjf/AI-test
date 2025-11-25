package com.example.codecheckdemo.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Description of this file * * @author Lai Yufang * @version 1.0 * @since 2025/8/25
 */
@Component
public class Example1 {
    @Autowired
    private PrometheusMeterRegistry registry;
    private Counter.Builder counterBuilder = Counter.builder("example");
    private Map<String, Counter> requestTypeCounterMap = new ConcurrentHashMap<>();

    public void increase(String requestType) {
        if (StringUtils.isEmpty(requestType)) {
            return;
        }
        Counter counter = requestTypeCounterMap.computeIfAbsent(requestType, key -> getCounter(requestType));
        counter.increment();
    }

    private Counter getCounter(String requestType) {
        return counterBuilder.tag("requestType", requestType).register(registry);
    }
}