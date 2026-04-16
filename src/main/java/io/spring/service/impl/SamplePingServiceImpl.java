package io.spring.service.impl;

// @generated-by jira-dev-pipeline
// @generated-ticket SCRUM-13
import io.spring.dto.SamplePingRequest;
import io.spring.dto.SamplePingResponse;
import io.spring.infrastructure.mybatis.readservice.SamplePingReadService;
import io.spring.application.SamplePingService;
import org.springframework.stereotype.Service;

@Service
public class SamplePingServiceImpl implements SamplePingService {

    private final SamplePingReadService samplePingReadService;

    public SamplePingServiceImpl(SamplePingReadService samplePingReadService) {
        this.samplePingReadService = samplePingReadService;
    }

@Override
    public SamplePingResponse getSamplePing(SamplePingRequest request) {
        return samplePingReadService.getSamplePing(request);
    }
}
