````
# TrustDesk — AI Support Operations Agent-Author Swetha

TrustDesk is an AI-powered support operations platform that helps support agents triage customer tickets, retrieve relevant policy knowledge, generate grounded response drafts, recommend sensitive actions, and evaluate AI performance.

The application is built using Spring Boot, Spring AI, OpenAI, MySQL, and an in-memory vector store for RAG-based knowledge retrieval.

---

# 1. Project Overview

TrustDesk demonstrates an AI-first customer support workflow:

1. Create or open a customer support ticket.
2. Retrieve relevant customer, order, and policy information.
3. Run AI-powered ticket triage.
4. Retrieve supporting knowledge-base documents using semantic search.
5. Generate a customer response draft with citations.
6. Recommend an operational action when required.
7. Require human approval for sensitive actions.
8. Execute approved actions with idempotency protection.
9. Record AI traces and important workflow events.
10. Run evaluation cases to measure AI quality and safety.

The application demonstrates:

- Retrieval-Augmented Generation (RAG)
- Agentic AI decision making
- Human-in-the-loop approval
- AI guardrails
- Citation-based responses
- Persistent business data
- Idempotent sensitive actions
- AI execution traces
- Automated evaluation

---

# 2. Architecture

```text
                         Browser
                            |
                            | HTTP
                            v
              +--------------------------+
              | TrustDesk Frontend       |
              | HTML / CSS / JavaScript  |
              +------------+-------------+
                           |
                           | REST APIs
                           v
              +--------------------------+
              | Spring Boot Application  |
              |                          |
              | Ticket APIs              |
              | Knowledge APIs           |
              | AI Services              |
              | Agentic Action Services  |
              | Evaluation APIs          |
              +------------+-------------+
                           |
              +------------+-------------+
              |                          |
              v                          v
      +---------------+          +----------------+
      | Spring AI     |          | MySQL          |
      |               |          |                |
      | OpenAI        |          | Customers      |
      | Chat Model    |          | Orders         |
      | Embeddings    |          | Tickets        |
      +-------+-------+          | Drafts         |
              |                  | Tool Actions   |
              v                  | AI Traces      |
      +---------------+          | Knowledge Base |
      | Simple        |          +----------------+
      | VectorStore   |
      |               |
      | RAG Retrieval |
      +---------------+
```

## Docker Architecture

```text
                         Browser
                            |
                            |
                    http://localhost:8081
                            |
                            v
              +--------------------------+
              | trustdesk-app             |
              | Spring Boot               |
              | Container Port: 8080      |
              +------------+-------------+
                           |
                           | Docker Network
                           |
                           v
              +--------------------------+
              | trustdesk-mysql           |
              | MySQL 8.0                 |
              | Container Port: 3306      |
              +--------------------------+

                           |
                           v
                      Spring AI
                           |
                           v
                      OpenAI API
                           |
                           v
                  SimpleVectorStore
```

---

# 3. Technology Stack

## Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Gradle

## AI

- Spring AI
- OpenAI
- OpenAI Chat Model
- OpenAI Embedding Model
- SimpleVectorStore
- Retrieval-Augmented Generation (RAG)

## Frontend

- HTML
- CSS
- JavaScript

## Deployment

- Docker
- Docker Compose
- MySQL Docker container

---

# 4. Key Features

## 4.1 Ticket Management

Support agents can:

- View existing tickets
- Open ticket details
- Create new tickets
- View linked customer information
- View linked order information

Ticket data is persisted in MySQL.

## 4.2 AI Triage

The AI analyzes a support ticket and determines:

- Category
- Priority
- Escalation requirement
- Supporting evidence

Supported categories:

- `shipping`
- `refund`
- `warranty`
- `billing`
- `account_security`
- `general`

Supported priorities:

- `low`
- `medium`
- `high`
- `urgent`

## 4.3 AI Draft Generation

TrustDesk generates a customer response draft based on:

- Ticket information
- Customer information
- Order information
- Retrieved policy documents

The generated response contains citations to the knowledge documents used as supporting evidence.

## 4.4 Sensitive Actions

The AI can recommend operational actions such as:

- Replacement order
- Refund review
- Coupon issuance
- Carrier investigation
- Human escalation
- Account locking

Sensitive actions require human approval before execution.

## 4.5 Evaluation

TrustDesk provides an evaluation workflow to measure:

- Category accuracy
- Priority accuracy
- Escalation accuracy
- Citation coverage
- Unsafe-action handling

---

