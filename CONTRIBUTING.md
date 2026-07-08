# Contributing

Thanks for helping improve `llamagen-java`.

## Development Setup

Requirements:

- Java 8 or newer
- Maven or Gradle

Run tests:

```bash
mvn test
```

or:

```bash
./gradlew test
```

## Pull Requests

- Keep SDK changes small and focused.
- Add or update tests for request paths, serialized JSON, response parsing, and error behavior.
- Update `README.md`, `CHANGELOG.md`, and `VERSION` when changing public API.
- Prefer backward-compatible additions. If a breaking change is required, call it out clearly in the PR.

## API Surface

The SDK intentionally follows Java API-client conventions:

- Builder-style params.
- Resource namespaces from `LlamaGenClient`.
- `RequestOptions` for per-request overrides.
- `putExtraParam` on params builders for newly documented API fields.

When adding a new LlamaGen endpoint, add:

- Params under `src/main/java/com/llamagen/param`.
- Models under `src/main/java/com/llamagen/model`.
- Service methods under `src/main/java/com/llamagen/service`.
- Unit tests under `src/test/java/com/llamagen`.
