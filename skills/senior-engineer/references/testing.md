# Testing Strategy

## The pyramid (still holds)

```
        /\
       /UI\        few — slow, brittle, high confidence
      /----\
     /Integ.\      some — real collaborators at the seams
    /--------\
   /Unit tests\    many — fast, isolated, cheap
  /------------\
```

## Test doubles — use the right one

| Double   | Purpose                                   |
| -------- | ------------------------------------------ |
| Dummy    | Passed but never used (fills a parameter) |
| Stub     | Returns canned answers                     |
| Fake     | Working but simplified implementation (in-memory repo) |
| Mock     | Verifies interactions happened as expected |
| Spy      | Records calls for later assertion          |

Prefer **fakes** over mocks where feasible (e.g. an in-memory
`OrderRepository`) — they're less brittle than interaction-verifying mocks
and test behavior, not implementation.

## Java

- **Unit**: JUnit 5 + Mockito, no Spring context.
- **Slice tests**: `@WebMvcTest` for controllers, `@DataJpaTest` for
  repositories — load only the relevant slice, not the full context.
- **Integration**: `@SpringBootTest` sparingly — it's slow; reserve for a
  handful of critical end-to-end paths. Use Testcontainers for real DB
  behavior instead of H2-vs-Postgres drift.
- Assert on behavior/state, not on `verify()` counts unless the interaction
  itself is the contract (e.g. "an email was sent exactly once").

## Python

- **Unit**: pytest, plain functions/classes, no framework bootstrap.
- **Integration**: pytest with `pytest-django`/`pytest-asyncio` markers as
  needed; separate marker so `pytest -m "not integration"` stays fast.
- Use `responses`/`httpx` mock transports for HTTP calls instead of
  patching internals.
- Property-based testing (`hypothesis`) for pure functions with wide input
  spaces (parsers, calculators).

## C

- **Unit**: Unity or CMocka, one test binary per module under test.
- Isolate hardware/syscalls behind a thin interface (function-pointer
  table) so tests can inject fakes — never let a unit test touch real
  hardware, files, or the network.
- Run under valgrind/AddressSanitizer in CI; a passing test with a memory
  leak or use-after-free is not a passing test.
- Keep test binaries small and fast — C test suites often get skipped in
  practice because they're slow to build; guard against that.

## Coverage philosophy

Coverage tells you what's *not* tested — it never tells you what's tested
*well*. Read the uncovered lines; don't chase a percentage. A branch on a
critical financial calculation matters more than 100 lines of
getter/setter coverage.
