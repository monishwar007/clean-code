# Object Design — Stereotypes and Responsibility Assignment

## Assigning responsibility (GRASP, simplified)

- **Information Expert** — give a responsibility to the class/module that
  already has the data needed to fulfill it. Don't pull data out just to
  compute elsewhere.
- **Creator** — a class/module creates instances of things it contains,
  aggregates, or closely uses (an `Order` creates its `LineItem`s).
- **Low Coupling / High Cohesion** — a class should depend on few others,
  and everything inside it should relate to a single purpose.
- **Tell, Don't Ask** — call a method that does the work; don't pull an
  object's internals out to make the decision yourself in the caller.
```java
// Bad — asking
if (account.getBalance() < amount) throw new InsufficientFundsException();
account.setBalance(account.getBalance() - amount);

// Good — telling
account.withdraw(amount); // throws internally if insufficient
```

## Law of Demeter

Only talk to: yourself, your parameters, objects you create, your direct
fields/components. Avoid chains like `order.getCustomer().getAddress().getCity()`
in calling code — ask the `Order` for what you need, and let it delegate.

## Value objects vs entities

- **Value object**: no identity, defined entirely by its attributes,
  immutable, replaceable. `Money`, `EmailAddress`, `DateRange`.
- **Entity**: has identity that persists across attribute changes.
  `User`, `Order` — two orders with identical line items are still
  different orders if their IDs differ.

Use value objects aggressively to eliminate primitive obsession:

**Java**
```java
public record Email(String value) {
    public Email {
        if (!value.matches(".+@.+\\..+")) throw new IllegalArgumentException("invalid email");
    }
}
```

**Python**
```python
@dataclass(frozen=True)
class Email:
    value: str
    def __post_init__(self):
        if "@" not in self.value:
            raise ValueError("invalid email")
```

**C** — a small struct with a validating constructor function; treat it as
immutable by convention (no setter functions, only a constructor and
accessors):
```c
typedef struct { char value[254]; } Email;
bool email_create(const char *raw, Email *out); /* validates, fills out */
```

## Avoiding anemic models

If every "domain" class is just data with a service class doing all the
logic, behavior has been pulled out of the object that owns the data.
Move validation, invariants, and domain calculations onto the object
itself; keep services for orchestration across multiple objects/repositories.
