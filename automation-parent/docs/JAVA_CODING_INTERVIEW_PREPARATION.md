# Java Coding Interview Preparation — 10 Core Topics for SDET / Lead SDET

> **Repository:** [IntelliJJavaSQL](https://github.com/kkmahajan/IntelliJJavaSQL)
> **Learning branch:** `feature/multi-module-test-framework`
> **Java baseline:** Java 21 (the automation framework compiles for Java 21)
> **Audience:** SDET, Senior SDET, Lead Quality Engineer and Staff SDET interviews
> **Purpose:** A practical study guide with Java patterns, complexity, SDET applications, exercises and interview checkpoints.

This guide is designed to be used alongside the repository's Maven multi-module automation framework. The examples are interview-practice snippets; they are not claims that these algorithms are already implemented in the sample application. Add completed exercises to a dedicated practice package or module only when that fits the repository's architecture.

## Learning outcomes

After completing the ten topics, you should be able to:

1. Translate a problem statement into input constraints, edge cases and expected behaviour.
2. Explain a simple solution before optimizing it.
3. Select appropriate Java data structures and algorithms.
4. Write readable, null-safe code with meaningful names and focused methods.
5. Explain time and auxiliary-space complexity.
6. Apply the same patterns to API payloads, test data, logs, execution reports and parallel test suites.

## Recommended interview-solving protocol

Use this sequence for every coding question:

1. **Clarify:** Ask about input size, duplicates, ordering, null/empty inputs, case sensitivity, character set and whether mutation is allowed.
2. **Work an example:** Walk through a normal case and at least one edge case.
3. **Baseline:** State the simplest correct solution and its complexity.
4. **Optimize:** Choose a data structure or algorithm that improves the important cost.
5. **State the invariant:** Explain what remains true while the loop or algorithm runs.
6. **Implement:** Prefer clear code over clever code. Handle invalid inputs according to the stated contract.
7. **Validate:** Test normal, boundary, duplicate, empty and adversarial cases.
8. **Analyze:** Explain time complexity and auxiliary space separately.

### Complexity quick reference

| Complexity | Typical interpretation | Example |
|---|---|---|
| O(1) | Constant work | Array index access |
| O(log n) | Repeatedly halves a search space | Binary search on sorted data |
| O(n) | Visits each element a constant number of times | Array traversal |
| O(n log n) | Efficient comparison sorting | General-purpose sorting |
| O(n²) | Compares many pairs | Nested-loop duplicate check |
| O(n) space | Additional storage grows with input | HashSet of seen values |

> Complexity depends on the operation and implementation. State assumptions such as average-case HashMap access, sorted input, or extra storage when explaining an answer.

---

# Topic 1 — Strings and Arrays

**Priority:** High · **Suggested practice time:** 2–3 focused sessions

## Concepts to understand

### Strings

- `String` is immutable. Methods such as `concat`, `replace` and `toUpperCase` return a new String; they do not modify the original object.
- Use `equals` for content comparison. `==` compares object references.
- Use `StringBuilder` for repeated mutable string construction in a single thread. `StringBuffer` is synchronized and is usually not the default choice for local string construction.
- Know `length()`, `charAt()`, `substring()`, `contains()`, `indexOf()`, `split()`, `toCharArray()` and `equalsIgnoreCase()`.
- Be explicit about case, whitespace and punctuation normalization.
- Java `char` represents a UTF-16 code unit, not always a complete Unicode character. Typical interview exercises use basic English letters; Unicode-aware solutions may need `codePoints()`.

### Arrays

- Arrays have a fixed length and store values of one declared component type.
- Index access and assignment are O(1); traversal and linear search are O(n).
- `Arrays.sort` sorts the supplied array in place. Copy first when the original order must be preserved.
- Check for `null`, empty arrays, one-element arrays, duplicates, negative values and integer boundary conditions.

## Common patterns and examples

### Reverse a String without `StringBuilder.reverse()`

```java
public static String reverse(String input) {
    if (input == null) {
        throw new IllegalArgumentException("input must not be null");
    }

    char[] chars = input.toCharArray();
    int left = 0;
    int right = chars.length - 1;

    while (left < right) {
        char current = chars[left];
        chars[left] = chars[right];
        chars[right] = current;
        left++;
        right--;
    }

    return new String(chars);
}
```

Time: O(n). Auxiliary space: O(n) for the character array and returned String.

### Palindrome check

```java
public static boolean isPalindrome(String value) {
    if (value == null) {
        return false;
    }

    int left = 0;
    int right = value.length() - 1;
    while (left < right) {
        if (value.charAt(left) != value.charAt(right)) {
            return false;
        }
        left++;
        right--;
    }
    return true;
}
```

This version is case-sensitive and treats spaces/punctuation as characters. If the requirement says to ignore them, normalize the input first and document the rule.

### Second-largest distinct number in one pass

```java
public static int secondLargestDistinct(int[] values) {
    if (values == null || values.length < 2) {
        throw new IllegalArgumentException("At least two values are required");
    }

    Integer largest = null;
    Integer second = null;

    for (int value : values) {
        if (largest == null || value > largest) {
            second = largest;
            largest = value;
        } else if (value != largest && (second == null || value > second)) {
            second = value;
        }
    }

    if (second == null) {
        throw new IllegalArgumentException("No second distinct value exists");
    }
    return second;
}
```

Time: O(n). Auxiliary space: O(1). The `Integer` sentinels allow arrays containing `Integer.MIN_VALUE` without using an unsafe numeric sentinel.

## SDET applications

- Compare expected and actual response fields.
- Normalize IDs or text values before comparison when the contract permits it.
- Find maximum/second-maximum latency values from response measurements.
- Validate arrays of device IDs, permissions, API records or event attributes.
- Parse a compact test log or extract a token from a response.

## Practice exercises

1. Reverse a String without using `StringBuilder.reverse()`.
2. Check whether a String is a palindrome; add a second version that ignores case and non-alphanumeric characters.
3. Find the second-largest **distinct** number in an array.
4. Find all duplicated values in an integer array.
5. Given values from 1 through N with exactly one missing value, find the missing value. Clarify assumptions and consider integer overflow before choosing a sum formula.

## Interview checkpoint

Be able to explain String immutability, `==` versus `equals`, StringBuilder versus StringBuffer, array access complexity, in-place sorting, and the edge cases for second-largest distinct value.

---

# Topic 2 — HashMaps

**Priority:** High · **Suggested practice time:** 2–3 focused sessions

## Concepts to understand

- `Map<K, V>` stores key/value associations. A key is unique; putting an existing key replaces its value.
- `HashMap` offers expected/average O(1) lookup, insertion and removal under a good hash distribution. Worst-case behaviour can be worse; do not promise unconditional O(1).
- `HashMap` does not guarantee iteration order. Use `LinkedHashMap` when insertion order matters and `TreeMap` when sorted-key order is required.
- One `null` key and null values are permitted by `HashMap`; other Map implementations may differ.
- `equals()` and `hashCode()` must be consistent for custom key types. Equal objects must have equal hash codes.
- A mutable object used as a key is dangerous if fields involved in `equals`/`hashCode` change after insertion.

## Common patterns and examples

### Frequency counting

```java
public static Map<String, Integer> countWords(List<String> words) {
    Map<String, Integer> counts = new HashMap<>();
    for (String word : words) {
        counts.merge(word, 1, Integer::sum);
    }
    return counts;
}
```

### Two Sum — unsorted input

```java
public static int[] twoSum(int[] values, int target) {
    Map<Integer, Integer> indexByValue = new HashMap<>();

    for (int i = 0; i < values.length; i++) {
        int complement = target - values[i];
        Integer matchingIndex = indexByValue.get(complement);
        if (matchingIndex != null) {
            return new int[] { matchingIndex, i };
        }
        indexByValue.put(values[i], i);
    }

    return new int[0]; // Explicitly represents no pair found in this example.
}
```

Expected time: O(n). Auxiliary space: O(n). The method returns the first pair found; a production API should define its no-solution and multiple-solution contract clearly.

### First non-repeating character

```java
public static Character firstUniqueCharacter(String input) {
    Map<Character, Integer> counts = new LinkedHashMap<>();
    for (char ch : input.toCharArray()) {
        counts.merge(ch, 1, Integer::sum);
    }

    for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
        if (entry.getValue() == 1) {
            return entry.getKey();
        }
    }
    return null;
}
```

## SDET applications

- Map API records by ID to compare expected and actual results.
- Count pass/fail/skip outcomes or failure messages in test reports.
- Compare headers, query parameters and JSON fields.
- Aggregate events by device ID or correlation ID.
- Detect repeated test IDs or duplicate event keys.

Example: compare two lists of API objects by ID using a Map rather than repeatedly scanning one list for each element.

## Practice exercises

1. Count the frequency of each character in a String.
2. Solve Two Sum for an unsorted array in O(n) expected time.
3. Find the first non-repeating character.
4. Group words that are anagrams of each other.
5. Compare two API response lists by unique ID and report missing, extra and changed records.

## Interview checkpoint

Explain collision handling at a high level, expected versus worst-case complexity, the `equals`/`hashCode` contract, why HashMap order is not guaranteed, and when LinkedHashMap or TreeMap is more appropriate.

---

# Topic 3 — ArrayList vs LinkedList

**Priority:** High · **Suggested practice time:** 1–2 focused sessions

## Concepts to understand

| Operation | ArrayList | LinkedList |
|---|---:|---:|
| Get by index | O(1) | O(n) |
| Search by value | O(n) | O(n) |
| Append at end | Amortized O(1) | O(1) |
| Insert/remove at a known index | O(n) due to shifting | O(n) to locate; O(1) relinking once positioned |
| Memory/cache behaviour | Compact backing array; good locality | Node objects and links; more overhead |

Important nuances:

- `ArrayList` is backed by a resizable array. Appending is amortized O(1), although an occasional resize copies elements.
- `LinkedList` is doubly linked. It is not automatically faster for insertion/removal by index because locating the position costs O(n).
- When an iterator or node already identifies the position, linked-list relinking is O(1), but this advantage is often overstated in interview answers.
- Neither class is thread-safe by default.
- In most everyday application and test-framework code, prefer `ArrayList` unless a measured access pattern justifies another structure.

## Examples

```java
List<String> testNames = new ArrayList<>();
testNames.add("api-smoke");
testNames.add("ui-login");
testNames.add("device-sync");

for (String testName : testNames) {
    System.out.println(testName);
}
```

If the primary need is queue or stack behaviour, consider `ArrayDeque` instead of choosing LinkedList by habit.

## SDET applications

- Store ordered test cases, API records and execution results.
- Use indexed access when repeatedly reading an element by position.
- Use a Deque for pending work or retry queues.
- Avoid concurrent modification when multiple workers share a mutable list.

## Practice exercises

1. Remove an element by value and explain the cost.
2. Remove elements matching a predicate using `removeIf`.
3. Merge two ordered lists into one ordered list.
4. Implement a simple queue with `ArrayDeque`.
5. Choose a collection for a test result pipeline and justify it using access patterns, ordering and thread-safety needs.

## Interview checkpoint

Explain why ArrayList is usually the default, why LinkedList indexed insertion is O(n), what amortized O(1) means, and why neither collection should be assumed thread-safe.

---

# Topic 4 — Sets and Duplicate Detection

**Priority:** High · **Suggested practice time:** 1–2 focused sessions

## Concepts to understand

- A Set stores unique elements according to equality semantics.
- `HashSet`: expected O(1) add/contains/remove; no guaranteed order.
- `LinkedHashSet`: maintains insertion order, typically with expected O(1) membership operations.
- `TreeSet`: maintains sorted order; add/contains/remove are O(log n) under normal balanced-tree behaviour.
- `HashSet` relies on `equals` and `hashCode` in the same way as HashMap keys.
- A Set removes duplicates; it does not tell you how many times a value appeared unless you count separately.

## Examples

### Preserve first-seen order while removing duplicates

```java
public static <T> List<T> distinctInOrder(List<T> values) {
    return new ArrayList<>(new LinkedHashSet<>(values));
}
```

### Return duplicate values

```java
public static Set<Integer> findDuplicates(int[] values) {
    Set<Integer> seen = new HashSet<>();
    Set<Integer> duplicates = new LinkedHashSet<>();

    for (int value : values) {
        if (!seen.add(value)) {
            duplicates.add(value);
        }
    }
    return duplicates;
}
```

Expected time: O(n). Auxiliary space: O(n). Each duplicate value is returned once, in the order its repeated occurrence is first encountered.

## SDET applications

- Ensure test IDs, device IDs or event IDs are unique.
- Detect duplicate records in an API response.
- Compare expected and actual sets of permissions or features when order is irrelevant.
- Preserve input order when de-duplicating test names for a report.

## Practice exercises

1. Remove duplicate integers while preserving insertion order.
2. Find duplicate values in an array in expected O(n) time.
3. Find the intersection of two arrays.
4. Check whether two arrays contain the same distinct values, regardless of order.
5. Explain how a custom `Device` class should implement `equals` and `hashCode` before it is stored in a HashSet.

## Interview checkpoint

Compare HashSet, LinkedHashSet and TreeSet. Explain ordering, expected complexity, duplicate equality and the custom-object `equals`/`hashCode` contract.

---

# Topic 5 — Sorting

**Priority:** High · **Suggested practice time:** 1–2 focused sessions

## Concepts to understand

- Sorting arranges values according to natural ordering or a Comparator.
- Comparison sorting is generally O(n log n) for Java's general-purpose sort operations; check the API contract for the particular overload rather than assuming one implementation algorithm.
- `Arrays.sort(array)` sorts an array in place. `Collections.sort(list)` and `list.sort(comparator)` sort a list in place.
- A Comparator should be consistent and transitive. Prefer `Integer.compare(a, b)` over `a - b`, which can overflow.
- Object-array sorting in Java is stable; do not rely on stability for primitive arrays because equal primitive values have no associated identity to preserve.
- Define tie-breaking explicitly with `thenComparing`.

## Examples

### Sort integers

```java
int[] latencies = { 240, 90, 180, 120 };
Arrays.sort(latencies);
// [90, 120, 180, 240]
```

### Sort test results by status and then name

```java
record TestResult(String name, String status, long durationMs) {}

List<TestResult> results = new ArrayList<>();
results.sort(
    Comparator.comparing(TestResult::status)
              .thenComparing(TestResult::name)
);
```

### Sort by duration descending without subtraction overflow

```java
results.sort(Comparator.comparingLong(TestResult::durationMs).reversed());
```

## SDET applications

- Rank slowest API calls or tests.
- Sort test failures deterministically for stable reports.
- Order defects by severity and age.
- Compare sorted representations when input order is not part of the API contract.

## Practice exercises

1. Sort an integer array in ascending and descending order.
2. Find the second-largest distinct number after sorting; compare the complexity with the one-pass solution in Topic 1.
3. Sort test results by status, then duration descending, then name.
4. Merge two already-sorted arrays.
5. Given intervals representing test execution windows, sort them by start time as preparation for interval-merging problems.

## Interview checkpoint

Explain in-place sorting, stable sorting, natural ordering versus Comparator, tie-breaking, and why subtraction should not be used as a general comparator implementation.

---

# Topic 6 — Two Pointers

**Priority:** High · **Suggested practice time:** 2 focused sessions

## Concepts to understand

Two pointers maintain two positions in a collection and move them according to a rule. Common forms are:

- Opposite ends: one pointer starts at the beginning and one at the end.
- Fast/slow: pointers move at different rates or represent a read and write position.
- Two sorted inputs: one pointer advances in each input.

The pattern is especially effective on sorted arrays or strings because ordering provides a reason to discard part of the search space.

## Examples

### Pair sum in a sorted array

```java
public static boolean hasPairWithSum(int[] sortedValues, int target) {
    int left = 0;
    int right = sortedValues.length - 1;

    while (left < right) {
        long sum = (long) sortedValues[left] + sortedValues[right];
        if (sum == target) {
            return true;
        }
        if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    return false;
}
```

Precondition: input is sorted ascending. Time: O(n). Auxiliary space: O(1). The `long` sum prevents overflow when two int values are added.

### Remove duplicates from a sorted array in place

```java
public static int uniqueCount(int[] sortedValues) {
    if (sortedValues.length == 0) {
        return 0;
    }

    int write = 1;
    for (int read = 1; read < sortedValues.length; read++) {
        if (sortedValues[read] != sortedValues[write - 1]) {
            sortedValues[write++] = sortedValues[read];
        }
    }
    return write; // Unique values occupy [0, write).
}
```

## SDET applications

- Compare two sorted API response lists.
- Find mismatches between expected and actual IDs.
- Remove repeated sorted event IDs in a compact result.
- Check whether two sorted timelines or sequences align.

## Practice exercises

1. Determine whether a sorted array contains a pair adding to a target.
2. Reverse an array in place.
3. Remove duplicates from a sorted array and return the logical new length.
4. Merge two sorted arrays.
5. Move all zero values to the end while preserving the relative order of non-zero values.

## Interview checkpoint

Explain why sorting enables two pointers, what invariant determines which pointer moves, and whether the solution changes the input or uses extra memory.

---

# Topic 7 — Sliding Window

**Priority:** High · **Suggested practice time:** 2 focused sessions

## Concepts to understand

A sliding window maintains a contiguous range and updates it incrementally instead of recalculating every range from scratch.

- Fixed-size window: the window length is known (for example, maximum average over k measurements).
- Variable-size window: expand and shrink while maintaining a condition (for example, longest substring without repeated characters).
- Most common positive/monotonic-condition problems can be solved in O(n) because each element enters and leaves the window at most once.
- A simple variable window does not solve every problem. Negative numbers can invalidate greedy shrink/expand logic for some sum constraints; prefix sums or other techniques may be required.

## Examples

### Maximum sum of any contiguous subarray of size k

```java
public static long maxWindowSum(int[] values, int k) {
    if (values == null || k <= 0 || k > values.length) {
        throw new IllegalArgumentException("k must be between 1 and array length");
    }

    long windowSum = 0;
    for (int i = 0; i < k; i++) {
        windowSum += values[i];
    }

    long max = windowSum;
    for (int right = k; right < values.length; right++) {
        windowSum += values[right] - values[right - k];
        max = Math.max(max, windowSum);
    }
    return max;
}
```

Time: O(n). Auxiliary space: O(1).

### Longest substring without repeating characters

```java
public static int longestUniqueSubstringLength(String input) {
    if (input == null) {
        throw new IllegalArgumentException("input must not be null");
    }

    Map<Character, Integer> lastSeenIndex = new HashMap<>();
    int left = 0;
    int maxLength = 0;

    for (int right = 0; right < input.length(); right++) {
        char current = input.charAt(right);
        Integer previous = lastSeenIndex.put(current, right);
        if (previous != null && previous >= left) {
            left = previous + 1;
        }
        maxLength = Math.max(maxLength, right - left + 1);
    }
    return maxLength;
}
```

This version works on UTF-16 `char` units. For full Unicode code-point semantics, use code points and track code-point positions.

## SDET applications

- Calculate rolling error rates or latency summaries.
- Find the highest failure count within a fixed number of recent executions.
- Identify the longest sequence of unique event IDs or test names.
- Analyze telemetry or logs over a moving time/count window.

## Practice exercises

1. Find the maximum sum of a contiguous subarray of size k.
2. Find the average of every contiguous window of size k.
3. Find the longest substring without repeating characters.
4. Find the smallest contiguous subarray with sum at least a target for positive numbers; explain why the positivity condition matters.
5. Find the longest run/window satisfying a stated failure-rate or threshold condition.

## Interview checkpoint

Explain fixed versus variable windows, the condition that allows the window to shrink, why each pointer moves at most O(n) times, and when negative numbers require another technique.

---

# Topic 8 — Stacks and Queues

**Priority:** Medium–High · **Suggested practice time:** 2 focused sessions

## Concepts to understand

### Stack (LIFO)

Last In, First Out. Core operations are push, pop and peek.

### Queue (FIFO)

First In, First Out. Core operations are offer, poll and peek.

- Prefer `Deque` with `ArrayDeque` for ordinary stack/queue work in single-threaded code rather than the legacy `Stack` class.
- `ArrayDeque` does not permit null elements.
- Use `BlockingQueue` or another concurrent queue when producer and consumer threads need coordination; a plain ArrayDeque is not thread-safe.
- A monotonic stack is useful for next-greater-element and histogram patterns.

## Examples

### Valid parentheses

```java
public static boolean hasValidParentheses(String input) {
    Deque<Character> stack = new ArrayDeque<>();
    Map<Character, Character> closingToOpening = Map.of(
        ')', '(', ']', '[', '}', '{'
    );

    for (char ch : input.toCharArray()) {
        if (ch == '(' || ch == '[' || ch == '{') {
            stack.push(ch);
        } else if (closingToOpening.containsKey(ch)) {
            if (stack.isEmpty() || stack.pop() != closingToOpening.get(ch)) {
                return false;
            }
        }
    }
    return stack.isEmpty();
}
```

This sample ignores non-bracket characters. If the input contract requires only brackets, reject other characters explicitly.

### Queue using ArrayDeque

```java
Deque<String> pendingTests = new ArrayDeque<>();
pendingTests.offer("api-smoke");
pendingTests.offer("ticket-regression");

String next = pendingTests.poll(); // api-smoke; null when empty
```

## SDET applications

- Validate nested JSON-like delimiters or expressions.
- Manage pending test tasks in a sequential runner.
- Track nested steps or retry work.
- Use a queue for breadth-first traversal of dependency graphs.
- Use a monotonic stack for next-greater latency or nearest-higher-value questions.

## Practice exercises

1. Validate parentheses/brackets.
2. Implement a queue using two stacks.
3. Implement a stack using two queues.
4. Find the next greater element for each array element.
5. Explain when a BlockingQueue is preferable to ArrayDeque in a producer-consumer test executor.

## Interview checkpoint

Explain LIFO versus FIFO, `push/pop/peek` versus `offer/poll/peek`, why ArrayDeque is often preferred to Stack, and why a plain Deque cannot be shared safely between threads.

---

# Topic 9 — Java Streams

**Priority:** High · **Suggested practice time:** 2–3 focused sessions

## Concepts to understand

- Streams process data from a source through a pipeline; they are not collections and generally do not store elements.
- Intermediate operations such as `filter`, `map`, `flatMap`, `distinct`, `sorted`, `limit` and `peek` are lazy.
- Terminal operations such as `collect`, `toList`, `forEach`, `count`, `reduce`, `anyMatch` and `findFirst` trigger processing.
- A Stream should generally be consumed once. Create a new stream from the source for another pipeline.
- Avoid side effects in stream operations. Prefer collectors for aggregation.
- `Collectors.toMap` throws on duplicate keys unless a merge function is supplied.
- Parallel streams are not automatically faster; consider workload size, splitting cost, shared state, ordering and the common ForkJoinPool.

## Examples

### Filter and transform test failures

```java
List<String> failedNames = results.stream()
    .filter(result -> "FAILED".equals(result.status()))
    .map(TestResult::name)
    .sorted()
    .toList();
```

Assumes `TestResult` is the record introduced in Topic 5.

### Group execution results by status

```java
Map<String, List<TestResult>> byStatus = results.stream()
    .collect(Collectors.groupingBy(TestResult::status));
```

### Count results by status

```java
Map<String, Long> countByStatus = results.stream()
    .collect(Collectors.groupingBy(
        TestResult::status,
        Collectors.counting()
    ));
```

### Convert records to a Map with duplicate-key handling

```java
Map<String, TestResult> resultByName = results.stream()
    .collect(Collectors.toMap(
        TestResult::name,
        Function.identity(),
        (first, later) -> later
    ));
```

The merge function explicitly keeps the later record. Choose a merge rule that matches the business requirement; do not silently discard duplicates without a decision.

## SDET applications

- Aggregate pass/fail/skip counts.
- Group failures by exception type, feature or test tag.
- Extract slow tests and sort them by duration.
- Transform API response objects into expected lookup structures.
- Produce concise test-run summaries from raw execution records.

## Practice exercises

1. Filter failed tests and return sorted test names.
2. Group tests by status and count each group.
3. Find the five slowest tests.
4. Convert API records to a Map by ID and handle duplicate IDs explicitly.
5. Flatten a list of test suites containing lists of test results using `flatMap`.

## Interview checkpoint

Explain lazy evaluation, intermediate versus terminal operations, `map` versus `flatMap`, `reduce` versus `collect`, duplicate-key handling in `toMap`, and why side effects/parallel streams can cause problems.

---

# Topic 10 — Concurrency Fundamentals

**Priority:** High · **Suggested practice time:** 3–4 focused sessions

## Concepts to understand

- A process has its own resources and address space; threads within a process share memory.
- Concurrency allows overlapping progress. Parallelism means work is actually executing simultaneously on multiple processing resources.
- A race condition occurs when correctness depends on timing/interleaving of unsynchronized operations on shared state.
- `synchronized` provides mutual exclusion and visibility guarantees for the protected monitor.
- `volatile` provides visibility/order guarantees for a variable, but does not make compound operations such as `count++` atomic.
- `AtomicInteger` provides atomic operations for a single value.
- `ExecutorService` manages task execution. Prefer bounded, intentionally sized pools for controlled workloads; shut down executors cleanly.
- `Future` represents an asynchronous result. `CompletableFuture` supports composition of asynchronous tasks.
- Use concurrent collections such as `ConcurrentHashMap` when shared concurrent access is required.
- Java 21 includes virtual threads. They can simplify high-concurrency blocking I/O workloads, but do not make CPU-bound work faster and do not remove the need to reason about shared mutable state.

## Examples

### Race-prone counter versus atomic counter

```java
AtomicInteger completed = new AtomicInteger();
completed.incrementAndGet();
int total = completed.get();
```

An `AtomicInteger` is appropriate for independent atomic updates to one integer. It is not a replacement for coordinating complex multi-field state transitions.

### Execute independent tasks with an ExecutorService

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
try {
    List<Callable<String>> tasks = List.of(
        () -> runApiSmoke("users"),
        () -> runApiSmoke("tickets"),
        () -> runApiSmoke("health")
    );

    List<Future<String>> results = executor.invokeAll(tasks);
    for (Future<String> result : results) {
        System.out.println(result.get()); // Propagates task failure via ExecutionException.
    }
} finally {
    executor.shutdown();
}
```

This is an illustrative pattern; `runApiSmoke` represents an application-specific method. In production code, include timeout, cancellation and shutdown-await policies appropriate to the runner.

## SDET applications

- Execute independent API tests in parallel.
- Prevent shared test data and report writers from racing.
- Make WebDriver instances thread-confined (for example, one driver per worker) rather than sharing a single mutable driver across tests.
- Use unique test data and cleanup to avoid cross-test interference.
- Make test reporting aggregation safe under parallel execution.
- Set explicit resource limits for browsers, devices, API rate limits and database connections.

## Common parallel-test hazards

| Hazard | Typical mitigation |
|---|---|
| Shared mutable test data | Unique records, isolated fixtures, cleanup |
| Shared WebDriver | One driver per test/worker; controlled lifecycle |
| Static mutable fields | Remove shared state or synchronize the narrow critical section |
| Concurrent report writes | Thread-safe reporting API or per-test result files plus aggregation |
| Too many browser sessions | Bounded worker pool and device capacity limits |
| Order-dependent tests | Remove hidden dependencies; make setup/teardown explicit |
| Hung tasks | Timeouts, cancellation and diagnostic logging |

## Practice exercises

1. Write two tasks and run them through a fixed thread pool.
2. Demonstrate a race condition with a shared integer, then correct it using AtomicInteger or synchronization.
3. Explain why `volatile int count; count++` is not atomic.
4. Design a parallel API test run that isolates test data and aggregates results safely.
5. Explain how you would manage one WebDriver per parallel test worker and guarantee driver cleanup after failure.

## Interview checkpoint

Explain race conditions, visibility, atomicity, `synchronized` versus `volatile`, AtomicInteger, ExecutorService, Future, thread-safe collections and test isolation. Be able to describe when parallel execution makes a suite less reliable rather than faster.

---

# Consolidated complexity and pattern sheet

| Topic / pattern | Typical time | Auxiliary space | Key assumption |
|---|---:|---:|---|
| String reverse with char array | O(n) | O(n) | UTF-16 char-unit reversal |
| Palindrome two ends | O(n) | O(1) | Comparison rule is defined |
| Array max / second max | O(n) | O(1) | One pass |
| HashMap frequency count | O(n) expected | O(k) | k distinct keys |
| Two Sum with HashMap | O(n) expected | O(n) | Unsorted input |
| HashSet duplicate detection | O(n) expected | O(n) | Hashing behaves normally |
| ArrayList indexed get | O(1) | O(1) | Existing list |
| LinkedList indexed get | O(n) | O(1) | Must traverse nodes |
| General comparison sorting | O(n log n) typical contract-level expectation | Depends on implementation | Check Java API for selected overload |
| Two pointers on sorted array | O(n) | O(1) | Input sorted |
| Fixed sliding window | O(n) | O(1) | Window size valid |
| Variable sliding window | O(n) typical | O(k) if a map/set tracks state | Window condition supports monotonic movement |
| Stack / queue operations | O(1) amortized for ArrayDeque end operations | O(n) total storage | Single-threaded use |
| Stream filter/map | O(n) | Depends on operation/collector | Pipeline is sequential unless specified otherwise |
| Parallel task execution | Work-dependent | Thread/task resources | Isolation and bounded capacity required |

# SDET-focused coding challenge bank

Use these as follow-up problems after the core exercises. They connect algorithm patterns to the kind of data handled by automation frameworks.

| Challenge | Main pattern | Expected outcome |
|---|---|---|
| Find duplicate test case IDs | HashSet / HashMap | Duplicate ID list with counts |
| Compare expected and actual API records by ID | HashMap / Set | Missing, extra and changed records |
| Find the slowest five API calls | Sorting / Streams | Top five by duration |
| Count failures by exception type | HashMap / Streams | Deterministic grouped summary |
| Find longest run of consecutive passing tests | Sliding window / scan | Longest consecutive pass length |
| Validate nested test-step delimiters | Stack | Valid/invalid nesting |
| Merge two sorted execution timelines | Two pointers | One ordered timeline |
| Execute independent API checks | Concurrency | Bounded parallel execution and aggregated results |
| Find events repeated within a time/count window | Sliding window + Map/Deque | Duplicate event detection under a stated window rule |
| Compare two report snapshots | Map + Set + sorting | Added, removed and changed test results |

# Suggested 10-week study plan

A suggested cadence is one main topic per week, with two or three short sessions. Adjust the pace to the career plan's weekend-only time budget. The hours below are study targets, not completed work.

| Week | Topic | Suggested hours | Weekly evidence |
|---:|---|---:|---|
| 1 | Strings and Arrays | 2.5 | Five independently solved exercises |
| 2 | HashMaps | 2.5 | Five problems including Two Sum and API comparison |
| 3 | ArrayList vs LinkedList | 1.5 | Collection choice note plus five exercises |
| 4 | Sets and Duplicate Detection | 1.5 | Duplicate/intersection utilities and tests |
| 5 | Sorting | 1.5 | Comparator exercises and complexity notes |
| 6 | Two Pointers | 2.0 | Five problems with invariants explained |
| 7 | Sliding Window | 2.0 | Fixed and variable window implementations |
| 8 | Stacks and Queues | 2.0 | Bracket validator and queue/stack exercises |
| 9 | Java Streams | 2.0 | Test-report aggregation examples |
| 10 | Concurrency Fundamentals | 3.0 | Parallel task runner with isolation notes |

## Completion criteria for each topic

Mark a topic complete only when all of the following are true:

- [ ] I can explain the core concept without reading notes.
- [ ] I can implement at least three representative problems independently.
- [ ] I can test normal, edge and invalid inputs.
- [ ] I can state time and auxiliary-space complexity.
- [ ] I can explain one practical SDET application.
- [ ] I can discuss at least three related interview questions.
- [ ] I have committed the practice code or notes to the learning branch.

## Suggested repository organization for future practice code

Keep study examples separate from the production-like automation framework modules. A future learning module could use a structure such as:

```text
automation-parent/
└── coding-practice/
    ├── pom.xml
    └── src/
        ├── main/java/org/example/practice/
        │   ├── arrays/
        │   ├── maps/
        │   ├── collections/
        │   ├── sorting/
        │   ├── twopointers/
        │   ├── slidingwindow/
        │   ├── stackqueue/
        │   ├── streams/
        │   └── concurrency/
        └── test/java/org/example/practice/
            └── ...
```

This is a proposed structure only. Do not add a new Maven module until there are enough exercises to justify it and the parent POM/module list has been reviewed.

## Recommended references

Prefer official Java documentation for API behaviour and use coding-problem sites for practice, not as a substitute for understanding complexity and edge cases.

- [Java 21 API documentation](https://docs.oracle.com/en/java/javase/21/docs/api/)
- [Java Collections Framework](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/package-summary.html)
- [String API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html)
- [Stream API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html)
- [ExecutorService API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html)
- [NeetCode practice roadmap](https://neetcode.io/roadmap)
- [LeetCode](https://leetcode.com/problemset/)

## Interview readiness self-assessment

At the end of the ten topics, do a timed practice round:

1. Solve one easy array/string problem in 15 minutes.
2. Solve one medium HashMap/two-pointer/sliding-window problem in 25–30 minutes.
3. Explain one collection or stream design choice in five minutes.
4. Explain a race condition and a safe parallel-test architecture in five minutes.
5. For every solution, state assumptions, complexity and at least three edge cases.

The aim is not to memorize ten solutions. It is to recognize the underlying patterns and apply them to unfamiliar problems while communicating trade-offs clearly.