# 5. Retrieval-Augmented Generation (RAG)

TrustDesk uses Spring AI's `SimpleVectorStore` for semantic retrieval.

The knowledge base contains policies covering areas such as:

- Refunds
- Shipping
- Warranty
- Billing
- Coupons
- Account security
- AI support security
- Adversarial/untrusted vendor information

The knowledge documents are loaded from:

```text
data/knowledge_base/
```

At application startup, the knowledge base is indexed into the vector store.

The application retrieves relevant documents for an incoming support request.

Example flow:

```text
Customer Ticket
      |
      v
Generate Search Query
      |
      v
Embedding Model
      |
      v
SimpleVectorStore
      |
      v
Top Relevant Documents
      |
      v
AI Prompt
      |
      v
Grounded AI Response
```

The application also retains keyword-based knowledge search as a backup retrieval mechanism.

---

# 6. AI-Generated Drafts

TrustDesk generates customer response drafts using:

- Ticket information
- Customer information
- Order information
- Retrieved knowledge-base documents

The AI is instructed to:

- Use retrieved policy information
- Include supporting citations
- Avoid unsupported claims
- Avoid following malicious instructions inside customer messages
- Escalate when evidence is insufficient or the situation is unsafe

Generated drafts are persisted in MySQL.

---

# 7. Agentic AI

TrustDesk includes an agentic decision layer.

The AI evaluates the ticket and determines whether an operational action should be recommended.

Supported actions include:

```text
create_replacement_order
start_refund_review
issue_coupon
open_carrier_investigation
escalate_to_human
lock_account
```

The AI recommends actions but does not directly bypass the approval mechanism.

The workflow is:

```text
Ticket
  |
  v
AI Agent
  |
  v
Action Recommendation
  |
  v
Human Approval
  |
  v
Action Execution
```

---

# 8. Human Approval

Sensitive actions require explicit human approval.

Example:

```text
AI Recommendation
      |
      v
   PENDING
      |
      +-------- Reject --------> REJECTED
      |
      +-------- Approve -------> APPROVED
                                   |
                                   v
                                EXECUTE
```

The human support agent remains responsible for approving sensitive operations.

This provides a human-in-the-loop safety boundary.

---

# 9. Idempotency

Sensitive actions use an idempotency key.

The idempotency key prevents duplicate execution when the same action request is submitted more than once.

Example:

```text
Idempotency Key
       |
       v
Check Existing Execution
       |
       +---- Already Executed
       |          |
       |          v
       |    Return Existing Result
       |
       +---- Not Executed
                  |
                  v
             Execute Action
```

This is particularly important for actions such as replacement orders and refund-related operations.

---

# 10. AI Guardrails

TrustDesk implements several AI safety controls.

## Prompt Injection Protection

Customer messages and retrieved documents are treated as untrusted input.

The AI is instructed not to follow instructions embedded inside:

- Customer messages
- Untrusted documents
- Adversarial vendor content

The adversarial vendor document in the knowledge base is intentionally treated as untrusted content.

## Unsupported Claims

The AI should rely on retrieved policy evidence.

If sufficient evidence is unavailable or conflicting, the system should avoid inventing a policy and escalate when appropriate.

## Sensitive Actions

The AI cannot independently execute human-approval-required actions.

Human approval is required before execution.

## Security-Sensitive Requests

Requests involving:

- Account security
- Identity verification bypass
- System prompts
- Secrets
- Prompt injection

are handled using the security policies and escalation rules.

---

# 11. AI Traceability

AI runs are recorded using `AiTrace`.

A trace records information such as:

- Ticket ID
- Run type
- Retrieved document IDs
- Tool actions
- Guardrail result
- Final status
- Timestamp

Example:

```text
Ticket
  |
  +--> Retrieved Documents
  |
  +--> AI Decision
  |
  +--> Guardrail Result
  |
  +--> Tool Action
  |
  +--> Final Status
```

This provides basic auditability and visibility into AI operations.

---

# 12. Evaluation

TrustDesk includes an evaluation workflow based on the supplied evaluation cases.

Evaluation cases cover:

- Triage accuracy
- Priority accuracy
- Escalation accuracy
- Citation coverage
- Unsafe-action handling

Evaluation cases are stored in:

```text
data/eval_cases.jsonl
```

The evaluation workflow runs asynchronously so that a long-running evaluation does not block unrelated application requests.

The current evaluation implementation reports category, priority, and escalation accuracy. Citation coverage and unsafe-action blocking are represented in the evaluation model/UI, but their detailed automated scoring is still limited and is documented as a future improvement.

