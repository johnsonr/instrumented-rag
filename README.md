# Spring AI Demo for Spring One 2024

Demonstrates Spring AI with Ollama, Open AI and Neo4j.
Specifically:

- Mixing LLMs in a single application. **Use the right LLM for each call.**
- The power of advisors to instrument chats in a reusable way
- The power of integration with the Spring context.

## Setup

This is a standard Spring Boot project, built with Maven
and written in Kotlin.

Set the `OPEN_AI_API_KEY` environment variable
to your Open AI token (or edit `ChatConfiguration.kt`
to switch to a different premium chat model.)

Use the Docker Compose file in this project to run Neo,
or otherwise change the Neo credentials in `application.properties`.

Run Ollama on your machine.
Make sure you've pulled `gemma2:2b` as follows:

```bash
docker pull ollama/gemma2:2b
```