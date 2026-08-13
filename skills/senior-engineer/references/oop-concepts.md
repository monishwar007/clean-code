# OOP Concepts — and their C equivalents

C has no classes, but disciplined C code can (and should) express the same
four pillars through convention.

## 1. Encapsulation

Hide internal state; expose behavior through a controlled interface.

- **Java**: `private` fields, package-private where appropriate, getters
  only when a caller genuinely needs the value (avoid anemic
  getter/setter-only classes — that's data, not an object).
- **Python**: single leading underscore = "internal, don't touch";
  double leading underscore only when name-mangling is actually needed.
  Prefer `@property` over bare public mutable attributes for anything with
  invariants.
- **C**: use an **opaque pointer**. Declare the struct in the `.h` only as
  a forward declaration; define its fields in the `.c` file. Callers can
  only touch the struct through functions you export.
```c
/* widget.h */
typedef struct Widget Widget;
Widget *widget_create(int id);
int widget_get_id(const Widget *w);
void widget_destroy(Widget *w);

/* widget.c */
struct Widget { int id; char *name; };
```

## 2. Abstraction

Expose *what*, hide *how*.

- Java/Python: program against interfaces/ABCs (`PaymentGateway`, not
  `StripeGateway`) in calling code.
- C: expose a header describing operations (`Reader` struct of function
  pointers, or a small set of `foo_do_x()` functions); the `.c`
  implementation can change entirely without callers noticing.

## 3. Inheritance (use sparingly)

Prefer **composition over inheritance** in all three ecosystems. Inheritance
is appropriate only for genuine "is-a" relationships with stable contracts.

- Java: favor interfaces + composition; avoid deep inheritance chains
  (>2 levels is a smell).
- Python: multiple inheritance is powerful but dangerous — prefer mixins
  that add one capability each, or composition.
- C: "inheritance" is simulated by embedding a base struct as the first
  member of a derived struct, allowing safe pointer casting:
```c
typedef struct { int id; } Shape;
typedef struct { Shape base; double radius; } Circle; /* Circle* can be
                                                          cast to Shape* */
```

## 4. Polymorphism

Same interface, different behavior per concrete type.

- Java: interface + multiple implementations, dispatched by the JVM
  via vtables automatically.
- Python: duck typing — no explicit interface required, but a `Protocol`
  documents the expected shape.
- C: manual vtable — a struct of function pointers, populated differently
  per "type," with a `void *self` or embedded base struct as the first
  argument to every function:
```c
typedef struct {
    double (*area)(const void *self);
} ShapeOps;

double total_area(const void *shapes[], const ShapeOps *ops[], int n) {
    double sum = 0;
    for (int i = 0; i < n; i++) sum += ops[i]->area(shapes[i]);
    return sum;
}
```

## Object stereotypes (applies across languages)

When designing a class/struct, name its stereotype — it clarifies
responsibility immediately:

| Stereotype        | Holds state? | Has behavior? | Example                          |
| ------------------ | ------------- | --------------- | ----------------------------------- |
| Value Object        | Yes (immutable) | Minimal (equality, validation) | `Money`, `EmailAddress` |
| Entity               | Yes (identity + mutable) | Yes | `Order`, `User`          |
| Service              | No (stateless) | Yes | `TaxCalculator`, `OrderPolicy`     |
| Repository            | No (delegates) | Yes (persistence only) | `OrderRepository`     |
| Data Transfer Object | Yes (plain)   | No             | `OrderResponseDto`               |

Never mix stereotypes in one type (e.g. an Entity that also does HTTP
calls, or a DTO with business logic).
