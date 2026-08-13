# SQL / Database Design — Senior Practices

## Schema design

- Normalize to 3NF by default; denormalize deliberately and document why
  (read-heavy reporting table, materialized view) — not as a default.
- Every table has a primary key; prefer a surrogate key (`id`) plus a
  unique constraint on the natural key, unless the natural key is stable
  and simple (e.g. ISO country code).
- Foreign keys are real constraints in the schema, not just convention
  enforced in application code — the database should refuse an orphan row.
- Use the narrowest correct type: don't store money as `FLOAT`
  (use `DECIMAL`/`NUMERIC`), don't store dates as strings.

## Indexing

- Index columns used in `WHERE`, `JOIN`, and `ORDER BY` on large tables —
  but don't index speculatively; every index has a write-cost.
- Composite indexes: column order matters — put the most selective /
  most frequently filtered-alone column first.
- Watch for indexes made useless by a function wrapped around the column
  in the query (`WHERE LOWER(email) = ...` needs a functional index or a
  normalized-at-write-time column).

## The N+1 query smell

The most common ORM-era mistake (Spring Data JPA, Django ORM, SQLAlchemy):
fetching a list, then querying again per row for a related entity.
```java
// Bad: N+1 — one query per order for its items
orders.forEach(o -> o.getItems().size());

// Good: fetch joined/graph in one query
@EntityGraph(attributePaths = "items")
List<Order> findAll();
```
Detect it by counting queries in a test (Hibernate statistics, Django
`assertNumQueries`, or query logging) — don't rely on eyeballing code.

## Transactions

- Keep transactions short — don't hold one open across a network call
  (e.g. an HTTP request to another service) inside a DB transaction.
- Choose isolation level deliberately for anything involving concurrent
  writes to the same row (READ COMMITTED is the common safe default;
  SERIALIZABLE only where true serializability is required).
- Understand the difference between optimistic locking (version column)
  and pessimistic locking (`SELECT ... FOR UPDATE`) and pick based on
  contention level, not habit.

## Writing queries

- Explicit column lists, never `SELECT *` in application code — schema
  changes shouldn't silently change what a query returns.
- Parameterized queries always — string-concatenated SQL is a SQL
  injection vulnerability regardless of "trusted" input.
- Prefer a `JOIN` over a correlated subquery when both express the same
  logic — usually clearer and often faster.

## Migrations

- Every schema change is a versioned, reversible migration file
  (Flyway/Liquibase for Java, Alembic for Python) — never a manual `ALTER
  TABLE` run by hand against production.
- Additive changes (new nullable column) are safe to deploy before the
  application code that uses them; destructive changes (drop column) go
  out only after the code that depended on it is fully removed.

## Common review flags

- `SELECT *` in application queries.
- String-concatenated SQL / no parameterization.
- Missing foreign key constraint where a relationship clearly exists.
- N+1 query pattern in a loop over ORM-fetched entities.
- `FLOAT`/`DOUBLE` for currency.
- A transaction wrapping a network call to another service.
