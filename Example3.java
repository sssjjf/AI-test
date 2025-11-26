package com.example.codecheckdemo.example;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description of this file * * @author Lai Yufang * @version 1.0 * @since 2025/7/21
 */
@Service
public class Example3 {
    @Autowired
    private PrometheusMeterRegistry meterRegistry;
    public static final String TAG_URI = "uri";
    public static final String TAG_STREAM_REGION = "stream_region";
    public static final String SUMMARY_CIPC_GRPC_REQUEST_SECONDS = "cipc_grpc_request_seconds";

    public void countGrpcRequestStat(RequestStat requestStat) {
        DistributionSummary.builder(SUMMARY_CIPC_GRPC_REQUEST_SECONDS)
                .publishPercentileHistogram(true)
                .tags(TAG_URI, requestStat.getUri(), TAG_STREAM_REGION, requestStat.getRegionCode())
                .register(meterRegistry)
                .record(requestStat.getCostTime());
    }
}