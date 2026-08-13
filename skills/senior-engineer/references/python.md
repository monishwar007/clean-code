# Python — Senior Practices

## Style and typing

- PEP 8, `black`-formatted, `ruff` for linting.
- **Type hints on every public function/method signature.** They document
  intent and let `mypy`/agents catch mistakes before runtime.
```python
def calculate_discount(order: Order, threshold: Decimal = Decimal("1000")) -> Decimal:
    ...
```
- Use `Decimal` for money, never `float` — floating point rounding errors
  are a real production bug source.

## Data modeling

- `@dataclass(frozen=True)` for value objects; plain `@dataclass` for
  simple entities; full classes when you need real invariants/behavior.
- `Enum`/`StrEnum` instead of string constants for anything with a fixed
  set of valid values.
- `Protocol` for structural typing at boundaries instead of forcing
  inheritance from an ABC when duck typing suffices.
```python
class PaymentGateway(Protocol):
    def charge(self, amount: Decimal, token: str) -> ChargeResult: ...
```

## Error handling

- Never bare `except:` — catch specific exception types.
- Define domain-specific exceptions (`class InsufficientFundsError(Exception)`)
  rather than raising generic `Exception`/`ValueError` for domain rule
  violations.
- Use context managers (`with`) for anything with cleanup (files, DB
  sessions, locks) — don't manually try/finally when a context manager
  already exists or can be written.

## Structure

- Package by feature (see `architecture.md`), not a top-level `utils.py`
  dumping ground.
- Keep `__init__.py` thin — re-export the public API, don't put logic there.
- Avoid mutable default arguments (`def f(items=[])`); use `None` and
  assign inside the function body.
- Prefer composition + `Protocol`s over deep inheritance chains.

## Testing

- `pytest`, one test file per module (`test_order.py` for `order.py`).
- `pytest.mark.parametrize` for input variations instead of duplicated
  test functions.
- `hypothesis` for property-based tests on pure functions with wide input
  spaces.
- Mark slow/integration tests explicitly so `pytest -m "not integration"`
  stays fast for the inner dev loop.

## Async

- Don't mix sync and async DB drivers/clients in the same code path.
- `asyncio.gather` for genuinely independent concurrent I/O; don't reach
  for async where a synchronous call is simpler and fast enough.

## Common review flags

- Missing type hints on public functions.
- `except Exception:` swallowing errors without logging or re-raising.
- Business logic embedded in a Django/FastAPI view/route function instead
  of a service layer.
- `float` used for currency.
- Global mutable module-level state.
