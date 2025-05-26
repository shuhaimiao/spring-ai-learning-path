# Spring AI Learning Path 🚀

Welcome to my personal learning journey into the world of **Spring AI**! This repository serves as both a documented roadmap and a collection of hands-on projects designed to master the Spring AI framework. It's built for Java Spring developers looking to dive into building AI-powered applications.

## About Spring AI

The [Spring AI](https://docs.spring.io/spring-ai/reference/index.html) project aims to streamline the development of applications that incorporate artificial intelligence functionality without unnecessary complexity, drawing inspiration from Python's LangChain and LlamaIndex.

## The Roadmap 🗺️

This roadmap is structured progressively, starting from the basics and moving towards more complex and production-ready concepts. Each stage includes hands-on projects (found in the `/projects` directory) to solidify understanding.

* **Stage 1: Introduction to Spring AI & Basic Chat Applications**
    * **Goal:** Understand Spring AI's core concepts, set up your environment, and build basic chat interactions.
    * **Key Features:** `ChatClient`, AI Model Integration (OpenAI, Ollama), Prompts, Spring Boot Starters.
    * **Projects:** Simple Q&A Bot, Joke Generator.

* **Stage 2: Working with Embeddings and Vector Stores (Basic RAG)**
    * **Goal:** Learn how to convert data into embeddings and use vector stores for basic Retrieval Augmented Generation (RAG).
    * **Key Features:** `EmbeddingClient`, `VectorStore` API (ChromaDB, PGVector), Document Readers & Loaders.
    * **Projects:** Q&A Over Custom Documents, Simple Product Recommender.

* **Stage 3: Advanced AI Interactions**
    * **Goal:** Explore function calling to connect LLMs to external tools and leverage structured outputs.
    * **Key Features:** Function Calling/Tools, Output Parsers, `BeanOutputConverter`, Multi-modality (Vision/Audio basics).
    * **Projects:** Weather Information Bot (using an API), Data Extractor (JSON/POJO output).

* **Stage 4: Building Complex AI Flows**
    * **Goal:** Implement more sophisticated AI workflows using agentic patterns and manage conversation history.
    * **Key Features:** `ChatMemory`, Advisors API, Agentic Patterns (Chain, Router, Orchestrator).
    * **Projects:** Customer Support Chatbot with History, Simple Research Agent.

* **Stage 5: Productionizing Spring AI Applications**
    * **Goal:** Focus on evaluating, monitoring, and refining AI applications for real-world use.
    * **Key Features:** `Evaluator` API, Observability (Micrometer), Advanced RAG Techniques, Prompt Engineering Best Practices.
    * **Projects:** RAG System with Evaluation, Chatbot with Monitoring & Feedback Loop.

## Projects 🛠️

All hands-on projects are located in the `/projects` directory. Each project is a standalone Maven application built with:

* **Java** (Latest LTS)
* **Spring Boot 3.x**
* **Spring Framework 6.x**
* **Spring AI 1.x**
* **Maven**

Each project directory contains its own `pom.xml` and source code. Refer to the individual project's `README.md` (if available) for specific setup and running instructions.

## Directory Structure 📂

```
spring-ai-learning-path/
├── README.md
├── LICENSE
├── roadmap/                # (Optional: Detailed .md files per stage)
│   ├── 01-foundations.md
│   └── ...
└── projects/
    ├── 01-basic-chat-app/
    │   ├── pom.xml
    │   └── src/
    ├── 02-joke-generator/
    │   ├── pom.xml
    │   └── src/
    ├── ... (more projects)
```

## How to Use

1.  **Clone the repo:** `git clone https://github.com/shuhaimiao/spring-ai-learning-path.git`
2.  **Follow the Roadmap:** Start with Stage 1 and progress through the stages.
3.  **Explore the Projects:** Navigate into each project directory, review the code, and run it locally.
4.  **Track Your Progress:** Feel free to fork this repo and adapt it to track your own learning!

## License 📄

This project is licensed under the MIT License.

---

**MIT License**

Copyright (c) 2025 [Your Name or GitHub Handle]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.