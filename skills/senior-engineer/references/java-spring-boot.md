# Java / Spring Boot — Senior Practices

## Dependency injection

- **Constructor injection only.** Never `@Autowired` on a field — it hides
  dependencies, defeats final fields, and makes unit testing without
  Spring painful.
```java
@Service
public class OrderService {
    private final OrderRepository repository;
    private final OrderPolicy policy;

    public OrderService(OrderRepository repository, OrderPolicy policy) {
        this.repository = repository;
        this.policy = policy;
    }
}
```
- Use Lombok's `@RequiredArgsConstructor` on `final` fields only if the
  team already uses Lombok consistently — don't introduce it for one class.

## Layering

- Controller → Service → Repository. Controllers do request/response
  mapping only, no business logic.
- Use request/response DTOs (records) at the controller boundary — never
  expose JPA entities directly over the API (leaks persistence concerns,
  couples API contract to schema).
```java
public record CreateOrderRequest(String customerId, List<LineItemRequest> items) {}
public record OrderResponse(String id, String status, BigDecimal total) {}
```

## Exception handling

- Domain-specific unchecked exceptions (`InsufficientFundsException`), not
  generic `RuntimeException`.
- Centralize HTTP mapping with `@RestControllerAdvice` /
  `@ExceptionHandler` — don't try/catch in every controller method.
- Avoid checked exceptions for anything the caller can't meaningfully
  recover from at that call site.

## JPA / persistence

- Keep entities focused on persistence + basic invariants; push complex
  business rules into domain services if they span multiple entities.
- Avoid `FetchType.EAGER` by default — prefer `LAZY` and fetch explicitly
  (`@EntityGraph` or a query) to avoid N+1 queries.
- Use `@Transactional` at the service method boundary, one clear
  transaction per use case — not nested across multiple service calls.

## Testing

- `@WebMvcTest` for controller slice tests (MockMvc, no DB).
- `@DataJpaTest` for repository slice tests (embedded/Testcontainers DB).
- Plain JUnit + Mockito for services — no Spring context needed if
  dependencies are interfaces injected via constructor.
- Reserve `@SpringBootTest` for a small number of true end-to-end paths.

## Concurrency & config

- Prefer immutable configuration objects bound via `@ConfigurationProperties`
  over scattered `@Value` injections.
- Be explicit about thread-safety for any `@Service` holding mutable state
  (rare — services should generally be stateless).
- Use `CompletableFuture`/virtual threads (Java 21+) deliberately, not as
  a default — most Spring MVC endpoints don't need manual async.

## Common review flags

- Field injection anywhere.
- Business logic in `@RestController` methods.
- JPA entities returned directly from controllers.
- `catch (Exception e) { log.error(...); }` with no rethrow or recovery.
- God service classes accumulating unrelated use cases — split by
  use case/feature instead.
