# JIRA_DEV_PIPELINE_PROJECT_GUIDE.md

This guide helps automated agents follow the existing conventions in `feature_SCRUM-15`.
If the repository evolves, update this file so future automated changes stay aligned.

## Managed Conventions
<!-- jira-dev-pipeline:conventions:start -->
base_package: io.spring
api_package: io.spring.api
api_class_suffix: Api
service_package: io.spring.application
service_class_suffix: QueryService
service_interface_enabled: false
service_impl_package: 
mapper_package: io.spring.infrastructure.mybatis.readservice
mapper_class_suffix: ReadService
dto_package: io.spring.dto
mapper_xml_root: src/main/resources/mapper
test_package: io.spring.api
prefer_hash_map_response: true
<!-- jira-dev-pipeline:conventions:end -->

## Guidance For Agents
- Put REST entrypoints in `io.spring.api` and prefer the `*Api` suffix.
- Prefer concrete `*QueryService` classes in `io.spring.application` instead of interface/impl pairs unless the repo already needs them.
- Put MyBatis read interfaces in `io.spring.infrastructure.mybatis.readservice` and keep XML mappers under `src/main/resources/mapper`.
- Put request/response DTOs in `io.spring.dto` when DTOs are needed.
- Add or update tests under `io.spring.api`.
- Existing APIs already use `HashMap`/map-based response bodies in places, so match that style when the endpoint is simple.
- Reuse existing package structure and names before introducing generic names like `GeneratedResource`.

## Pipeline Safety Rules
- Never overwrite existing non-generated files. If a target file already exists and is not pipeline-managed, skip the change and require review.
- Treat `Target Files` as an exact allowlist when they are provided. Do not widen the write scope from partial hints.
- Separate `NEW_RESOURCE` mode from `EXISTING_CLASS_EXTENSION` mode. If `Target Files` point to existing classes, do not scaffold sibling files for a new resource.
- In `EXISTING_CLASS_EXTENSION` mode, modify only the exact existing target files and keep the generated change inside bounded marker blocks.
- If `Target Files` do not cover every scaffold file required for `NEW_RESOURCE` mode, skip Level1 generation instead of creating a partial scaffold.
