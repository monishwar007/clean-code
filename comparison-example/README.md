# Comparison Example — With Skill vs. Without Skill

A single feature (Order Management: create order, apply a threshold
discount, fetch by ID, search by customer) implemented twice:

- **`without-skill/`** — how an agent commonly writes it with no guidance:
  one God-class controller, JPA entities exposed directly as the API
  model, field injection, `double` for money, a string-concatenated JPQL
  query (injection risk), swallowed exceptions, zero tests.
- **`with-skill/`** — the same feature with the `senior-engineer` skill
  applied: layered (controller → service → domain → repository), DTOs at
  the boundary, a `Money` value object, constructor injection, a Strategy
  pattern for the discount rule (Open/Closed), domain exceptions +
  `@RestControllerAdvice`, parameterized queries, and JUnit/Mockito tests.

See the root `README.md`'s "Comparison Example" section for a full
side-by-side write-up of every difference and why it matters.

## Verifying it compiles

Neither project can reach Maven Central from a locked-down sandbox, so
`_stub-spring-api/` is a minimal hand-written stub of the Spring
Boot/JPA/JUnit/Mockito surface both projects actually use — enough to
type-check real code, not a mock framework.

```bash
./verify-compile.sh
```

This compiles both projects against the stub and actually **executes**
`OrderPolicyTest` (pure JUnit, no mocking needed) to confirm the discount
math is genuinely correct — not just that the code parses.
`OrderServiceTest`/`OrderControllerTest` use Mockito, which the stub
doesn't functionally implement (no bytecode-level mocking), so those are
verified to compile/type-check correctly; run them for real with
`mvn test` once you have internet access to Maven Central.

## Running for real

Both are normal Spring Boot Maven projects:

```bash
cd with-skill    # or without-skill
mvn spring-boot:run
```
