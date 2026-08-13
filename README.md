# Clean Code — Java/Spring Boot · Python · C · JS/TS · OOP

[![npm](https://img.shields.io/npm/v/clean-code)](https://www.npmjs.com/package/clean-code)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Node](https://img.shields.io/badge/node-%3E%3D18-brightgreen)](package.json)

An [Agent Skill](https://agentskills.io) that makes AI coding agents write
senior-engineer quality code — SOLID, TDD, clean architecture, and
language-specific practices — across **Java/Spring Boot**, **Python**,
**C**, and **JavaScript/TypeScript**, with a shared **OOP concepts**
foundation and cross-cutting SQL/API/concurrency guidance.

Installs into **Claude Code, Cursor, Codex CLI, Antigravity, OpenCode,
OpenClaw, and Hermes Agent** with one command, and can be scoped to a
single language so you're not carrying Python/C/JS reference docs around
in a pure-Java project.

A full **before/after comparison example** (a real Spring Boot feature,
built twice — with and without this skill) is included and detailed at
the bottom of this README.

---

## Table of Contents

- [Why this exists](#why-this-exists)
- [What's inside](#whats-inside)
- [Installation](#installation)
  - [Multi-agent support](#multi-agent-support)
  - [Language-scoped install](#language-scoped-install)
  - [All install options](#all-install-options)
- [How the skill works](#how-the-skill-works)
- [Enforcement configs](#enforcement-configs)
- [Repository structure](#repository-structure)
- [Testing this repo](#testing-this-repo)
- [Comparison Example: With Skill vs. Without Skill](#comparison-example-with-skill-vs-without-skill)
- [License](#license)

---

## Why this exists

Agents left to their own defaults tend to produce code that *runs* but
isn't *maintainable*: God classes, field injection, primitive obsession,
entities leaking straight out of a REST API, no tests, silently swallowed
exceptions. None of that shows up in a quick demo — it shows up six
months later when the codebase needs to change.

This skill encodes the practices that prevent that, as reference
documentation an agent loads and applies automatically, plus machine-
enforceable lint/format configs so the guidance isn't just a suggestion
the agent can forget three files later.

## What's inside

| Principle        | Focus                                                                 |
| ------------------ | ------------------------------------------------------------------------ |
| TDD               | Red-Green-Refactor, tests before code                                  |
| SOLID             | All five principles, adapted per language (including C, which has no classes) |
| OOP Fundamentals  | Encapsulation, abstraction, inheritance, polymorphism — and their C equivalents (opaque pointers, function-pointer vtables) |
| Clean Code        | Naming, function size, formatting                                      |
| Design Patterns   | GoF patterns, applied only when they remove real duplication            |
| Architecture      | Layered/hexagonal boundaries, dependency rule                          |
| SQL/Database      | Schema design, indexing, N+1 detection, transactions, migrations       |
| API Design        | REST resource modeling, status codes, idempotency, versioning          |
| Concurrency       | Threading/async guidance across all four languages                     |

21 reference docs in total — see [Repository structure](#repository-structure).

---

## Installation

Clone the repo, or install straight from npm — every command below works
identically with **npm**, **pnpm**, or plain **node**.

```bash
# Option A: clone and run locally
git clone https://github.com/monishwar007/clean-code.git
cd clean-code
npm install            # or: pnpm install
node install.js --list # preview first, writes nothing

# Option B: run without cloning, via npm
npx clean-code --list

# Option B: run without cloning, via pnpm
pnpm dlx clean-code --list
```

Once cloned, the same install can be triggered through package scripts
too (useful if you want `--agent`/`--lang` flags to flow through a
project's existing `npm run`/`pnpm run` workflow):

```bash
npm run install-skill -- --agent claude --lang java
pnpm run install-skill -- --agent claude --lang java
```

### Multi-agent support

The skill folder (`SKILL.md` + `references/`) follows the open
[Agent Skills standard](https://agentskills.io), so the same content works
unmodified across every supported agent — they just look in different
directories. `install.js` handles placing a copy in each:

| Agent(s)                              | Directory (project scope)      |
| ---------------------------------------- | --------------------------------- |
| Claude Code                             | `.claude/skills/senior-engineer/` |
| Cursor                                   | `.cursor/skills/senior-engineer/` |
| Codex CLI                                | `.codex/skills/senior-engineer/`  |
| OpenClaw                                 | `.openclaw/skills/senior-engineer/` |
| Antigravity, OpenCode, Hermes Agent      | `.agents/skills/senior-engineer/` (shared tool-agnostic location) |

```bash
node install.js --list      # preview first, writes nothing
node install.js             # install the full pack into every known agent
```

Install into specific agents only:

```bash
node install.js --agent claude,cursor
# equivalently: npx clean-code --agent claude,cursor
# equivalently: pnpm dlx clean-code --agent claude,cursor
```

> **Agent Compatibility Note:** Hermes Agent and some emerging tools don't
> have a publicly confirmed dedicated skills directory as of this
> writing. `install.js` places their copy at the tool-agnostic
> `.agents/skills/` location (the same one Antigravity and OpenCode use),
> which is the safest current bet. If your version of one of these tools
> looks elsewhere, point it at that folder manually, or open an issue and
> the mapping in `install.js`'s `AGENT_INFO` table can be updated.

### Language-scoped install

Only writing Java? Don't install Python/C/JS reference docs you'll never
load. `--lang` filters both the reference files copied **and** the
`SKILL.md` router itself (its language-detection table, its Step 2 file
list, and its non-negotiable-rules section are all trimmed to match):

```bash
node install.js --lang java
# or: npx clean-code --lang java
# or: pnpm dlx clean-code --lang java
```

```
Installing "senior-engineer" (java) under /path/to/project:

  [ok] Claude Code -> .claude/skills/senior-engineer
  [ok] Cursor -> .cursor/skills/senior-engineer
  ...

19 files installed per agent.
```

Compare to a full install (23 files — the 4 extra are the 3 other
language references plus their share of `SKILL.md` content). Combine with
`--agent` freely:

```bash
node install.js --lang java --agent claude
node install.js --lang python,javascript --agent cursor,codex
```

### All install options

```
node install.js [--agent <list>] [--lang <list>] [--target DIR] [--global] [--force] [--list]
npx clean-code [same flags]
pnpm dlx clean-code [same flags]

--agent   claude, cursor, codex, openclaw, antigravity, opencode, hermes, all (default: all)
--lang    java, python, c, javascript, all                                   (default: all)
--target  project root to install into                                       (default: cwd)
--global  install to each agent's home-directory (personal) location instead of --target
--force   overwrite an existing install
--list    preview only — prints what would be installed, writes nothing
```

Run `npm test` (or `pnpm test`) to validate the package structure and
installer behavior — 23 automated checks covering every agent/language
combination, reinstall protection, and marker-filtering correctness (see
[Testing this repo](#testing-this-repo)).

---


## How the skill works

`SKILL.md` is the router an agent reads first. It:

1. **Detects language context** from file extensions / project markers
   (`pom.xml` → Java, `requirements.txt` → Python, `Makefile` → C,
   `package.json` → JS/TS), asking only when genuinely ambiguous.
2. **Loads references by task type**, not everything at once — writing
   new code loads a different, smaller set than reviewing a diff or
   designing architecture. See the task-loading table in `SKILL.md`.
3. **Applies a TDD workflow**: clarify → failing test → minimum code to
   pass → refactor against `code-smells.md`/`solid-principles.md` →
   check architecture boundaries → self-review against
   `self-review-prompt.md` before presenting the diff.
4. **Defers to `.claude/CLAUDE.md`** for scope discipline — this skill's
   design guidance operates *inside* "touch only what you must," not
   instead of it. `SKILL.md` has an explicit precedence section covering
   the cases that actually conflict (e.g. TDD is not "extra scope";
   flagging an unrelated smell is not the same as fixing it unasked).

## Enforcement configs

`config/` ships machine-enforceable versions of the size/style guidance,
so it's checked automatically instead of relying on the agent to remember
it every time:

- `.editorconfig` — indentation/formatting, all languages
- `checkstyle.xml` — Java: method length, cyclomatic complexity, bans
  field injection, flags empty catch blocks
- `ruff.toml` — Python: complexity, required type annotations, bugbear checks
- `.clang-format` / `.clang-tidy` — C: formatting + defensive-programming
  static analysis (bugprone-*, cert-*)

Copy the relevant ones into a target project alongside the skill.

---

## Repository structure

```
.claude/
└── CLAUDE.md                 # general behavioral guidelines (scope discipline, simplicity-first)
config/                       # machine-enforceable versions of the guidance
├── .editorconfig
├── checkstyle.xml
├── ruff.toml
├── .clang-format
└── .clang-tidy
skills/
└── senior-engineer/
    ├── SKILL.md               # router: language detection, task-based loading, workflow
    └── references/
        ├── solid-principles.md
        ├── oop-concepts.md
        ├── tdd.md
        ├── testing.md
        ├── clean-code.md
        ├── code-smells.md
        ├── design-patterns.md
        ├── architecture.md
        ├── object-design.md
        ├── complexity.md
        ├── limits.md            # canonical size/complexity numbers (single source of truth)
        ├── checklist.md         # terse pre-commit checklist
        ├── self-review-prompt.md
        ├── examples.md          # full before/after walkthroughs (Java, Python, C)
        ├── java-spring-boot.md
        ├── python.md
        ├── c-programming.md
        ├── javascript-typescript.md
        ├── sql-database.md
        ├── api-design.md
        └── concurrency.md
test/
├── structure.test.js         # validates package structure (files exist, no orphans)
└── install.test.js           # validates installer behavior across agents/languages
comparison-example/           # see "Comparison Example" section below
├── without-skill/
├── with-skill/
├── _stub-spring-api/
└── verify-compile.sh
install.js                    # multi-agent, language-filtered installer
package.json
```

## Testing this repo

```bash
npm test
```

23 checks across two suites:

- **`structure.test.js`** — every reference file `SKILL.md` points to
  actually exists, every file in `references/` is linked from somewhere,
  no orphaned/empty docs, all enforcement configs present, `install.js`
  is syntactically valid.
- **`install.test.js`** — default install writes exactly 5 physical
  directories (grouping shared-location agents correctly), `--agent`
  filters correctly, `--lang java` excludes the other three languages'
  reference files *and* leaves no leftover marker syntax in the generated
  `SKILL.md`, reinstall-without-`--force` is blocked, `--force` succeeds,
  invalid `--agent`/`--lang` values fail with a clear message, `--global`
  resolves against `$HOME`.

---

## Comparison Example: With Skill vs. Without Skill

To make the effect of this skill concrete rather than just asserted, the
same feature — an **Order Management** REST endpoint (create an order
with line items, apply a "10% off over ₹1000" discount, fetch an order,
search by customer) — is implemented twice in
[`comparison-example/`](comparison-example/):

- **`without-skill/`** — how this typically gets written with no guidance.
- **`with-skill/`** — the same feature with this skill's practices applied.

Both are real, complete Spring Boot Maven projects. Since this
environment can't reach Maven Central, `comparison-example/verify-compile.sh`
compiles both against a small hand-written stub of the Spring/JPA/JUnit/
Mockito API surface they actually use (`_stub-spring-api/`, 57 files) and
**actually runs** the pure-JUnit test to confirm the discount math is
genuinely correct, not just that the code parses:

```
$ cd comparison-example && ./verify-compile.sh
== Compiling without-skill/ ==
OK - 62 files compiled clean

== Compiling with-skill/ (main + test) ==
OK - 78 files compiled clean

== Running OrderPolicyTest for real (pure JUnit, no Mockito needed) ==
  PASS  com.example.orders.service.OrderPolicyTest#appliesTenPercentDiscountAboveThreshold
  PASS  com.example.orders.service.OrderPolicyTest#appliesNoDiscountAtOrBelowThreshold

Results: 2 passed, 0 failed
```

(`OrderServiceTest`/`OrderControllerTest` use Mockito, which the stub
doesn't functionally implement — they're verified to compile/type-check
correctly; a real `mvn test` would execute them against actual Mockito.)

### Side-by-side

**Without skill** — one file, `OrderController.java`, does everything:

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired                              // field injection
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @PostMapping
    public Order createOrder(@RequestBody Order order) {   // JPA entity as the API model
        double total = 0;                                   // double for money
        if (order.getItems() != null) {
            if (order.getItems().size() > 0) {
                for (OrderItem item : order.getItems()) {
                    if (item.getQuantity() > 0) {
                        total = total + (item.getPrice() * item.getQuantity());
                        item.setOrder(order);
                    }
                }
            }
        }
        if (total > 1000) {                                  // magic number, buried business rule
            total = total - (total * 0.1);
        }
        order.setTotal(total);
        order.setStatus("CREATED");                          // string, not an enum
        return orderRepository.save(order);                   // entity serialized straight back
    }

    @GetMapping("/search")
    public List<Order> search(@RequestParam String name) {
        String jpql = "SELECT o FROM Order o WHERE o.customerName = '" + name + "'"; // injection risk
        return entityManager.createQuery(jpql).getResultList();
    }

    @PutMapping("/{id}/status")
    public Map<String, String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Map<String, String> result = new HashMap<>();
        try {
            Order order = orderRepository.findById(id).orElse(null);
            order.setStatus(status);                           // NPE if id doesn't exist
            orderRepository.save(order);
            result.put("result", "ok");
        } catch (Exception e) {
            result.put("result", "error");                     // swallowed — caller gets no detail
        }
        return result;
    }
}
```
No service layer. No tests. No custom exceptions. `Order`/`OrderItem` are
plain JPA entities with only getters/setters — an anemic model with all
"logic" (such as it is) crammed into the controller.

**With skill** — the same feature, layered:

```java
// domain/Order.java — entity owns its own invariants
public void addItem(OrderItem item) {
    items.add(item);
    item.assignTo(this);
}
public void applyDiscount(DiscountStrategy strategy) {   // tell, don't ask
    this.appliedDiscountRate = strategy.discountRateFor(subtotal());
}

// domain/ThresholdDiscountStrategy.java — Open/Closed: new rules are a
// new class, never an edit to tested code or a growing if-chain
public BigDecimal discountRateFor(Money subtotal) {
    return subtotal.isGreaterThan(threshold) ? rate : BigDecimal.ZERO;
}

// service/OrderService.java — constructor injection, transaction boundary
public OrderService(OrderRepository orderRepository, OrderPolicy orderPolicy) {
    this.orderRepository = orderRepository;
    this.orderPolicy = orderPolicy;
}
@Transactional
public Order createOrder(CreateOrderRequest request) { ... }

// repository/OrderRepository.java — parameterized, no string concatenation
@EntityGraph(attributePaths = "items")
List<Order> findByCustomerName(String customerName);

// controller/OrderController.java — pure delegation
@PostMapping
public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    Order order = orderService.createOrder(request);
    return ResponseEntity.status(201).body(OrderResponse.from(order));
}
```
Plus: a `Money` value object (BigDecimal-backed, no float rounding
surprises), DTOs at the API boundary (`Order` the JPA entity never
leaves the service layer), `OrderNotFoundException` +
`@RestControllerAdvice` instead of a null-returning 200 or a swallowed
exception, and three real test classes
(`OrderPolicyTest`, `OrderServiceTest`, `OrderControllerTest`).

### What changed, and which reference doc drove it

| Issue in `without-skill/`                          | Fix in `with-skill/`                              | Reference doc               |
| ------------------------------------------------------ | ------------------------------------------------------ | ------------------------------ |
| Field injection (`@Autowired` on a field)               | Constructor injection                                    | `java-spring-boot.md`         |
| JPA entity returned directly from the API                | DTOs (`CreateOrderRequest`/`OrderResponse`) at the boundary | `architecture.md`, `api-design.md` |
| `double` for money                                       | `Money` value object over `BigDecimal`                    | `object-design.md`, `java-spring-boot.md` |
| Discount rule as a hardcoded `if` + magic number          | `DiscountStrategy` interface + `ThresholdDiscountStrategy` | `design-patterns.md` (Strategy, OCP) |
| String-concatenated JPQL (injection risk)                 | Spring Data derived query, parameterized automatically     | `sql-database.md`             |
| Missing order → NPE / silent `null` 200 response            | `OrderNotFoundException` + `@RestControllerAdvice`         | `code-smells.md`, `java-spring-boot.md` |
| Swallowed exception in `updateStatus`                       | No equivalent path left unhandled; errors surface with detail | `clean-code.md` (error handling) |
| Everything in one `OrderController` God class                | Controller → Service → Domain → Repository layering         | `architecture.md`             |
| Zero tests                                                    | `OrderPolicyTest`, `OrderServiceTest`, `OrderControllerTest` (TDD-first) | `tdd.md`, `testing.md`        |

### Reproduce it yourself

```bash
cd comparison-example
./verify-compile.sh          # compile-check + run OrderPolicyTest for real
cd with-skill && mvn test    # full test suite with real Mockito/JUnit (needs Maven Central access)
```

---
## License

MIT
