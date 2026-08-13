---
name: senior-engineer
description: >
  Transform junior-level code into senior-engineer quality software across
  Java/Spring Boot, Python, C, and JavaScript/TypeScript. Applies SOLID,
  TDD, clean code, design patterns, clean architecture, SQL/database
  design, API design, and concurrency — adapted per language, including
  OOP fundamentals in Java/Python/TS and their disciplined procedural
  equivalents in C. Use when writing, refactoring, reviewing, testing, or
  architecting any code in these languages.
---

# Senior Engineer Skill (Java/Spring Boot · Python · C · JS/TS · OOP)

## When to use this skill

- Writing any code: features, fixes, utilities, endpoints, modules
- Refactoring existing code
- Planning or reviewing architecture
- Reviewing code quality / doing a code review pass
- Debugging issues that stem from design, not just logic
- Creating or extending tests
- Making a design decision (pattern choice, ownership model, API shape)

## Step 0 — Detect the language context

Before applying anything, determine which language(s) are in play:

| Signal                                   | Language        | Primary reference           |
| ----------------------------------------- | --------------- | ---------------------------- |
| `.java`, `pom.xml`, `build.gradle`, `@SpringBootApplication` | Java / Spring Boot | `references/java-spring-boot.md` | <!--LANG:java-->
| `.py`, `requirements.txt`, `pyproject.toml` | Python         | `references/python.md`       | <!--LANG:python-->
| `.c`, `.h`, `Makefile`, `CMakeLists.txt`  | C                | `references/c-programming.md` | <!--LANG:c-->
| `.js`, `.ts`, `.jsx`, `.tsx`, `package.json` | JavaScript/TypeScript | `references/javascript-typescript.md` | <!--LANG:javascript-->
| Mixed / cross-service                     | All applicable  | Load each relevant reference |

If the language is ambiguous, ask once or infer from the file extension of
the file being edited. Default to Java/Spring Boot only if the user's
history or project clearly signals it.

**Example elicitation** (only when truly ambiguous — e.g. a fresh repo
with no code yet and a request like "build the order service"):
> "Which stack should I use — Java/Spring Boot, Python, or C?"

Don't ask if the answer is inferable from an open file, an existing
`pom.xml`/`requirements.txt`/`Makefile`, or prior messages in the
conversation.

## Precedence: this skill vs. `.claude/CLAUDE.md`

`.claude/CLAUDE.md` (think-before-coding, simplicity-first, surgical
changes, goal-driven execution) is the outer behavioral layer and always
applies. This skill's language/design guidance operates *inside* those
constraints, not instead of them:

- **Simplicity First (CLAUDE.md) wins over speculative pattern use** in
  `design-patterns.md` — don't introduce a Strategy pattern or value
  object for a single use, even if this skill shows the pattern.
- **TDD (this skill) is not "extra scope"** — writing a test first is part
  of solving the task correctly, not a speculative addition, so it is not
  overridden by Simplicity First.
- **Surgical Changes (CLAUDE.md) wins over "refactor smells you notice"**
  — flag a code smell you notice outside the requested change, don't fix
  it unasked, even though `code-smells.md` says to fix smells during
  refactors. That instruction applies only within the code you were asked
  to touch.
- When the two genuinely conflict and it's not obvious which wins, surface
  the tradeoff to the user instead of silently picking one (per CLAUDE.md
  §1).

## Step 1 — Load by task type (don't load everything by default)

Loading all reference docs on every task wastes context and dilutes focus.
Match the task to the row below and load only that set, plus the one
language-specific file from Step 2. `references/checklist.md` is cheap
enough to load regardless of task type.

