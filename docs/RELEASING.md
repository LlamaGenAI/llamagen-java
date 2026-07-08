# Releasing

This repository is prepared for Maven Central style releases, but the first public artifact should only be published after group ownership and signing credentials are configured.

## Preflight

```bash
mvn test
./gradlew test
```

Check version files:

- `VERSION`
- `gradle.properties`
- `build.gradle`
- `pom.xml`
- `README.md`
- `CHANGELOG.md`

## Maven Coordinates

```text
ai.llamagen:llamagen-java:<version>
```

## Release Checklist

1. Update `VERSION`, `gradle.properties`, `build.gradle`, `pom.xml`, and README install snippets.
2. Update `CHANGELOG.md`.
3. Run tests locally and verify GitHub Actions.
4. Tag the release as `v<version>`.
5. Publish to Maven Central once credentials/signing are configured.
6. Create a GitHub release with changelog notes.

## Notes

`deploy.gradle` currently supports publishing to a local build repository and can be extended for Maven Central once Sonatype Central Portal credentials are ready.
