# API Design — Senior Practices

## Resource modeling

- URLs name resources (nouns), not actions: `POST /orders`, not
  `POST /createOrder`.
- Use nesting only for genuine ownership: `/orders/{id}/items`, not for
  everything related.
- Plural resource names consistently (`/orders`, not `/order`).

## HTTP methods and status codes

| Method  | Use                                | Success code        |
| ------- | ------------------------------------ | ---------------------- |
| GET     | Read, no side effects, idempotent   | 200                    |
| POST    | Create, or a non-idempotent action  | 201 (with `Location`) or 200 |
| PUT     | Full replace, idempotent            | 200/204                |
| PATCH   | Partial update                       | 200/204                |
| DELETE  | Remove, idempotent                   | 204                     |

- 4xx = client error (400 validation, 401 unauthenticated, 403
  unauthorized, 404 not found, 409 conflict, 422 semantically invalid).
- 5xx = server fault — never used to signal a client mistake.
- Don't invent custom status codes or overload 200 with an error body.

## Request/response contracts

- Version the API (`/v1/...` or a header) from day one — retrofitting
  versioning after the first breaking change is expensive.
- DTOs at the boundary, not internal domain/entity objects (see
  `architecture.md`) — the wire contract and the internal model are
  allowed to diverge.
- Consistent error response shape across every endpoint:
```json
{ "error": { "code": "INSUFFICIENT_FUNDS", "message": "...", "details": {} } }
```
- Pagination for any list endpoint that can grow unbounded — don't ship
  an endpoint that returns "all rows" with no limit.

## Idempotency

- `PUT`/`DELETE` must be safe to retry with the same result.
- For `POST` operations that must not double-execute on retry (payments,
  order creation), support an idempotency key header.

## Validation

- Validate at the boundary, reject early with a clear 400/422 and field-level
  detail — don't let invalid input travel three layers deep before failing.
- Never trust client-supplied IDs for authorization decisions without
  checking ownership server-side.

## Documentation

- OpenAPI/Swagger spec kept in sync with the actual implementation
  (generate from code annotations where the framework supports it —
  springdoc-openapi for Spring Boot, FastAPI's built-in OpenAPI export —
  rather than hand-maintaining a separate spec that drifts).

## Common review flags

- Verbs in URLs (`/getOrder`, `/createUser`).
- Inconsistent error shapes across endpoints.
- No pagination on a list endpoint backed by an unbounded table.
- Internal entity/ORM object serialized directly as the response body.
- Breaking a field's meaning/type without a version bump.
- A `POST` that isn't safe against accidental duplicate submission where
  duplication has real consequences (payments, order creation).
