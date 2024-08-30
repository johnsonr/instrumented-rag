# Spring AI Demo for Spring One 2024

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![ChatGPT](https://img.shields.io/badge/chatGPT-74aa9c?style=for-the-badge&logo=openai&logoColor=white)
![Neo4J](https://img.shields.io/badge/Neo4j-008CC1?style=for-the-badge&logo=neo4j&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

Kotlin project demonstrating Spring AI with Ollama, Open AI and Neo4j.
Shows:

- Mixing LLMs in a single application. **Use the right LLM for each call.**
- The power of Spring AI advisors to instrument chats in a reusable way
- The power of integration with the Spring context.

This project features the following custom advisors:

- `CaptureMemoryAdvisor`: Simple take on the ChatGPT concept of capturing memories. Uses a small model (`gemma2:2b` by
  default) to try to find useful memories in the latest user message. If it finds them it saves a Document to the
  `VectorStore` so it can be indexed in this or future chats.
- `NoteMentionsAdvisor`: Detects when a topic is mentioned in a chat and raises an application event

## Setup

This is a standard Spring Boot project, built with Maven
and written in Kotlin.

Set the `OPEN_AI_API_KEY` environment variable
to your Open AI token, or edit `ChatConfiguration.kt`
to switch to a different premium chat model.

Use the Docker Compose file in this project to run Neo,
or otherwise change the Neo credentials in `application.properties`
to use your own database.

Run [Ollama](https://ollama.com/) on your machine.
Make sure you've pulled the `gemma2:2b` model as follows:

```bash
docker pull ollama/gemma2:2b
```

## Running

- Start the server, either in your IDE or with `mvn spring-boot:run`,
- Go to `http://localhost:8080` to see the simple chat interface

## Limitations

This is meant to illustrate the power of Spring AI advisors,
so it's simplistic.

In particular:

- The `CaptureMemoryAdvisor` works of the latest user message only (athough this is extracted into a strategy function)
- The `NoteMentionsAdvisor` looks for a literal string. This could easily be improved to work with a local model and
  exhibit deeper understanding (e.g. "the user is talking about auto service")

Contributions welcome.