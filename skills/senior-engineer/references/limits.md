# Canonical Limits

Single source of truth for size/complexity numbers used across
`clean-code.md`, `code-smells.md`, `complexity.md`, and the `config/`
lint configs. If you tune these, update them here only — the other docs
reference this file rather than repeating the numbers.

| Metric                              | Soft limit | Hard signal to refactor |
| ------------------------------------- | ------------ | -------------------------- |
| Function/method length               | 10-15 lines | 20+ lines                 |
| Class/module length                  | 50-80 lines | 100+ lines                |
| Function parameters                  | 0-3          | 4+ (bundle into a value object/struct) |
| Cyclomatic complexity per function   | 10           | 15+                        |
| Nesting depth                        | 2-3 levels   | 4+ (use guard clauses)     |
| File length (any language)           | 300 lines    | 400+                        |

These are soft defaults tuned for readability, not hard rules — a
genuinely cohesive function slightly over the line is fine; the number
exists to prompt a "should this be split?" question, not to force a split
mechanically.

`config/checkstyle.xml`, `config/ruff.toml`, and `config/.clang-tidy`
encode these numbers so violations surface automatically instead of
relying on manual review.
