package io.spring.mapper;

// @generated-by jira-dev-pipeline
// @generated-ticket SCRUM-13
import io.spring.dto.GeneratedResourceRequest;
import io.spring.dto.GeneratedResourceResponse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GeneratedResourceMapper {

GeneratedResourceResponse getSamplePing(GeneratedResourceRequest request);
}
