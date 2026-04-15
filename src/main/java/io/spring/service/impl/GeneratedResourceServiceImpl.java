package io.spring.service.impl;

import io.spring.dto.GeneratedResourceRequest;
import io.spring.dto.GeneratedResourceResponse;
import io.spring.mapper.GeneratedResourceMapper;
import io.spring.service.GeneratedResourceService;
import org.springframework.stereotype.Service;

@Service
public class GeneratedResourceServiceImpl implements GeneratedResourceService {

    private final GeneratedResourceMapper generatedResourceMapper;

    public GeneratedResourceServiceImpl(GeneratedResourceMapper generatedResourceMapper) {
        this.generatedResourceMapper = generatedResourceMapper;
    }

@Override
    public GeneratedResourceResponse ping(GeneratedResourceRequest request) {
        return generatedResourceMapper.ping(request);
    }
}
