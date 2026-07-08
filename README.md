# llamagen-java

Java library for the LlamaGen API, including the latest Comic API and Animation API.

This SDK gives Java developers a typed client for LlamaGen comic generation, manga/webtoon workflows, panel regeneration, continuation, reference uploads, webhook verification, and video artwork generation.

Keywords for discovery: comic api, animation api, ai comic api, text-to-video api, image-to-video api, LlamaGen Java SDK.

## Installation

Gradle:

```groovy
implementation "ai.llamagen:llamagen-java:0.1.0"
```

Maven:

```xml
<dependency>
  <groupId>ai.llamagen</groupId>
  <artifactId>llamagen-java</artifactId>
  <version>0.1.0</version>
</dependency>
```

The library targets Java 8+ and uses Gson for JSON.

## Quick Start

```java
import com.llamagen.LlamaGenClient;
import com.llamagen.model.ComicGeneration;
import com.llamagen.param.CreateComicParams;

public class Example {
  public static void main(String[] args) throws Exception {
    LlamaGenClient client = new LlamaGenClient(System.getenv("LLAMAGEN_API_KEY"));

    ComicGeneration created = client.comic().create(
        CreateComicParams.builder()
            .setPrompt("A 4-panel comic about Leo finding a glowing key in a quiet library.")
            .setStyle("manga")
            .setFixPanelNum(4)
            .build());

    ComicGeneration done = client.comic().waitForCompletion(created.getId());
    System.out.println(done.getStatus());
  }
}
```

## Client Style

The SDK follows the same shape Java developers expect from production API clients:

- `LlamaGenClient.builder()` for global configuration.
- Resource namespaces such as `client.comic()` and `client.animation()`.
- Typed params with builders, for example `CreateComicParams.builder()`.
- Per-request `RequestOptions` for idempotency keys, headers, retry count, and API-key overrides.
- Automatic retries for `429` and `5xx` responses.
- `putExtraParam` on params builders for new API fields that ship before the SDK is regenerated.

```java
LlamaGenClient client = LlamaGenClient.builder()
    .setApiKey(System.getenv("LLAMAGEN_API_KEY"))
    .setMaxNetworkRetries(2)
    .build();
```

## Comic API

Use `client.comic()` for LlamaGen's latest comic api capabilities.

```java
ComicGeneration generation = client.comic().create(
    CreateComicParams.builder()
        .setPrompt("The Little Prince meets the Fox in a luminous desert.")
        .setStyle("storybook manga")
        .setPagination(2, 4)
        .addComicRole("The Little Prince", "https://example.com/prince.png")
        .addComicLocation("Dreamwood Forest", "https://example.com/forest.png")
        .addAttachment("image", "https://example.com/reference.png")
        .setLanguage("en")
        .setUpscale("2K")
        .build());
```

Comic API methods:

- `client.comic().create(params)` calls `POST /v1/comics/generations`.
- `client.comic().get(id)` calls `GET /v1/comics/generations/{generationId}`.
- `client.comic().get(id, options)` can fetch a specific zero-based `page` and `panel`.
- `client.comic().continueWrite(id, params)` extends an existing comic with `action=continueWrite`.
- `client.comic().updatePanel(id, params)` regenerates a single panel with `action=regeneratePanel`.
- `client.comic().usage()` calls `GET /v1/comics/usage`.
- `client.comic().upload(bytes, filename, contentType)` calls `POST /v1/comics/upload`.
- `client.comic().waitForCompletion(id)` polls until a terminal status.
- `client.comic().createAndWait(params)` creates and polls in one call.

Panel update example:

```java
client.comic().updatePanel(
    "gen_123",
    UpdateComicPanelParams.builder()
        .setPage(0)
        .setPanel(2)
        .setPanelPrompt("Make Leo look more hopeful and keep the same orange wizard robe.")
        .build());
```

Continue-write example:

```java
client.comic().continueWrite(
    "gen_123",
    ContinueComicParams.builder()
        .setPrompt("The two friends walk deeper into the neon city and find a hidden arcade.")
        .setPagination(1, 4)
        .addAttachment("image", "https://example.com/reference.png")
        .build());
```

## Animation API

Use `client.animation()` for LlamaGen's animation api, including text-to-video, image-to-video, storyboard-to-video, and reference-driven video generation. It targets `POST /v1/artworks/generations`.

```java
AnimationGeneration video = client.animation().create(
    CreateAnimationParams.builder()
        .setPrompt("A heroic fox detective walks through neon rain, cinematic camera move.")
        .setDuration(5)
        .setResolution("720p")
        .setAspectRatio("16:9")
        .setImage("https://example.com/first-frame.png")
        .setLastFrameImage("https://example.com/last-frame.png")
        .addReferenceImage("https://example.com/character.png")
        .build());

AnimationGeneration finished = client.animation().waitForCompletion(video.getId());
```

Animation API methods:

- `client.animation().create(params)` calls `POST /v1/artworks/generations`.
- `client.animation().get(id)` calls `GET /v1/artworks/generations/{id}`.
- `client.animation().waitForCompletion(id)` polls status.
- `client.animation().createAndWait(params)` creates and polls.

## Per-Request Options

```java
RequestOptions options = RequestOptions.builder()
    .setApiKey("request-scoped-key")
    .setIdempotencyKey("idem_123")
    .setMaxNetworkRetries(2)
    .putHeader("X-Trace-Id", "trace_123")
    .build();

client.comic().create(params, options);
```

## Webhooks

The SDK verifies LlamaGen webhook signatures from `X-Llama-Webhook-*` headers.

```java
WebhookEvent event = Webhook.constructEvent(
    rawBody,
    requestHeaders,
    System.getenv("LLAMAGEN_WEBHOOK_SECRET"));

if ("comic.generation.completed".equals(event.getType())) {
    System.out.println(event.getData());
}
```

Supported event names include:

- `comic.generation.created`
- `comic.generation.updated`
- `comic.generation.completed`
- `comic.generation.failed`

## Local Development

```bash
mvn test
./gradlew test
```

This repository keeps the code compatible with Java 8; a local `javac` compile can also be run with Gson on the classpath.

On macOS, if Gradle reports `Could not find tools.jar`, point `JAVA_HOME` to a JDK instead of the legacy browser plugin JRE.

## Related Packages

- JavaScript/TypeScript SDK: [llamagenai/comic](https://github.com/llamagenai/comic)
- Comic API docs: [https://llamagen.ai/comic-api/docs](https://llamagen.ai/comic-api/docs)
- Animation API: [https://llamagen.ai/animation-api](https://llamagen.ai/animation-api)
