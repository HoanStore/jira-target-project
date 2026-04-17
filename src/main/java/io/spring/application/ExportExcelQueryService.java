package io.spring.application;

// @generated-by jira-dev-pipeline
// @generated-ticket SCRUM-16
import io.spring.dto.ExportExcelRequest;
import io.spring.dto.ExportExcelResponse;
import io.spring.infrastructure.mybatis.readservice.ExportExcelReadService;
import org.springframework.stereotype.Service;

@Service
public class ExportExcelQueryService {

    private final ExportExcelReadService exportExcelReadService;

    public ExportExcelQueryService(ExportExcelReadService exportExcelReadService) {
        this.exportExcelReadService = exportExcelReadService;
    }

public ExportExcelResponse getExportExcel(ExportExcelRequest request) {
        return exportExcelReadService.getExportExcel(request);
    }
}
