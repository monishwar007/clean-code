# Complexity — Essential vs Accidental

## The distinction

- **Essential complexity** — inherent to the problem domain. Tax rules are
  genuinely complicated; that complexity can be organized but not removed.
- **Accidental complexity** — introduced by tooling, design choices, or
  poor structure. This is the complexity to eliminate.

Before adding an abstraction, ask: does this reduce essential complexity's
footprint, or is it accidental complexity I'm introducing to feel clever?

## Practical budgets

See `limits.md` for the canonical numbers (function length, class/module
length, parameter count, cyclomatic complexity, nesting depth) — enforced
automatically via `config/checkstyle.xml`, `config/ruff.toml`, and
`config/.clang-tidy`.

Use static analysis to enforce these, not memory: `PMD`/`Checkstyle`/
SonarLint (Java), `ruff`/`radon`/`mypy` (Python), `cppcheck`/`clang-tidy`
+ `-Wall -Wextra` (C).

## Reducing accidental complexity

- Guard clauses instead of nested `if/else`.
- Extract till the function reads like a sentence of well-named calls.
- Replace conditionals on type with polymorphism/strategy (see
  `design-patterns.md`).
- Don't abstract for a single call site — wait for a second real use case
  (Rule of Three) before generalizing.
- In C, avoid deeply nested pointer indirection (`***`) and long macro
  chains — both hide control flow from the reader.

## Cognitive load in review

If a reviewer has to hold more than ~4 things in their head to understand
one function (state from three call sites, two flags, a side effect,
and a return value), it's too complex regardless of what the cyclomatic
complexity number says. Simplify for the reader, not just the linter.
