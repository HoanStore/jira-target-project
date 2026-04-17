package io.spring.infrastructure.mybatis.readservice;

// @generated-by jira-dev-pipeline
// @generated-ticket SCRUM-16
import io.spring.dto.ExportExcelRequest;
import io.spring.dto.ExportExcelResponse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExportExcelReadService {

ExportExcelResponse getExportExcel(ExportExcelRequest request);
}
