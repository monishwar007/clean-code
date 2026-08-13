# Code Smells — Detection and Fixes

## Universal smells

| Smell                  | Symptom                                   | Fix                                     |
| ------------------------ | -------------------------------------------- | ------------------------------------------ |
| Long function           | over limit in `limits.md`, multiple abstraction levels | Extract method              |
| God class/module        | Does everything, imports everything        | Split by responsibility (SRP)           |
| Primitive obsession     | Raw `String`/`int`/`char*` for domain concepts | Value object / struct (`Email`, `Money`) |
| Long parameter list     | 4+ params                                  | Introduce parameter object               |
| Duplicate code          | Same logic, copy-pasted                    | Extract shared function/method           |
| Feature envy            | Method uses another object's data more than its own | Move method to that object       |
| Shotgun surgery         | One change requires edits across many files | Consolidate related behavior            |
| Speculative generality  | Abstraction with one implementation, "just in case" | Delete until a second case exists |
| Magic numbers/strings   | Unexplained literals                       | Named constant                          |
| Deep nesting            | 3+ levels of `if`/`for`                     | Guard clauses, extract method           |
| Switch/if-chain on type | Repeated type-check dispatch               | Polymorphism / strategy / vtable         |

## Java-specific

- **Field injection** (`@Autowired` on a field) — use constructor injection;
  it makes dependencies explicit and testable without reflection.
- **Anemic domain model** — entities that are just getters/setters with all
  logic in service classes. Push behavior into the entity where it belongs.
- **Checked exception abuse** — forcing every caller to catch/declare an
  exception they can't meaningfully handle.
- **Optional misuse** — `Optional` as a field type or method parameter
  (it's meant for return types only).

## Python-specific

- **Bare `except:`** — swallows `SystemExit`/`KeyboardInterrupt` too; catch
  specific exceptions.
- **Mutable default arguments** — `def f(items=[])` shares state across
  calls; use `None` + assign inside.
- **God module** — a single `utils.py` that accumulates unrelated helpers.
- **Missing type hints on public APIs** — makes intent and refactoring
  safety worse for both humans and agents.
- **Stringly-typed code** — passing string constants instead of an `Enum`.

## C-specific

- **Unchecked return values** — ignoring `malloc`/`fopen`/syscall failure.
- **Unclear ownership** — a pointer returned from a function with no
  documented rule for who calls `free`.
- **Global mutable state** — makes testing and reasoning about concurrency
  nearly impossible; pass state explicitly.
- **Macro abuse** — using `#define` for anything beyond simple constants
  or genuinely necessary text substitution; prefer `const`/`static inline`.
- **Implicit int / missing prototypes** — always compile with
  `-Wall -Wextra -Werror`; a warning today is a bug tomorrow.
- **Buffer boundary assumptions** — any `strcpy`/`sprintf`/manual index
  arithmetic without an explicit bounds check.

## Refactoring discipline

Only refactor with a passing test suite as a safety net. Refactor in small
steps, re-running tests after each. Never mix a refactor with a behavior
change in the same commit — it makes the diff impossible to review safely.
