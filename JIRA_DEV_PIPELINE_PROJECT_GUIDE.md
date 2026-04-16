# JIRA_DEV_PIPELINE_PROJECT_GUIDE.md

This guide helps automated agents follow the existing conventions in `feature_SCRUM-13`.
If the repository evolves, update this file so future automated changes stay aligned.

## Managed Conventions
<!-- jira-dev-pipeline:conventions:start -->
base_package: io.spring
api_package: io.spring.api
api_class_suffix: Api
service_package: io.spring.application
service_class_suffix: Service
service_interface_enabled: true
service_impl_package: io.spring.service.impl
mapper_package: io.spring.infrastructure.mybatis.readservice
mapper_class_suffix: ReadService
dto_package: io.spring.dto
mapper_xml_root: src/main/resources/mapper
test_package: io.spring.api
prefer_hash_map_response: true
<!-- jira-dev-pipeline:conventions:end -->

## Guidance For Agents
- Put REST entrypoints in `io.spring.api` and prefer the `*Api` suffix.
- Keep service interfaces in `io.spring.application` and implementations in `io.spring.service.impl`.
- Put MyBatis read interfaces in `io.spring.infrastructure.mybatis.readservice` and keep XML mappers under `src/main/resources/mapper`.
- Put request/response DTOs in `io.spring.dto` when DTOs are needed.
- Add or update tests under `io.spring.api`.
- Existing APIs already use `HashMap`/map-based response bodies in places, so match that style when the endpoint is simple.
- Reuse existing package structure and names before introducing generic names like `GeneratedResource`.