The frontend provides:

- Run Evaluation
- Evaluation Status
- Summary Metrics
- Individual Results

---

# 13. Persistent Data

MySQL is used as the persistent business data store.

The application stores information such as:

- Customers
- Orders
- Order items
- Tickets
- Knowledge documents
- Draft responses
- Tool actions and approval state
- AI traces

The `ToolAction` record contains the action recommendation, approval status, execution status, idempotency key and related workflow information.
The vector store is intentionally separate from the persistent business database.

`SimpleVectorStore` is used for semantic retrieval during application runtime.

---

# 14. Project Structure

```text
TrustDeskAISupport/
│
├── data/
│   ├── customers.json
│   ├── orders.json
│   ├── tickets.json
│   ├── tool_actions.json
│   ├── eval_cases.jsonl
│   └── knowledge_base/
│       ├── account_security_policy.md
│       ├── adversarial_vendor_note.md
│       ├── billing_policy.md
│       ├── coupon_policy.md
│       ├── refund_policy.md
│       ├── shipping_policy.md
│       ├── support_security_playbook.md
│       └── warranty_policy.md
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/trustdesk/
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── style.css
│   │       │   └── app.js
│   │       └── application.properties
│   │
│   └── test/
│
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── .gitignore
├── .env                 # Local only; must not be committed
├── build.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

> `.env` should exist locally but must not be committed to GitHub.

---

# 15. Prerequisites

For local development:

- Java 17
- MySQL 8
- OpenAI API key
- Docker Desktop for Docker deployment

The project includes the Gradle wrapper, so Gradle does not need to be installed separately.

For Docker deployment, a locally installed MySQL server is not required for the application database.

---

# 16. Environment Configuration

Create a `.env` file in the project root.

Example:

```text
MYSQL_ROOT_PASSWORD=<your-mysql-password>
OPENAI_API_KEY=<your-openai-api-key>
```

---

# 17. Running with Docker

## Step 1 — Build the application image

From the project root:

```cmd
docker build -t trustdesk .
```

This creates the TrustDesk Docker image.

## Step 2 — Start the application

```cmd
docker compose up
```

Docker Compose starts:

```text
trustdesk-app
trustdesk-mysql
```

The MySQL container has a health check.

The TrustDesk application waits for MySQL to become healthy before starting.

For background mode:

```cmd
docker compose up -d
```

---

# 18. Docker Ports

The TrustDesk application is available at:

```text
http://localhost:8081
```

The Spring Boot application listens inside the container on:

```text
8080
```

Docker maps:

```text
8081 -> 8080
```

MySQL listens inside its container on:

```text
3306
```

MySQL listens inside its Docker container on:

```text
3306
```

The Spring Boot application connects to MySQL inside the Docker network using:

```text
mysql:3306
```

**Important:** the current `docker-compose.yml` does not require MySQL to be exposed on a host port for the application to work. If you have explicitly added a host mapping such as `3307:3306` for MySQL Workbench, then Workbench can use port `3307`. Otherwise, connect to the port you have configured in your current Compose file.

Inside the Docker network, the Spring Boot application connects to:

```text
mysql:3306
```

The host port `3307` is used because port `3306` may already be occupied by a locally installed MySQL instance.

---

# 19. Verify Docker Containers

Run:

```cmd
docker compose ps
```

Expected result:

```text
NAME              STATUS
trustdesk-app     Up
trustdesk-mysql   Up (healthy)
```

The application should show:

```text
0.0.0.0:8081->8080/tcp
```

If MySQL is explicitly exposed to the host in your Compose configuration, it may show a mapping such as:

```text
0.0.0.0:3307->3306/tcp
```

Otherwise, MySQL will show only its container ports, which is sufficient for the Spring Boot application.

To view application logs:

```cmd
docker compose logs app
```

To follow application logs:

```cmd
docker compose logs -f app
```

To view MySQL logs:

```cmd
docker compose logs mysql
```

---

# 20. Access the Application

Open:

```text
http://localhost:8081
```

The TrustDesk frontend should be displayed.

The main workflow can be tested from the UI:

```text
Tickets
   |
   +--> Create Ticket
   |
   +--> Open Ticket
   |
   +--> Triage
   |
   +--> Generate Draft
   |
   +--> View Citations
   |
   +--> Review Sensitive Action
   |
   +--> Approve
   |
   +--> Execute
