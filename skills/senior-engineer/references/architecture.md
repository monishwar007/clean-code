# Architecture — Layering and Module Boundaries

## The dependency rule (applies everywhere)

Dependencies point inward, toward business logic — never outward toward
frameworks, databases, or I/O. The domain layer should not import
Spring, a Python ORM, or a hardware driver header.

```
   [ Controllers / Routes / main.c ]      <- I/O boundary
              |
              v
        [ Services / Use Cases ]         <- orchestration
              |
              v
        [ Domain / Entities ]            <- pure business logic
              ^
              |
       [ Repositories / Adapters ]       <- implement domain interfaces
```

## Java / Spring Boot — layered architecture

```
com.company.app
├── controller/     REST endpoints, request/response DTOs only
├── service/        business orchestration, transaction boundaries
├── domain/         entities, value objects, domain logic — no Spring imports
├── repository/     Spring Data interfaces, implement domain-defined ports
└── config/         @Configuration, bean wiring
```
- Controllers never call repositories directly — always through a service.
- Domain classes should compile without any `org.springframework.*` import.
- Use `@Transactional` at the service layer, not scattered in repositories.

## Python — package-by-feature

```
app/
├── orders/
│   ├── domain.py       # Order, OrderPolicy — plain Python, no framework
│   ├── service.py       # use-case orchestration
│   ├── repository.py    # DB access, implements a Protocol from domain.py
│   └── api.py            # FastAPI/Flask routes
├── payments/
│   └── ...
└── shared/
```
- Package by feature, not by technical layer at the top level — it keeps
  related code together and limits blast radius of changes.
- `domain.py` defines a `Protocol` for persistence; `repository.py`
  implements it — dependency inversion without an explicit interface
  keyword.

## C — module boundaries via headers

```
src/
├── order/
│   ├── order.h        public API — opaque type + functions
│   └── order.c         private struct fields, implementation
├── order_repository/
│   ├── order_repository.h
│   └── order_repository.c   depends on order.h, not the reverse
└── main.c               composition root — wires modules together
```
- A module's `.c` file only reaches into its own struct's fields; never
  reach into another module's struct through its opaque pointer.
- Composition happens in `main.c` (or an explicit `app_init()`) — lower
  modules don't know about higher ones, mirroring Dependency Inversion.
- Circular `#include`s between modules are an architecture violation, not
  just a build error to work around with forward declarations.

## Hexagonal / Ports & Adapters (all three)

For services with real external dependencies (DB, message queue, third-party
API), define the port (interface/Protocol/function-pointer struct) in the
domain, and let the adapter (repository, HTTP client, hardware driver)
implement it. This keeps the domain testable with fakes and swappable
without touching business logic.

## Red flags in review

- A domain class importing a web framework, ORM, or hardware header.
- A controller/route with business logic instead of one-line delegation.
- A C module `#include`-ing another module's private (non-header) file.
- Circular package/module dependencies anywhere.
