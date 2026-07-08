# Agent Notes

This repository is the Java SDK for the LlamaGen API.

## Priorities

- Keep Java 8 compatibility unless the README and build matrix are intentionally changed.
- Preserve the public builder-style API.
- Add tests for every new request path, serialized payload, response model, and webhook behavior.
- Keep README examples aligned with the latest Comic API and Animation API docs.

## Verification

Preferred:

```bash
./gradlew test
```

Fallback:

```bash
mvn test
```

When local Maven/Gradle is unavailable, compile main sources with Java 8 and Gson on the classpath.

## Release Files

Update these together for a release:

- `VERSION`
- `gradle.properties`
- `build.gradle`
- `pom.xml`
- `README.md`
- `CHANGELOG.md`
