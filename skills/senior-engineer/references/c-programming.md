# C — Senior Practices

C has no classes, no exceptions, no garbage collector. Discipline replaces
what the language doesn't give you.

## Memory ownership

- Every `malloc`/`calloc` has one clear owner and one matching `free`.
  Document ownership transfer explicitly in the header comment when a
  function returns allocated memory the caller must free.
```c
/* Caller owns the returned Order and must call order_destroy(). */
Order *order_create(const char *customer_id);
void order_destroy(Order *order);
```
- Never return a pointer to a stack-local variable.
- Pair every resource acquisition with a single, obvious release path —
  use `goto cleanup;` patterns for functions with multiple exit points
  and multiple resources to avoid leaking on early returns.
```c
int process(void) {
    FILE *f = fopen("data.txt", "r");
    if (!f) return -1;
    char *buf = malloc(SIZE);
    if (!buf) { fclose(f); return -1; }

    int rc = do_work(f, buf);

    free(buf);
    fclose(f);
    return rc;
}
```

## Header discipline

- One `.h` per `.c`, header declares only the public API.
- Use include guards (`#ifndef`) or `#pragma once` consistently.
- Opaque pointers for encapsulation (see `oop-concepts.md`) — don't expose
  struct fields in the header unless every consumer legitimately needs
  direct access.
- No circular `#include`s between modules — forward-declare instead, or
  fix the actual architecture violation it's revealing.

## Defensive programming

- Check every return value that can fail: `malloc`, `fopen`, syscall
  wrappers, library calls. An unchecked `NULL` deref is a crash waiting to
  happen.
- Validate all external/boundary input (sizes, ranges, null pointers)
  before use; internal-only helper functions can trust their preconditions
  if documented.
- Compile with `-Wall -Wextra -Werror` (and `-fsanitize=address,undefined`
  in test builds) — treat every warning as a bug.
- Avoid unbounded string functions (`strcpy`, `sprintf`, `gets`); use
  bounded variants (`strncpy` with explicit null-termination, `snprintf`)
  or track lengths explicitly.

## Const correctness

- Mark pointer parameters `const` when the function doesn't modify what
  they point to — it documents intent and catches accidental mutation at
  compile time.
```c
double order_total(const Order *order); /* read-only, enforced by compiler */
```

## Simulating OOP where it earns its keep

- Use opaque pointers + function tables (see `oop-concepts.md`) only where
  you have a genuine need for polymorphism (multiple interchangeable
  implementations). Don't build a vtable for a type with one implementation
  — that's speculative generality in C form.
- Prefer plain structs + free functions for the common case; reserve the
  "OOP in C" patterns for real variation points (drivers, strategies,
  plugin-style extension points).

## Common review flags

- Missing `free` for an owned allocation (leak) or a `free` without clear
  ownership documentation (potential double-free/use-after-free).
- Ignored return value from `malloc`, `fopen`, or a syscall.
- Struct fields exposed in a header with no encapsulation reason.
- Global mutable variables used for cross-function communication instead
  of explicit parameters/return values.
- Magic numbers for buffer sizes instead of named constants tied to the
  actual data they bound.
- Missing `-Wall -Wextra` in the build, or warnings left unresolved.
