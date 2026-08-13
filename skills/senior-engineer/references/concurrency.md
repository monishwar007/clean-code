# Concurrency — Java, Python, C, JS/TS

## General principles

- Prefer immutable data shared across threads — if it can't change, it
  can't race.
- Minimize shared mutable state; where unavoidable, protect it with the
  narrowest lock/mechanism that's correct, held for the shortest time.
- Never guess at thread-safety — document it explicitly on any class/
  module/function that's meant to be used concurrently.

## Java

- Prefer `java.util.concurrent` utilities (`ExecutorService`,
  `ConcurrentHashMap`, `CompletableFuture`) over hand-rolled `Thread`/
  `synchronized` code.
- Virtual threads (Java 21+) for high-throughput I/O-bound work; don't
  reach for them for CPU-bound work where the platform-thread pool is
  already sized correctly.
- `@Transactional` methods and thread-safety are separate concerns —
  a transactional service can still race on in-memory state if it isn't
  otherwise synchronized.
- Watch for lazy Spring beans with mutable state — Spring singleton scope
  means one instance shared across all request threads by default.

## Python

- The GIL means threads don't give CPU parallelism for pure-Python code —
  use `threading` for I/O-bound concurrency, `multiprocessing` (or a
  process pool) for CPU-bound parallelism.
- `asyncio` for I/O-bound concurrency at scale — but never mix blocking
  calls (`requests`, blocking DB drivers) inside an `async def` without
  offloading them (`run_in_executor` or an async-native client).
- Don't share mutable objects across `multiprocessing` workers without an
  explicit IPC mechanism (`Queue`, `Manager`) — each process has its own
  memory space.

## C

- `pthreads` (POSIX) or platform threading API — always pair shared state
  with an explicit mutex; document which mutex protects which fields in a
  comment next to the struct definition.
- Watch for classic hazards: data races (unsynchronized shared access),
  deadlock (inconsistent lock ordering across threads), and use of
  non-reentrant functions (`strtok`, `localtime`) from multiple threads —
  use the `_r` reentrant variants.
- Compile/test concurrency-sensitive code with ThreadSanitizer
  (`-fsanitize=thread`) — races often don't show up in normal testing.
- Prefer message-passing (a queue + one owning thread) over shared mutable
  state where the design allows it — it eliminates whole classes of bugs.

## JavaScript / TypeScript (Node.js)

- Node is single-threaded for JS execution — "concurrency" here means
  concurrent I/O via the event loop, not parallel CPU work.
- A synchronous CPU-heavy operation blocks the entire event loop for
  every request — offload to a worker thread (`worker_threads`) or a
  separate service for genuinely CPU-bound work.
- Race conditions still exist at the *logical* level even single-threaded:
  two concurrent async handlers can interleave and both read stale state
  before either writes — guard with an explicit lock/queue pattern or
  atomic DB operations, not in-process assumptions.

## Common review flags (any language)

- Shared mutable state with no documented synchronization strategy.
- A lock held across a blocking I/O call or network request.
- Inconsistent lock acquisition order across two code paths (deadlock risk).
- "It probably won't race in practice" as the justification for skipping
  synchronization.
