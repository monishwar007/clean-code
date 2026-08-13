# Design Patterns — Java, Python, C

Apply a pattern only when it removes real duplication or a real
conditional — never speculatively. "We might need it later" is not
justification.

## Creational

- **Factory Method** — Java: a `PaymentGatewayFactory` returning an
  interface. Python: a function or `classmethod` returning the right
  subclass based on config. C: a function returning a populated
  function-pointer struct based on an enum/config value.
- **Builder** — Java: for objects with many optional fields (avoid
  telescoping constructors); Python: rarely needed, prefer keyword
  arguments with defaults or a `dataclass`; C: a `struct` populated via a
  sequence of `foo_builder_set_x()` calls, finalized by `foo_build()`.
- **Singleton** — use sparingly; in Spring Boot, a `@Service` bean is
  already a de-facto singleton — don't hand-roll one. In C, a `static`
  file-scope variable behind accessor functions if genuinely global state
  is unavoidable (e.g. a hardware register map).

## Structural

- **Adapter** — wrap a third-party client behind your own interface so
  your domain code doesn't depend on the vendor SDK directly (Java/Python).
  In C, wrap a vendor's C API behind your own header with your own naming.
- **Decorator** — Java: `@Around` AOP or explicit wrapping for cross-cutting
  concerns (logging, retry); Python: actual `@decorator` functions are
  idiomatic here — use them for logging/timing/retry/caching.
- **Facade** — a simple entry point over a complex subsystem (e.g. one
  `ReportService` hiding several collaborators). Useful in all three
  languages to keep call sites clean.

## Behavioral

- **Strategy** — the primary replacement for `if/switch` on type. Java:
  interface + implementations, injected. Python: pass a callable or
  `Protocol` implementation. C: a function-pointer struct (see
  `oop-concepts.md`).
- **Observer** — Java: Spring's `ApplicationEventPublisher`; Python:
  a simple callback-list pattern or a pub/sub library; C: a registered
  array/list of function pointers invoked on an event.
- **Template Method** — Java: abstract class with a final orchestrating
  method calling abstract steps; Python: base class with `NotImplementedError`
  stubs, or prefer composition; C: a driver function taking a
  function-pointer struct for the varying steps (closely related to
  Strategy in C, since there's no inheritance).
- **Command** — encapsulate a request as an object/struct so it can be
  queued, logged, or undone. Java: a `Runnable`/custom interface; Python: a
  callable or small class; C: a struct holding a function pointer + args.

## Anti-pattern watch

- Don't force a pattern where a plain function suffices — patterns exist to
  solve a recurring structural problem, not to look sophisticated.
- Don't combine 3+ patterns in a single small feature — that's usually
  overengineering, not good design.