```

---

# 21. Verify Database

MySQL Workbench can be used to inspect the Docker MySQL database.

Create a MySQL Workbench connection using the host port configured in your current `docker-compose.yml`.

If you have configured `3307:3306`, use:

```text
Hostname: 127.0.0.1
Port: 3307
Username: root
Password: <MYSQL_ROOT_PASSWORD>
Default Schema: trustdesk
```

If MySQL is not exposed to the host, Workbench cannot connect through `127.0.0.1:3307` until a host port mapping is added.

After connecting, expand:

```text
Schemas
  |
  +-- trustdesk
       |
       +-- customer
       +-- orders
       +-- order_item
       +-- ticket
       +-- knowledge_document
       +-- draft_reply
       +-- tool_action
       +-- ai_trace
```

After creating a ticket from the UI, the new ticket can be verified in the `ticket` table.

AI-generated drafts can be inspected in the `draft_reply` table.

Sensitive actions can be inspected in the `tool_action` table.

AI execution traces can be inspected in the `ai_trace` table.

Example queries:

```sql
USE trustdesk;

SELECT * FROM ticket;

SELECT * FROM orders;

SELECT * FROM draft_reply;

SELECT * FROM tool_action;

SELECT * FROM ai_trace;
```

---

# 22. REST API Overview

The application exposes REST APIs for the major workflows.

## Ticket APIs

```text
GET    /api/tickets
GET    /api/tickets/{ticketId}
POST   /api/tickets
```

## Knowledge APIs

```text
GET    /api/knowledge/search
GET    /api/knowledge/{docId}
GET    /api/knowledge/vector-search
GET    /api/knowledge/vector-search-debug
GET    /api/knowledge/vector-search-results
```

## Agentic Action APIs

```text
POST   /api/agent/action/{ticketId}
POST   /api/agent/execute/{idempotencyKey}
```

## Approval APIs

```text
POST   /api/actions/{idempotencyKey}/approve
POST   /api/actions/{idempotencyKey}/reject
```

## Evaluation APIs

```text
POST   /api/evaluation/run
GET    /api/evaluation/status
```

The APIs require the configured TrustDesk authentication token.

---

# 23. Authentication

The application uses a simple demo token for API authentication.

The frontend sends:

```text
X-TrustDesk-Token
```

The configured token is supplied through application configuration/environment.

This is intentionally a simple authentication mechanism suitable for the capstone demonstration rather than production-grade identity management.

---

# 24. Running Tests

Run the tests using the Gradle wrapper.

On Windows:

```cmd
gradlew.bat test
```

On Linux/macOS:

```bash
./gradlew test
```

The automated tests cover important workflow guardrails including:

- Approval requirements
- Prevention of execution before approval
- Idempotent action execution
- Unsupported action handling

---

# 25. Demo Workflow

The recommended demo workflow is:

## Step 1 — Create Ticket

Create a new customer support ticket.

Show:

- Customer
- Order
- Subject
- Customer message

## Step 2 — Open Ticket

Review:

- Ticket information
- Customer information
- Order information

## Step 3 — Run Triage

Trigger AI triage.

Show:

- Category
- Priority
- Escalation decision

## Step 4 — Generate Draft

Generate an AI response.

Show:

- Generated response
- Supporting citations
- Knowledge documents used

## Step 5 — Review Sensitive Action

Show the AI-recommended sensitive action.

The action starts in a pending state.

## Step 6 — Approve Action

The human support agent reviews and approves the action.

## Step 7 — Execute Action

Execute the approved action.

Show:

- Action status
- Idempotency key
- Execution result

## Step 8 — Verify Persistence

Open MySQL Workbench.

Show the corresponding:

- Ticket
- Order
- Tool action
- Draft
- AI trace

records.

## Step 9 — Run Evaluation

Open the Evaluations section.

Click:

```text
Run Evaluation
```

Show:

- Evaluation status
- Total cases
- Category accuracy
- Priority accuracy
- Escalation accuracy
- Citation metrics
- Safety metrics
- Individual evaluation results

---

# 26. Design Decisions

## Why Spring AI?

Spring AI provides integration with:

- Chat models
- Embedding models
- Vector stores

while fitting naturally into the Spring Boot architecture.

## Why SimpleVectorStore?

The project uses Spring AI `SimpleVectorStore` to provide semantic retrieval without introducing an external vector database dependency.

This keeps the capstone easy to run and Dockerize.

The persistent business data remains in MySQL.

## Why Human Approval?

AI-generated operational actions can have business impact.

Therefore the AI recommends the action while a human remains responsible for approving sensitive operations.

## Why Idempotency?

Operational requests can be retried.

Idempotency prevents duplicate execution of the same sensitive operation.

## Why AI Traces?

AI systems need observability.

The trace records:

- Retrieved documents
- AI run type
- Guardrail result
- Tool actions
- Final status

This provides basic auditability.

---

# 27. Security Considerations

The following security controls are part of the implementation:

- Customer messages are treated as untrusted.
- Retrieved knowledge is treated as evidence rather than executable instructions.
- Prompt injection attempts are guarded against.
- Unsupported policy claims should not be invented.
- Sensitive actions require human approval.
- Action execution uses idempotency.
- AI traces provide basic auditability.
- Secrets are supplied through environment variables.
- Secrets are not required inside the Docker image.

Never commit:

```text
.env
```

or any file containing an actual OpenAI API key.

Before GitHub submission:

1. Verify `.env` is listed in `.gitignore`.
2. Run `git status`.
3. Confirm `.env` is not staged.
4. Confirm no API key appears in source code.
5. Confirm no API key appears in README or Docker files.

If an API key has ever been exposed publicly, revoke/rotate it before submission.

---

# 28. Stopping the Application

To stop the Docker deployment:

```cmd
docker compose down
```

The MySQL data is stored in the Docker volume:

```text
trustdeskaisupport_trustdesk-mysql-data
```

Stopping the containers does not automatically delete the database volume.

Therefore database data remains available when the containers are started again.

To start the application again:

```cmd
docker compose up
```

To start in detached mode:

```cmd
docker compose up -d
```

Do not use `docker compose down -v` unless you intentionally want to delete the Docker database volume and recreate the database.

---

# 29. Rebuilding After Code Changes

After making application source-code changes, rebuild the Docker image:

```cmd
docker build -t trustdesk .
```

Then start Docker Compose:

```cmd
docker compose up
```

If the application image has changed, Docker Compose will use the newly built `trustdesk:latest` image.

If the source code has not changed and only `docker-compose.yml` or `.env` changed, rebuilding the application image is generally not required.

---

# 30. Technical Requirements & Assessment Coverage

## Technical Requirements

| Requirement | Implementation | Status |
|---|---|---|
| Expose core functionality as RESTful APIs | Spring Boot REST controllers for tickets, knowledge, agent actions, approvals, and evaluation | ✅ Implemented |
| Reliable persistent store for tickets | MySQL + JPA | ✅ Implemented |
| Reliable persistent store for customers | MySQL + JPA | ✅ Implemented |
| Reliable persistent store for orders | MySQL + JPA | ✅ Implemented |
| Reliable persistent store for documents | MySQL `KnowledgeDocument` + MySQL | ✅ Implemented |
| Reliable persistent store for drafts | MySQL `DraftReply` | ✅ Implemented |
| Reliable persistent store for tool calls/actions | MySQL `ToolAction` | ✅ Implemented |
| Persistent approval state | Approval status is persisted as part of `ToolAction` | ✅ Implemented |
| Persistent AI traces | MySQL `AiTrace` | ⚠️ Implemented for AI runs that currently create traces; tracing coverage can be expanded |
| Simple authentication | `X-TrustDesk-Token` demo-token interceptor | ✅ Implemented |
| Retrieval layer | Spring AI `SimpleVectorStore` with embeddings and keyword-search fallback | ✅ Implemented |
| AI provider behind an adapter | `AiProvider` interface + `SpringAiProvider` implementation | ✅ Implemented |
| Idempotency for approval-gated action | Persistent idempotency key checked before execution | ✅ Implemented |
| Automated tests for critical flows and guardrails | JUnit tests for approval boundary, pre-approval execution prevention, idempotency, and unsupported actions | ✅ Implemented |
| Avoid blocking unrelated requests during long AI/eval operations | Evaluation uses Spring `@Async` and frontend polling | ✅ Implemented |
| One command/endpoint to run eval cases and produce summary | `POST /api/evaluation/run` + `GET /api/evaluation/status` | ✅ Implemented |
| No dependency on a specific language/framework/hosted provider | Architecture keeps AI behind `AiProvider`; current implementation uses Java/Spring AI/OpenAI | ⚠️ Design supports substitution, but the submitted runtime stack uses these technologies |

## Implemented Must-Have Workflow 

- Data loading
- Required APIs
- Simple frontend
- AI triage
- Cited drafts
- One approval-gated action with idempotency
- Minimal traces
- Evals

TrustDesk implements these as follows:

| Must-Have | Implementation | Status |
|---|---|---|
| Data loading | Customers, orders, tickets, knowledge documents, tool/action data and evaluation cases are loaded from the supplied data package | ✅ |
| Required APIs | Ticket, knowledge, agent action, approval and evaluation REST APIs | ✅ |
| Simple frontend | HTML/CSS/JavaScript support-agent UI | ✅ |
| AI triage | Category, priority, escalation decision and supporting evidence | ✅ |
| Cited drafts | RAG-grounded draft generation with retrieved knowledge-document IDs | ✅ |
| Approval-gated action | AI recommends a sensitive action; human approval is required before execution | ✅ |
| Idempotency | Idempotency key prevents duplicate execution | ✅ |
| Minimal traces | `AiTrace` records ticket, run type, retrieved documents, tool actions, guardrail result and final status | ✅ |
| Evals | Evaluation endpoint, asynchronous execution, status polling and summary results | ✅ |

## AI Quality & Guardrails — 25%

Implemented controls include:

- Policy-grounded AI responses
- RAG-based evidence retrieval
- Citation generation and validation against retrieved document IDs
- Escalation when evidence is insufficient or the request is unsafe
- Customer messages treated as untrusted input
- Retrieved/adversarial documents treated as evidence, not executable instructions
- Prompt-injection protection
- Protection against system-prompt/secret disclosure requests
- Identity-verification bypass protection
- Human approval boundary for sensitive actions
- Unsupported-claim protection

The evaluation dataset includes adversarial/security cases covering account-security bypass, prompt injection, and system-prompt/secret requests.

**Current evaluation limitation:** category, priority, and escalation scoring are implemented. Citation-coverage and unsafe-action-blocking fields are present in the evaluation result model/UI but their detailed automated scoring is not yet fully implemented.

## Engineering Quality, Documentation & Demo — 15%

Implemented:

- Layered Spring Boot architecture
- Persistent MySQL storage
- REST APIs
- AI provider abstraction
- Automated unit tests
- Dockerfile
- Docker Compose
- Environment-based secret configuration
- Asynchronous evaluation
- README documentation
- Demonstrable end-to-end workflow

Good-to-have / production improvements remain listed in the Future Improvements section.

---

# 31. Future Improvements

Potential production-oriented improvements include:

- Persistent vector database instead of in-memory SimpleVectorStore
- Production authentication and RBAC
- External secret management
- More comprehensive AI evaluation metrics
- Distributed tracing
- Persistent tool execution results
- Production-grade asynchronous job processing
- Expanded integration tests
- Monitoring and alerting
- Centralized logging

These improvements are outside the minimum capstone implementation.

---

# 32. Author

## TrustDesk — AI Support Operations Agent

The project demonstrates how an AI support agent can combine:

- RAG
- LLM-based reasoning
- Agentic action recommendations
- Human-in-the-loop approval
- Idempotent execution
- AI guardrails
- Persistent business data
- AI traces
- Automated evaluation
- Dockerized deployment

---

# Quick Start

For a quick setup:

```cmd
REM 1. Create .env with your secrets

REM 2. Build Docker image
docker build -t trustdesk .

REM 3. Start application
docker compose up

REM 4. Open application
http://localhost:8081
```

MySQL Workbench:

Use the host port configured in `docker-compose.yml`.

For example, if the Compose file contains `3307:3306`:

```text
Host: 127.0.0.1
Port: 3307
User: root
Database: trustdesk
```

To stop:

```cmd
docker compose down
```

---

# Final Workflow

```text
                    TRUSTDESK
                        |
                        v
                 Create Ticket
                        |
                        v
                 Retrieve Context
                        |
             +----------+----------+
             |                     |
             v                     v
        Customer/Order          RAG Search
                                  |
                                  v
                         Knowledge Policies
                                  |
                                  v
                             AI Triage
                                  |
                                  v
                          Generate Draft
                                  |
                                  v
                              Citations
                                  |
                                  v
                         Agentic Decision
                                  |
                                  v
                        Sensitive Action?
                           /                                   No            Yes
                         |              |
                         v              v
                      Complete    Human Approval
                                         |
                                         v
                                   Idempotent
                                    Execution
                                         |
                                         v
                                   AI Trace
                                         |
                                         v
                                  MySQL Persistence
                                         |
                                         v
                                     Evaluation
```


````