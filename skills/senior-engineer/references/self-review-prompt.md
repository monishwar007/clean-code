# Self-Review Prompt Template

Run this as a final internal pass against your own diff before presenting
it to the user. It's a single consolidated check replacing a full re-read
of `code-smells.md` + `solid-principles.md`.

## Prompt

```
Review the following diff against these criteria, in order. For each,
answer pass/fail with a one-line reason. If any fail, fix before
presenting.

1. SCOPE: Does every changed line trace to the user's actual request?
   (no unrelated refactors, no adjacent "improvements")
2. TESTS: Does a test exist that would fail without this change, and pass
   with it?
3. SRP: Does any touched class/function/module now have more than one
   reason to change?
4. SIZE: Is any new/modified function over ~15 lines, or class/module over
   ~80 lines, without a stated reason?
5. NAMING: Would a reader understand what each new name does without
   reading its body?
6. SMELLS: Any of — primitive obsession, long parameter list (4+),
   duplicated logic, deep nesting (3+), magic literals, swallowed
   errors/exceptions?
7. LANGUAGE-SPECIFIC:
   - Java: constructor injection only? No entity returned from a controller?
   - Python: type hints present? No bare except?
   - C: every malloc has a documented owner and matching free? Compiles
     clean under -Wall -Wextra?
   - JS/TS: no `any` introduced? No floating promises?
8. SPECULATION: Any abstraction, config option, or flexibility added that
   wasn't asked for and has only one current use site?

<diff>
{{DIFF}}
</diff>
```

## How to use it

- Substitute `{{DIFF}}` with the actual diff/new code before the final
  response.
- Treat this as an internal step — don't show the raw checklist output to
  the user; just fix what fails, then present the corrected diff with a
  short rationale for any non-obvious call.
- For trivial changes (typo fix, one-line config change), skip this — use
  judgment per the CLAUDE.md tradeoff note.
