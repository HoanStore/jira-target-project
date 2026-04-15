package io.spring.controller;

// @generated-by jira-dev-pipeline
// @generated-ticket SCRUM-13
import io.spring.service.GeneratedResourceService;
import io.spring.dto.GeneratedResourceRequest;
import io.spring.dto.GeneratedResourceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generated-resource")
public class GeneratedResourceController {

    private final GeneratedResourceService generatedResourceService;

    public GeneratedResourceController(GeneratedResourceService generatedResourceService) {
        this.generatedResourceService = generatedResourceService;
    }

@PostMapping("/ping")
    public ResponseEntity<GeneratedResourceResponse> ping(@RequestBody GeneratedResourceRequest request) {
        // Generated from Jira ticket SCRUM-13
        return ResponseEntity.ok(generatedResourceService.ping(request));
    }
}
