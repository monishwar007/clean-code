# Clean Code — Java, Python, C

## Naming

- Names reveal intent: `daysSinceLastOrder`, not `d`. `elapsed_ms`, not `t`.
- Avoid disinformation: don't call something `accountList` if it's a `Set`.
- Booleans read as predicates: `isActive`, `has_permission`, `order_is_valid`.
- C: prefix by module to avoid symbol collisions in the global namespace —
  `order_create`, `order_destroy`, not `create`, `destroy`.
- No Hungarian notation, no single-letter names outside tiny loop indices.

## Functions / methods

- Do one thing. If you need "and" to describe it, split it.
- Size limits: see `limits.md` (canonical numbers for function length,
  parameter count, nesting depth).
- No boolean flag parameters that branch internal behavior
  (`sendEmail(user, true)`) — split into two named functions instead.
- Extract till you drop, but stop before the extraction obscures more than
  it clarifies — a function called once, used once, adding no name value,
  is over-extraction.

## Classes / modules

- Size limit: see `limits.md`. Treat crossing it as a prompt to ask "does
  this do more than one thing?", not an automatic split.
- One level of abstraction per method — don't mix low-level string
  parsing with high-level business orchestration in the same function.

## Comments

- Comments explaining *what* code does are a naming failure — fix the name.
- Comments are for *why*: a regulatory constraint, a non-obvious
  workaround, a link to a ticket explaining a hack.
- Delete commented-out code — version control remembers it, the reader
  doesn't need it staring at them.

## Formatting

- **Java**: Google Java Style or the project's existing `.editorconfig`;
  4-space indent common, braces on same line.
- **Python**: PEP 8, `black`-formatted, 4-space indent, line length 88-100.
- **C**: consistent brace style (K&R or Allman, pick one per project),
  explicit `{}` even for single-line `if` bodies — a missing brace on a
  one-liner is a classic C bug source (goto-fail style).

## Error handling

- Exceptions/errors are not control flow. Don't use exceptions for
  expected branches (e.g. "not found" in a lookup — return `Optional`
  (Java), `None`/raise a specific typed exception (Python), or a status
  enum + out-param (C)).
- Never swallow an exception/error silently. `catch (Exception e) {}`,
  `except: pass`, and an ignored C return code are all defects.
- Fail fast: validate inputs at the boundary, not three calls deep.
