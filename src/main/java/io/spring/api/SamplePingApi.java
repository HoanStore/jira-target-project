package io.spring.api;

// @generated-by jira-dev-pipeline
// @generated-ticket SCRUM-13
import io.spring.application.SamplePingService;
import io.spring.dto.SamplePingRequest;
import io.spring.dto.SamplePingResponse;
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
@RequestMapping("/sample")
public class SamplePingApi {

    private final SamplePingService samplePingService;

    public SamplePingApi(SamplePingService samplePingService) {
        this.samplePingService = samplePingService;
    }

@GetMapping("/ping")
    public ResponseEntity<SamplePingResponse> getSamplePing(@RequestBody SamplePingRequest request) {
        // Generated from Jira ticket SCRUM-13
        return ResponseEntity.ok(samplePingService.getSamplePing(request));
    }
}
