# Worked Examples — Before / After

Full narrative examples showing the skill applied end-to-end: a
junior-quality snippet transformed by TDD + SOLID + clean code together,
not principles in isolation.

## Java — order discount calculation

**Request:** "Apply a 10% discount to orders over ₹1000."

**Before (junior)**
```java
public class OrderService {
    public void process(Order o) {
        if (o.getTotal() > 1000) {
            o.setTotal(o.getTotal() - (o.getTotal() * 0.1));
        }
        db.save(o);
        System.out.println("processed");
    }
}
```
Problems: no test, mixes discount logic + persistence + logging (SRP
violation), magic numbers, `System.out.println` instead of proper
handling, mutation logic buried in a conditional with no name.

**Step 1 — failing test first**
```java
@Test
void appliesTenPercentDiscountAboveThousand() {
    Order order = new Order(new BigDecimal("1200"));
    OrderPolicy policy = new OrderPolicy();

    policy.applyDiscount(order);

    assertEquals(new BigDecimal("1080.0"), order.getTotal());
}
```

**After**
```java
public class OrderPolicy {
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    public void applyDiscount(Order order) {
        if (order.getTotal().compareTo(DISCOUNT_THRESHOLD) > 0) {
            order.applyDiscount(DISCOUNT_RATE);
        }
    }
}

@Service
public class OrderService {
    private final OrderRepository repository;
    private final OrderPolicy policy;

    public OrderService(OrderRepository repository, OrderPolicy policy) {
        this.repository = repository;
        this.policy = policy;
    }

    public void process(Order order) {
        policy.applyDiscount(order);
        repository.save(order);
    }
}
```
Rationale: discount logic isolated (SRP, testable without a DB), magic
numbers named, `BigDecimal` for money, persistence separated from policy.

## Python — email validation on user signup

**Request:** "Validate email on signup."

**Before (junior)**
```python
def signup(name, email, password):
    if "@" not in email:
        return False
    user = {"name": name, "email": email, "password": password}
    save_to_db(user)
    return True
```
Problems: no test, no type hints, plaintext password stored, "email" is a
raw string with no real validation, `bool` return hides error detail.

**Step 1 — failing test first**
```python
def test_signup_rejects_invalid_email():
    with pytest.raises(ValueError):
        signup(name="Jane", email="not-an-email", password="hunter2")
```

**After**
```python
from dataclasses import dataclass
import re

EMAIL_PATTERN = re.compile(r"^[^@\s]+@[^@\s]+\.[^@\s]+$")

@dataclass(frozen=True)
class Email:
    value: str
    def __post_init__(self) -> None:
        if not EMAIL_PATTERN.match(self.value):
            raise ValueError(f"invalid email: {self.value}")

def signup(name: str, email: str, password: str) -> User:
    validated_email = Email(email)
    hashed = hash_password(password)
    user = User(name=name, email=validated_email, password_hash=hashed)
    user_repository.save(user)
    return user
```
Rationale: `Email` value object owns its own invariant (object
design), type hints added, password hashed not stored raw, function
raises a specific error instead of an opaque boolean.

## C — safe string copy for a config value

**Request:** "Copy a config value into a fixed buffer."

**Before (junior)**
```c
void set_config(char *dest, char *src) {
    strcpy(dest, src);
}
```
Problems: no test, no bounds check (classic buffer overflow), no `const`
on the read-only source, no return value to signal failure.

**Step 1 — failing test first**
```c
void test_set_config_rejects_oversized_input(void) {
    char dest[16];
    bool ok = set_config(dest, sizeof(dest), "this string is definitely too long");
    TEST_ASSERT_FALSE(ok);
}
```

**After**
```c
/* Returns true on success. dest_size is the full capacity of dest,
   including space for the null terminator. */
bool set_config(char *dest, size_t dest_size, const char *src) {
    if (dest == NULL || src == NULL || dest_size == 0) {
        return false;
    }
    size_t len = strlen(src);
    if (len >= dest_size) {
        return false; /* would truncate/overflow — reject explicitly */
    }
    memcpy(dest, src, len + 1);
    return true;
}
```
Rationale: bounds-checked, `const` on read-only param, explicit
success/failure return instead of silent undefined behavior on overflow.
