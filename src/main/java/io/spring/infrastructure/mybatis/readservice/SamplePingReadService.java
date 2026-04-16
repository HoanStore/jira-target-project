package io.spring.infrastructure.mybatis.readservice;

// @generated-by jira-dev-pipeline
// @generated-ticket SCRUM-13
import io.spring.dto.SamplePingRequest;
import io.spring.dto.SamplePingResponse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SamplePingReadService {

SamplePingResponse getSamplePing(SamplePingRequest request);
}
