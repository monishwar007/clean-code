# JavaScript / TypeScript — Senior Practices

## Typing

- Prefer TypeScript for anything beyond a trivial script. `any` on a
  public function signature is a defect, not a shortcut — use `unknown`
  and narrow, or a proper type/interface.
- Model domain concepts with types, not raw primitives:
```typescript
type Email = string & { readonly __brand: "Email" };
function parseEmail(raw: string): Email {
  if (!raw.includes("@")) throw new Error("invalid email");
  return raw as Email;
}
```
- Use `interface` for object shapes meant to be implemented/extended;
  `type` for unions, intersections, and everything else. Pick one
  convention per project and stay consistent.

## React (component design)

- One component, one responsibility — a component that fetches data,
  manages five pieces of state, and renders a complex layout should be
  split into a container + presentational component.
- Prefer composition (children, render props, hooks) over deep prop drilling.
- Custom hooks for reusable stateful logic (`useOrderForm`), not copy-pasted
  `useEffect` blocks across components.
- Keep side effects (`useEffect`) minimal and single-purpose; if an effect
  does two unrelated things, split it.

## Async / Promises

- No floating promises — every promise is awaited, returned, or explicitly
  `.catch()`-handled. Enable `no-floating-promises` (ESLint +
  `@typescript-eslint`) to catch this automatically.
- `Promise.all` for genuinely independent concurrent operations; sequential
  `await` in a loop is a smell when the operations don't depend on each other.
- Always handle rejection — an unhandled promise rejection is a defect,
  not a warning to ignore.

## Error handling

- Custom `Error` subclasses for domain errors (`class InsufficientFundsError extends Error`),
  not generic `throw new Error("...")` for conditions the caller needs to
  distinguish.
- Don't swallow errors in `catch` blocks silently — log, rethrow, or
  handle explicitly.

## Structure (Node.js/Express or similar)

- Route handlers do request/response mapping only; push logic into a
  service layer, mirroring the Java/Python layering in `architecture.md`.
- Validate request input at the boundary (e.g. `zod`/`yup` schema) before
  it reaches business logic.

## Testing

- Jest/Vitest for unit tests; React Testing Library for components —
  test behavior (what the user sees/does), not implementation details
  (internal state, specific function calls).
- Mock at the network boundary (`msw`) rather than mocking internal
  modules deeply.

## Common review flags

- `any` on new public signatures.
- Floating/unhandled promises.
- Business logic inside a route handler or a React component instead of a
  service/hook.
- Prop drilling more than 2-3 levels — extract context or restructure.
- Missing key props in lists, or using array index as key for reorderable lists.
