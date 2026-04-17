package io.spring.api;

// @generated-by jira-dev-pipeline
// @generated-ticket SCRUM-16
import io.spring.application.ExportExcelQueryService;
import io.spring.dto.ExportExcelRequest;
import io.spring.dto.ExportExcelResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/export")
public class ExportExcelApi {

    private final ExportExcelQueryService exportExcelQueryService;

    public ExportExcelApi(ExportExcelQueryService exportExcelQueryService) {
        this.exportExcelQueryService = exportExcelQueryService;
    }

@GetMapping("/excel")
    public ResponseEntity<ExportExcelResponse> getExportExcel(@RequestBody ExportExcelRequest request) {
        // Generated from Jira ticket SCRUM-16
        return ResponseEntity.ok(exportExcelQueryService.getExportExcel(request));
    }
}