| Task type                          | Load                                                                 |
| ----------------------------------- | ---------------------------------------------------------------------- |
| Write new code (feature/endpoint/function) | `solid-principles.md`, `tdd.md`, `object-design.md`, `checklist.md` |
| Fix a bug                           | `tdd.md` (reproduce with a test first), `checklist.md`                |
| Refactor existing code              | `code-smells.md`, `solid-principles.md`, `complexity.md`, `checklist.md` |
| Code review / review a diff         | `code-smells.md`, `clean-code.md`, `architecture.md`, `checklist.md`  |
| Design/plan architecture            | `architecture.md`, `object-design.md`, `design-patterns.md`           |
| Write or extend tests               | `tdd.md`, `testing.md`                                                |
| Anything touching OOP design choices | `oop-concepts.md` in addition to the row above                       |
| SQL / persistence work              | `sql-database.md` in addition to the row above                        |
| Building or reviewing an HTTP API   | `api-design.md` in addition to the row above                          |
| Threads/async/locking involved      | `concurrency.md` in addition to the row above                         |
| Self-check before presenting a diff | `self-review-prompt.md`                                                |

## Step 2 — Load the language-specific reference

Load exactly one (or more, if cross-language):

- `references/java-spring-boot.md` <!--LANG:java-->
- `references/python.md` <!--LANG:python-->
- `references/c-programming.md` <!--LANG:c-->
- `references/javascript-typescript.md` (React/Node.js/Express work) <!--LANG:javascript-->

## Step 3 — Apply the workflow

1. **Clarify the requirement** in one sentence before writing code.
2. **Write a failing test first** (JUnit/Mockito, pytest, or Unity/CMocka
   per `tdd.md`). Never write production code without a red test, unless
   explicitly told to skip TDD for a throwaway script.
3. **Write the minimum code** to pass the test.
4. **Refactor** against `code-smells.md` and `solid-principles.md` — extract
   value objects, remove primitive obsession, break up anything over the
   size limits in `references/limits.md`.
5. **Check architecture boundaries** — does this change stay inside its
   layer/module per `architecture.md`? Does a controller/route touch a
   repository directly? Does a C module reach into another module's
   private struct fields?
6. **Re-run the full test suite mentally** (or in the sandbox) before
   declaring done.
7. **Self-review** — for non-trivial changes, run
   `references/self-review-prompt.md` against the diff before presenting
   it. Skip for trivial changes per the CLAUDE.md tradeoff note.

See `references/examples.md` for full before/after walkthroughs of this
workflow in Java, Python, and C.

## Non-negotiable rules

- No comments that explain *what* the code does — rename instead. Comments
  are reserved for *why*, when the reason is non-obvious (e.g. a regulatory
  constraint, a workaround for a library bug).
- No God classes/functions/modules. Single Responsibility applies to files
  and modules too, not just classes.
- No public mutable state crossing a layer boundary. Use DTOs/value objects
  at boundaries (Java/Spring, Python), or plain structs passed by
  const-pointer (C).
- In C: every `malloc` has a documented owner and a matching `free`; no implicit ownership transfer without a comment stating so explicitly. <!--LANG:c-->
- In Java/Spring Boot: constructor injection only, never field injection. <!--LANG:java-->
- In Python: type hints on all public function signatures; no bare `except:`. <!--LANG:python-->
- In JS/TS: no `any` on public signatures; no floating/unhandled promises. <!--LANG:javascript-->
- Tests are not optional and are not an afterthought — they define the
  contract before the implementation exists.

## Enforcement configs

`config/` at the repo root ships machine-enforceable versions of this
guidance: `.editorconfig` (all languages)<!-- LANG:java -->, `checkstyle.xml` (Java)<!-- /LANG:java --><!-- LANG:python -->, `ruff.toml` (Python)<!-- /LANG:python --><!-- LANG:c -->, `.clang-format` + `.clang-tidy` (C)<!-- /LANG:c -->. If the target project doesn't already
have equivalents, suggest copying these in rather than relying on the
agent to remember the rules on every task.

## Output expectations

When asked to write or refactor code, produce:

1. The test(s) first (unless explicitly told otherwise).
2. The implementation.
3. A one-paragraph rationale citing which principle(s) drove each
   non-obvious design decision (skip if the change is trivial).

Do not narrate the skill mechanics to the user — apply it silently and
produce senior-engineer-quality output.
