# DSA and System Design Roadmap — SDET / Lead SDET

> **Repository:** [IntelliJJavaSQL](https://github.com/kkmahajan/IntelliJJavaSQL)
> **Learning branch:** feature/learning-dsa-system-design
> **Base branch:** feature/multi-module-test-framework
> **Java baseline:** Java 21
> **Target roles:** Senior SDET, Lead SDET, Lead Quality Engineer, Staff SDET / Quality Architect
> **Time budget:** 60 hours integrated into the existing 18-month career plan (not 60 additional hours)

This guide is a companion to [Java Coding Interview Preparation — 10 Core Topics](JAVA_CODING_INTERVIEW_PREPARATION.md). That guide contains the initial Java topic explanations and exercises. This document extends the roadmap with the broader DSA syllabus, system design expectations, interview practice, time allocations and completion evidence.

---

## 1. What depth is expected for your target roles?

You are preparing for Quality Engineering leadership and senior IC interviews, not competitive programming contests or a pure backend-development role.

| Capability | Target depth | Why it matters |
|---|---|---|
| Java coding and DSA | Strong fundamentals; comfortable with common Easy and Medium problems | Demonstrates programming ability, reasoning and interview readiness |
| Test Engineering System Design | Very strong | Shows that you can architect automation platforms, execution infrastructure, reporting and quality gates |
| General software architecture | Strong working knowledge | Helps you reason about APIs, services, data stores, queues, failures and scale |
| Distributed systems | Strong applied understanding | Directly relevant to APIs, Kafka, IoT, mobile/cloud workflows and eventual consistency |
| Competitive programming / very hard DSA | Not a priority unless a target employer explicitly requires it | Limited return for the available weekend study budget |

**Priority order**

1. Test platform and system design.
2. Java coding and DSA.
3. Distributed systems and reliability.
4. IoT and AI quality architecture.
5. Mock interviews, communication and trade-off explanations.

The goal is to become a Lead SDET who can code, architect systems and engineer quality at scale.

---

## 2. Time budget and allocation

The following 60 hours are reserved from existing technical and interview-preparation activities in the 18-month roadmap. Do not add them on top of the 270-hour technical budget.

| Track | Planned hours | Completion target |
|---|---:|---|
| DSA and Java coding | 30 | 30–35 well-understood Easy/Medium problems |
| System Design | 24 | 5–6 designs that can be explained and defended |
| Mock interviews and review | 6 | Three structured mock sessions |
| **Total** | **60** | Interview-ready foundations |

### Monthly allocation

| Career-plan months | DSA | System Design | Mocks | Total |
|---|---:|---:|---:|---:|
| 1–3 | 6 h | 2 h | 0 h | 8 h |
| 4–6 | 6 h | 3 h | 0 h | 9 h |
| 7–9 | 6 h | 5 h | 0 h | 11 h |
| 10–12 | 5 h | 6 h | 0 h | 11 h |
| 13–15 | 5 h | 5 h | 4 h | 14 h |
| 16–18 | 2 h | 3 h | 2 h | 7 h |
| **Total** | **30 h** | **24 h** | **6 h** | **60 h** |

This is a planning allocation, not a requirement to complete a topic in one sitting. Use the existing weekend schedule: focused Saturday build/practice, Sunday morning completion and notes, Sunday evening review. If a release weekend interrupts study, move the activity rather than doubling the next weekend.

---

# Part A — Data Structures and Algorithms

## 3. DSA syllabus and activity-level hour tracker

The existing ten-topic Java guide covers the initial foundations. Add the remaining topics below without duplicating basic explanations.

| # | Topic | Required depth | Planned hours | Evidence / completion condition |
|---:|---|---|---:|---|
| 1 | Time and space complexity | Big-O, best/average/worst case, auxiliary space | 2 h | Explain complexity for 5 solutions |
| 2 | Strings and Arrays | Traversal, mutation, searching, frequency, prefixes | 3 h | Solve 3 problems independently |
| 3 | HashMap and HashSet | Frequency maps, lookup, deduplication, equals/hashCode | 3 h | Solve 4 map/set problems |
| 4 | ArrayList and LinkedList | Operations, trade-offs, iteration, collection selection | 2 h | Explain operation costs and choose correctly |
| 5 | Sorting | Built-in sorting, Comparator, stable sort concept, merge sort / quicksort concepts | 2 h | Sort custom objects and explain O(n log n) |
| 6 | Two Pointers | Opposite ends, fast/slow pointers, sorted input | 2 h | Solve 2 problems |
| 7 | Sliding Window | Fixed and variable windows, frequency tracking | 2 h | Solve 2 problems |
| 8 | Stacks and Queues | LIFO/FIFO, Deque, monotonic stack concept | 2 h | Solve 2 problems |
| 9 | Binary Search | Search space, boundaries, lower/upper bound patterns | 2 h | Solve 2 problems and handle off-by-one cases |
| 10 | Linked Lists | Reversal, middle node, cycle detection, merge | 2 h | Solve 2 problems |
| 11 | Recursion and Backtracking | Base case, call stack, subsets/permutations concept | 2 h | Solve 1–2 introductory problems |
| 12 | Trees and BST | Traversals, height, search, level order | 2 h | Implement DFS and BFS traversals |
| 13 | Heap and PriorityQueue | Min/max heap, top-K, scheduling | 1.5 h | Solve top-K or Kth-largest problem |
| 14 | Graphs | Adjacency list, BFS, DFS, visited set | 1.5 h | Implement BFS and DFS on a small graph |
| 15 | Dynamic Programming | Memoization, tabulation, overlapping subproblems | 1 h | Explain and solve one basic DP problem |
| | **Total** | | **30 h** | |

### Java engineering concepts to study alongside DSA

These are important programming skills but should not be counted as separate DSA categories:

- String immutability; StringBuilder vs StringBuffer.
- == vs equals(); equals() and hashCode() contract.
- Generics, collections interfaces and iteration.
- Comparable vs Comparator.
- Streams, lambdas, Optional and collectors.
- Exceptions, defensive validation and null handling.
- Thread, Runnable, ExecutorService and CompletableFuture.
- Synchronization, race conditions, visibility and thread safety.

Concurrency and Java Streams remain part of the wider Java engineering roadmap. Do not remove them just because they are not algorithm topics.

## 4. Suggested coding problem bank

Target 30–35 problems. The list below is a bank, not a requirement to solve every item if the time budget is exceeded. Prioritize patterns and independent understanding over raw problem count.

| # | Problem / pattern | Level | Main concept |
|---:|---|---|---|
| 1 | Reverse a String | Easy | String traversal |
| 2 | Valid Palindrome | Easy | Two pointers |
| 3 | Second-largest distinct array value | Easy | Array traversal |
| 4 | Missing Number | Easy | Array / arithmetic |
| 5 | Contains Duplicate | Easy | HashSet |
| 6 | Two Sum | Easy | HashMap |
| 7 | Valid Anagram | Easy | Frequency map / array |
| 8 | First Non-Repeating Character | Easy | Frequency map |
| 9 | Group Anagrams | Medium | Hashing / grouping |
| 10 | Merge Two Sorted Arrays | Easy | Two pointers |
| 11 | Remove Duplicates from Sorted Array | Easy | Two pointers |
| 12 | Move Zeroes | Easy | Two pointers |
| 13 | Best Time to Buy and Sell Stock | Easy | Single-pass tracking |
| 14 | Longest Substring Without Repeating Characters | Medium | Sliding window |
| 15 | Minimum Size Subarray Sum | Medium | Sliding window |
| 16 | Valid Parentheses | Easy | Stack |
| 17 | Min Stack | Medium | Stack design |
| 18 | Binary Search | Easy | Search |
| 19 | Search Insert Position | Easy | Binary search boundaries |
| 20 | Merge Intervals | Medium | Sorting |
| 21 | Linked List Cycle | Easy | Fast/slow pointers |
| 22 | Reverse Linked List | Easy | Pointer manipulation |
| 23 | Merge Two Sorted Lists | Easy | Linked list |
| 24 | Binary Tree Level Order Traversal | Medium | BFS / queue |
| 25 | Maximum Depth of Binary Tree | Easy | DFS / recursion |
| 26 | Validate Binary Search Tree | Medium | Tree bounds |
| 27 | Kth Largest Element | Medium | Heap / PriorityQueue |
| 28 | Top K Frequent Elements | Medium | HashMap + heap |
| 29 | Number of Islands | Medium | Graph / grid BFS or DFS |
| 30 | Flood Fill | Easy | Graph traversal |
| 31 | Climbing Stairs | Easy | Introductory DP |
| 32 | House Robber | Medium | Introductory DP |

**Expected completion:** 30–35 problems, with the first 25 patterns prioritized. The final few problems are stretch coverage if the interview profile calls for them.

### Problem-solving routine

For each problem, follow the same sequence:

1. Restate the problem and clarify assumptions.
2. Work through a small example by hand.
3. Identify the simplest correct approach.
4. Improve the approach using the appropriate data structure or pattern.
5. Explain time and space complexity.
6. Code in Java without copying a solution.
7. Test normal, boundary and invalid inputs.
8. Record one lesson and one alternative approach.

A solution is not complete merely because it passes the sample input.

### DSA acceptance criteria

- [ ] Solve 30–35 selected Easy/Medium problems independently.
- [ ] Explain time and space complexity without guessing.
- [ ] Select suitable Java collections and justify the choice.
- [ ] Handle empty input, null where applicable, duplicates, boundaries and integer edge cases.
- [ ] Write readable Java methods with meaningful names and appropriate validation.
- [ ] Solve an Easy problem in approximately 20–30 minutes.
- [ ] Solve a familiar Medium problem in approximately 30–40 minutes.
- [ ] Explain the solution aloud while coding.

---

# Part B — System Design

## 5. System Design scope

System Design is a higher-priority differentiator for Lead SDET / Staff SDET than advanced competitive-programming topics.

Prepare three connected areas:

1. Test Engineering System Design — the primary specialization.
2. General Software System Design — the architecture building blocks.
3. IoT and AI Quality System Design — domain and specialization differentiators.

## 6. Test Engineering System Design — core designs

### Design 1: Distributed Test Automation Platform

**Problem statement:** Design a platform that accepts test suites from multiple repositories, distributes tests across workers, executes them in parallel, aggregates results and exposes searchable reports.

Required components:

- API or CI trigger.
- Test orchestration service.
- Test-suite metadata and selection.
- Queue / work distribution.
- Worker pool and execution agents.
- Environment and browser/device provisioning.
- Test data and secrets management.
- Result ingestion and normalization.
- Report storage and dashboard.
- Retry and quarantine policy.
- Flaky-test detection.
- Audit logs, access control and observability.

Design concerns to explain:

- How are tests sharded?
- How are worker failures recovered?
- How do you prevent duplicate test execution?
- How do you isolate test data?
- How do you handle long-running suites?
- How do you aggregate partial results?
- How do you keep a single failing test from hiding other results?
- How do you scale workers without overspending?

**Deliverable:** One architecture diagram, component responsibilities, execution sequence, failure handling and trade-off notes.

### Design 2: Scalable API Automation Framework

**Problem statement:** Design a maintainable API automation framework used by multiple teams and environments.

Cover:

- Test / scenario layer.
- Domain or service-action layer.
- API client layer.
- Authentication and token lifecycle.
- Request/response specifications.
- Environment configuration.
- Test data factories and cleanup.
- Schema and contract assertions.
- Retry policy (distinguish transient transport failures from assertion failures).
- Parallel execution and isolation.
- Reporting, logging and diagnostics.

**Deliverable:** Layered architecture diagram, sample package structure and explanation of at least three design decisions.

### Design 3: Centralized Test Reporting Platform

**Problem statement:** Design a reporting platform that ingests execution results from JUnit, Karate, Playwright, WDIO and other test systems, then exposes consistent analytics.

Cover:

- Result ingestion API or artifact collector.
- Common result schema.
- Test identity and history.
- Test-type and environment classification.
- Storage and retention.
- Search and filtering.
- Dashboard and API access.
- Trend analysis and failure clustering.
- Flaky-test metrics.
- Access control and data masking.
- Handling schema changes and duplicate uploads.

**Deliverable:** Data-flow diagram, normalized result schema, reporting architecture and retention strategy.

## 7. General Software System Design building blocks

| # | Subject | What to understand | Applied example |
|---:|---|---|---|
| 1 | Client-server architecture | Request/response, protocols, boundaries | Mobile application to cloud API |
| 2 | REST API design | Methods, status codes, pagination, idempotency | Device-control API |
| 3 | Load balancing | Traffic distribution, health checks | Multiple API instances |
| 4 | Caching | Cache-aside, TTL, invalidation, stale data | Device status cache |
| 5 | Databases | SQL vs NoSQL, indexing, transactions | Test result and device-state storage |
| 6 | Message queues | Producers, consumers, partitions, ordering | Kafka device events |
| 7 | Distributed systems | Eventual consistency, retries, delivery semantics | Cloud/device synchronization |
| 8 | Microservices | Service boundaries and failure isolation | Command, notification and user services |
| 9 | Authentication and authorization | OAuth2, JWT, RBAC, token validation | Device ownership and access control |
| 10 | Scalability | Horizontal vs vertical scaling, bottlenecks | Parallel test workers |
| 11 | Concurrency | Threads, locks, races, thread pools | Parallel test execution |
| 12 | Observability | Logs, metrics, traces, correlation IDs | Debug a failed release validation |

You should be able to explain what each component does, why it exists, how it can fail and how you would test it.

## 8. IoT System Design — domain differentiator

### Design 4: End-to-End Connected Garage Door System

**Problem statement:** Design the architecture and quality validation approach for a mobile application that controls a connected garage door and displays device state.

Illustrative architecture:

~~~text
Mobile App
   |
   v
API Gateway / Identity
   |
   v
Authorization + Device Command Service
   |
   v
Message Broker / Event Stream
   |
   v
Device Gateway / Connectivity Layer
   |
   v
Connected Device
   |
   v
Device State / Event Processing
   |
   +------> State Store / Cache
   |
   +------> Notification Service
                 |
                 v
             Mobile App
~~~

This is a generic reference architecture, not a claim about any employer's internal implementation.

Explain:

1. User authentication and device ownership.
2. Authorization before command acceptance.
3. Command identity and idempotency.
4. Command publishing and delivery.
5. Device acknowledgement and state transitions.
6. Event ordering and duplicate delivery.
7. State synchronization and eventual consistency.
8. Mobile push or real-time updates.
9. Observability and incident diagnosis.
10. Safe failure handling and recovery.

### Failure scenarios to design and test

| Scenario | Expected engineering / testing concern |
|---|---|
| Device offline | Clear pending/failed state; bounded retry |
| Network interruption | Recovery without duplicate physical action |
| Duplicate command | Idempotency and command identity |
| Delayed event | Eventual consistency and timeouts |
| Out-of-order Kafka events | Versioning, timestamps or sequence checks |
| Unauthorized account | Deny access; audit the attempt |
| Incorrect device status | Reconciliation and source-of-truth rules |
| Duplicate notification | Deduplication and notification identity |
| Two users operate the same device | Concurrency and conflict policy |

Do not assume that a message broker guarantees end-to-end exactly-once physical actions. Explain the actual delivery semantics, deduplication and idempotency boundaries.

**Deliverable:** System diagram, command/event sequence, API examples, failure matrix and a test strategy covering mobile, API, backend, event and device layers.

## 9. AI Quality System Design

### Design 5: AI Evaluation Platform

**Problem statement:** Design a platform that evaluates AI features across releases and detects quality regressions.

Cover:

- Prompt and model version registry.
- Golden dataset management.
- Human-labelled expected outputs.
- Evaluation orchestration.
- Deterministic validators.
- LLM-as-judge integration.
- Judge calibration and agreement.
- RAG quality metrics.
- Security and prompt-injection tests.
- Latency and cost measurement.
- CI quality gates and reports.
- Auditability and dataset privacy.

**Deliverable:** Evaluation architecture, dataset format, evaluation pipeline, metrics and model/prompt regression report.

This design should connect to your llm-eval-harness roadmap objective.

## 10. System design method: the interview framework

Use this eight-step framework for every design question.

| Step | What to do |
|---:|---|
| 1. Requirements | Clarify users, use cases and functional requirements |
| 2. Non-functional requirements | Availability, latency, scale, security and reliability |
| 3. Scale | Estimate users, tests, devices, throughput and storage where relevant |
| 4. Architecture | Draw components and define responsibilities |
| 5. Data flow | Explain synchronous and asynchronous interactions |
| 6. Failure handling | Timeouts, retries, idempotency, recovery and degradation |
| 7. Observability and security | Logs, metrics, traces, access, privacy and audit |
| 8. Trade-offs | Explain alternatives and why the chosen design is appropriate |

For each design, prepare:

- One architecture diagram.
- One sequence or data-flow diagram.
- Main APIs/events and example payloads.
- Data model or storage choice.
- Capacity assumptions (clearly labelled as assumptions).
- Failure scenarios and recovery.
- Security and privacy concerns.
- Scalability bottlenecks.
- Three trade-offs and alternatives.
- How you would test the design.

Avoid jumping into technology names before clarifying requirements. A good design is justified by constraints, not by listing products.

---

# Part C — Execution Schedule and Tracker

## 11. Eighteen-month learning sequence

| Period | DSA focus | System Design focus | Planned hours |
|---|---|---|---:|
| Months 1–3 | Complexity, Strings/Arrays, HashMaps, Collections | Architecture fundamentals and current framework design | 8 |
| Months 4–6 | Sorting, Two Pointers, Sliding Window, Stacks/Queues | Distributed systems, APIs, queues, databases and reliability | 9 |
| Months 7–9 | Binary Search, Linked Lists, Recursion | Distributed test platform and API automation framework | 11 |
| Months 10–12 | Trees, Heaps, Graphs, introductory DP | Reporting platform, IoT architecture and observability | 11 |
| Months 13–15 | Timed coding practice and problem revision | Complete design interviews; AI evaluation platform | 14 |
| Months 16–18 | Weak-area revision and coding mocks | Design mocks, trade-off defence and final interview practice | 7 |
| **Total** | **30 h** | **24 h** | **60 h** |

### Activity tracker

Update planned hours, actual hours, status and evidence after each session. Keep actual hours honest; do not mark an activity complete because a video or article was opened.

| ID | Activity | Planned hours | Actual hours | Status | Evidence / notes |
|---|---|---:|---:|---|---|
| DSA-01 | Complexity and problem-solving method | 2 |  | Not started | |
| DSA-02 | Strings and Arrays | 3 |  | Not started | |
| DSA-03 | HashMap and HashSet | 3 |  | Not started | |
| DSA-04 | ArrayList and LinkedList comparison | 2 |  | Not started | |
| DSA-05 | Sorting and Comparator | 2 |  | Not started | |
| DSA-06 | Two Pointers | 2 |  | Not started | |
| DSA-07 | Sliding Window | 2 |  | Not started | |
| DSA-08 | Stacks and Queues | 2 |  | Not started | |
| DSA-09 | Binary Search | 2 |  | Not started | |
| DSA-10 | Linked Lists | 2 |  | Not started | |
| DSA-11 | Recursion and Backtracking | 2 |  | Not started | |
| DSA-12 | Trees and BST | 2 |  | Not started | |
| DSA-13 | Heap and PriorityQueue | 1.5 |  | Not started | |
| DSA-14 | Graphs: BFS and DFS | 1.5 |  | Not started | |
| DSA-15 | Dynamic Programming foundations | 1 |  | Not started | |
| SD-01 | Requirements, NFRs and design method | 2 |  | Not started | |
| SD-02 | General architecture building blocks | 3 |  | Not started | |
| SD-03 | Distributed systems and messaging | 3 |  | Not started | |
| SD-04 | Distributed Test Automation Platform | 5 |  | Not started | |
| SD-05 | API Automation Framework | 3 |  | Not started | |
| SD-06 | Centralized Reporting Platform | 3 |  | Not started | |
| SD-07 | IoT Connected Device Architecture | 3 |  | Not started | |
| SD-08 | AI Evaluation Platform | 2 |  | Not started | |
| MOCK-01 | DSA coding mock and review | 2 |  | Not started | |
| MOCK-02 | Test platform design mock and review | 2 |  | Not started | |
| MOCK-03 | IoT/AI design mock and review | 2 |  | Not started | |
| | **Total** | **60** | | | |

**Status values:** Not started, In progress, Blocked, Complete. Record a blocker and next action when an activity cannot proceed.

## 12. Practical integration with your GitHub project

Use the existing Java learning guide for code exercises and this document for the extended DSA and architecture roadmap.

Recommended repository structure:

~~~text
automation-parent/
└── docs/
    ├── JAVA_CODING_INTERVIEW_PREPARATION.md
    ├── DSA_AND_SYSTEM_DESIGN_ROADMAP.md
    └── INTERVIEW_PREPARATION.md
~~~

Recommended practice location for future exercises (create only when implementation begins):

~~~text
automation-parent/
└── interview-practice/
    └── src/
        ├── main/java/
        │   └── ...
        └── test/java/
            └── ...
~~~

Keep learning exercises separate from the production-like automation modules. Use JUnit tests for problem verification. Each completed problem should have:

- A clear method signature and problem statement.
- At least three meaningful test cases.
- Boundary and edge-case tests.
- Complexity notes.
- A short explanation of the chosen approach.

For system designs, store Markdown explanations and diagrams under automation-parent/docs/system-design/ when the first design is ready. Do not create empty folders or placeholder implementations just to match the proposed tree.

## 13. Interview readiness scorecard

### DSA

- [ ] 30–35 selected Easy/Medium problems solved independently.
- [ ] Explain complexity and data-structure choices.
- [ ] Code with clean Java and test edge cases.
- [ ] Solve a familiar Easy problem in 20–30 minutes.
- [ ] Solve a familiar Medium problem in 30–40 minutes.

### System Design

- [ ] Distributed Test Automation Platform design completed.
- [ ] Scalable API Automation Framework design completed.
- [ ] Centralized Test Reporting Platform design completed.
- [ ] IoT Connected Device architecture completed.
- [ ] AI Evaluation Platform design completed.
- [ ] Explain functional and non-functional requirements.
- [ ] Discuss scalability, reliability, security, observability and trade-offs.
- [ ] Complete a 30–45 minute design discussion with a coherent diagram.

### Mock interviews

- [ ] DSA coding mock completed and reviewed.
- [ ] Test platform design mock completed and reviewed.
- [ ] IoT/AI system design mock completed and reviewed.
- [ ] Weak areas converted into specific follow-up activities.

## 14. Learning rules

1. **Solve before looking:** attempt the problem independently before reading a solution.
2. **AI as reviewer:** use Claude/Cursor to critique reasoning, tests and code after your first attempt. Do not let AI solve every problem first.
3. **Explain aloud:** verbal communication is part of interview performance.
4. **Test the solution:** sample inputs alone are insufficient.
5. **Prefer patterns over memorization:** understand why a HashMap, heap, queue or sliding window fits.
6. **Use realistic examples:** parse logs, compare API responses, group failed tests, process event streams and design test execution.
7. **Document evidence:** record solved problems, diagrams, assumptions and interview feedback.
8. **Protect the 18-month budget:** these 60 hours are integrated into the existing plan, not an additional workload.

## 15. Suggested reference material

Use references selectively to unblock a current activity:

- [NeetCode Roadmap](https://neetcode.io/roadmap) — structured DSA patterns and problem sequencing.
- [LeetCode](https://leetcode.com/) — practice and timed problem solving.
- [VisuAlgo](https://visualgo.net/) — visual explanations of data structures and algorithms.
- [System Design Primer](https://github.com/donnemartin/system-design-primer) — general system design concepts and practice questions.
- [Google SRE Books](https://sre.google/books/) — reliability, monitoring, incident response and production systems.
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/) — official Java API reference.
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) — structure and validate Java coding exercises.

Use one primary resource per topic. Do not turn resource collection into a substitute for coding and design practice.

---

## Final outcome

By the end of this track, you should be able to:

- Solve common coding problems in Java without relying on AI-generated answers.
- Explain the complexity and trade-offs of your solutions.
- Design a distributed automation and reporting platform.
- Design and test API, IoT and AI-driven systems.
- Discuss reliability, scaling, security, observability and failure recovery.
- Defend your architectural decisions in a Lead SDET / Staff SDET interview.

**The objective is not to become a competitive programmer. It is to demonstrate the coding ability, systems thinking and engineering judgment expected of a senior Quality Engineering leader.**
