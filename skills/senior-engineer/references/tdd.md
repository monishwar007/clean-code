# Test-Driven Development — Red-Green-Refactor

## The cycle (non-negotiable order)

1. **Red** — write a test for behavior that doesn't exist yet. Run it. It
   must fail (and fail for the *right* reason — a compile error doesn't
   count as a valid red).
2. **Green** — write the minimum code to pass. Resist the urge to
   generalize early.
3. **Refactor** — clean up duplication/smells with the safety net of a
   passing test. Re-run tests after every small change.

Never write production code that isn't demanded by a failing test, except
for scaffolding (e.g. an empty class stub needed for the test to compile).

## Java (JUnit 5 + Mockito)

```java
@Test
void appliesTenPercentDiscountAboveThreshold() {
    Order order = new Order(1200.0);
    OrderPolicy policy = new OrderPolicy();

    policy.apply(order);

    assertEquals(1080.0, order.getTotal());
}
```
- One assertion concept per test (multiple `assertEquals` for one logical
  outcome is fine; multiple unrelated behaviors is not).
- Name tests `methodUnderTest_condition_expectedResult` or a readable
  sentence — pick one convention and stay consistent.
- Mock only true collaborators (external systems, repositories); don't
  mock value objects or the class under test.

## Python (pytest)

```python
def test_applies_ten_percent_discount_above_threshold():
    order = Order(total=1200.0)

    apply_discount(order)

    assert order.total == 1080.0
```
- Use `pytest.mark.parametrize` for the same behavior across inputs
  instead of copy-pasted test functions.
- Use fixtures for setup, not `setUp`-style shared mutable state unless
  using `unittest.TestCase` deliberately.
- `monkeypatch` / `unittest.mock.patch` for collaborators — patch at the
  point of use, not the point of definition.

## C (Unity or CMocka)

```c
void test_apply_discount_above_threshold(void) {
    Order o = order_create(1200.0);

    order_apply_discount(&o);

    TEST_ASSERT_EQUAL_DOUBLE(1080.0, order_get_total(&o));
    order_destroy(&o);
}
```
- Every test that allocates must free — a leaking test is a failing test
  in spirit, catch it with valgrind/ASan in CI.
- Use CMocka's mock functions (`will_return`, `expect_value`) to fake
  hardware/syscalls/collaborator modules; don't test against real I/O.
- Prefer testing pure functions with no global state — if a function needs
  globals to be testable, that's a design smell, not a testing problem.

## What TDD does NOT mean

- It does not mean 100% coverage as a target — coverage is a byproduct,
  not a goal. A test suite that pins down bad design is worse than no
  suite.
- It does not mean testing private implementation details — test observable
  behavior through the public interface.
- It does not replace integration/contract tests — TDD drives unit-level
  design; you still need broader tests for the seams (Spring `@SpringBootTest`
  slices, Python `pytest` integration markers, C hardware-in-the-loop or
  fakes for syscalls).
