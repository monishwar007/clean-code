# SOLID Principles — Java, Python, C

SOLID was formulated for OOP, but every principle has a procedural
equivalent. C has no classes — apply these to **modules** (a `.c`/`.h`
pair) and **structs with function-pointer tables** where polymorphism is
needed.

## S — Single Responsibility Principle

One reason to change per unit (class, function, module).

**Java**
```java
// Bad: persistence + business rule + email in one class
class OrderService {
    void placeOrder(Order o) {
        db.save(o);
        if (o.getTotal() > 1000) o.setStatus("REVIEW");
        mailer.send(o.getCustomerEmail(), "Order placed");
    }
}

// Good: split by responsibility
class OrderService {
    private final OrderRepository repo;
    private final OrderPolicy policy;
    private final OrderNotifier notifier;

    void placeOrder(Order o) {
        policy.apply(o);
        repo.save(o);
        notifier.notifyPlaced(o);
    }
}
```

**Python**
```python
# Bad
class ReportGenerator:
    def generate(self, data):
        rows = self._compute(data)
        self._write_csv(rows)
        self._email_report()

# Good — separate compute, format, and delivery
class ReportComputer:
    def compute(self, data: list[dict]) -> list[Row]: ...

class CsvReportWriter:
    def write(self, rows: list[Row], path: str) -> None: ...
```

**C** — one `.c` file should own one concern; don't let `order.c` also
handle logging and network I/O.
```c
/* order.h — only order lifecycle operations */
typedef struct Order Order;
Order *order_create(const char *customer_id, double total);
void order_apply_policy(Order *o);
void order_destroy(Order *o);

/* order_repository.c handles persistence, not order.c */
```

## O — Open/Closed Principle

Open for extension, closed for modification — add new behavior without
editing existing, tested code.

- **Java/Python**: use interfaces/abstract base classes + strategy pattern
  instead of a growing `if/elif`/`switch` on type.
- **C**: use a function-pointer table (a "vtable") in a struct so new
  behavior is added by supplying a new table, not editing the dispatcher.

```c
typedef struct {
    double (*calculate_discount)(const Order *o);
} DiscountStrategy;

/* new strategies plug in without touching order.c */
```

## L — Liskov Substitution Principle

A subtype must be usable anywhere its base type is expected, without
surprising behavior.

- Java/Python: don't override a method to throw `UnsupportedOperationException`
  / raise `NotImplementedError` for a subset of subtypes — that's a sign the
  hierarchy is wrong; prefer composition or splitting the interface.
- C: if a function-pointer table entry is `NULL` for some "subtypes," every
  caller must null-check — that's a Liskov violation in disguise. Provide a
  no-op default instead.

## I — Interface Segregation Principle

Many small, client-specific interfaces beat one fat interface.

**Java**
```java
// Bad: one interface forces unrelated capabilities on every implementer
interface Worker { void work(); void eat(); void sleep(); }

// Good
interface Workable { void work(); }
interface Feedable { void eat(); }
```

**Python** — prefer small `Protocol`s over one large abstract base class.

**C** — split a large struct-of-function-pointers into smaller ones a
caller can depend on selectively (e.g. `Readable` vs `Writable` vtables).

## D — Dependency Inversion Principle

Depend on abstractions, not concretions. High-level policy shouldn't import
low-level detail.

**Java (Spring Boot)** — constructor-inject an interface, let Spring wire
the concrete bean:
```java
@Service
class PricingService {
    private final TaxCalculator taxCalculator; // interface

    PricingService(TaxCalculator taxCalculator) {
        this.taxCalculator = taxCalculator;
    }
}
```

**Python** — depend on a `Protocol`/ABC, inject the concrete implementation
via constructor or a factory, not a global import of a concrete class.

**C** — the high-level module owns the function-pointer struct type; the
low-level module fills it in and passes it to the high-level module at
init time. The high-level `.c` file never `#include`s the low-level
module's header directly for its concrete API.

## Quick self-check before committing code

- [ ] Does this class/function/module have exactly one reason to change?
- [ ] Can I add a new case without editing existing tested code?
- [ ] Can every subtype/implementation stand in for its abstraction?
- [ ] Are callers forced to depend on methods they don't use?
- [ ] Does the high-level policy import a concrete low-level detail?
