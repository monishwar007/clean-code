# Pre-Commit Checklist

Run through this before presenting any diff. Distilled from the full
reference set — use this instead of re-reading everything every time.

## Correctness
- [ ] A failing test existed before the implementation (TDD), or this is
      explicitly a no-test throwaway script the user asked for.
- [ ] All tests pass, including pre-existing ones.

## Scope (CLAUDE.md)
- [ ] Every changed line traces to the actual request.
- [ ] No unrelated formatting/refactoring of adjacent code.
- [ ] No speculative flexibility/config not asked for.
- [ ] Only imports/vars/functions made unused *by this change* were removed.

## Design
- [ ] No function/method over ~10-15 lines without a reason.
- [ ] No class/module over ~50-80 lines without a reason.
- [ ] No new pattern/abstraction introduced for a single use site.
- [ ] No primitive obsession for a domain concept (money, email, ID) if this
      change touches that concept directly.
- [ ] No SRP violation introduced (one new reason to change per unit).

## Language-specific quick check
- [ ] **Java**: constructor injection only; no entity returned directly from a controller.
- [ ] **Python**: type hints on new public functions; no bare `except:`.
- [ ] **C**: every new `malloc` has a documented owner and matching `free`; new code compiles clean under `-Wall -Wextra`.
- [ ] **JS/TS**: no `any` on new public function signatures; no floating promises.

## Before presenting
- [ ] State assumptions made, if any (CLAUDE.md §1).
- [ ] Flag (don't fix) any unrelated dead code or smell noticed.
- [ ] One-sentence rationale for any non-obvious design decision.
