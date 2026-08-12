# Changelog

All notable changes to this skill package are documented here.

## [1.2.1] — Repo rename, npm/pnpm install support

### Changed
- Package/repo renamed to `clean-code` (was `senior-engineer-skill` /
  originally `solid-skills-multi`). Updated `package.json` (`name`, `bin`,
  `repository`, `homepage`, `bugs`) and all README install instructions.
- README installation section rewritten to show equivalent `npm`, `pnpm`,
  and plain `node` invocations side by side (`npx clean-code`,
  `pnpm dlx clean-code`, `node install.js`, plus `npm run install-skill --`
  / `pnpm run install-skill --` for flag passthrough).

## [1.2.0] — Multi-agent install, language scoping, comparison example

### Added
- Multi-agent installer: `install.js --agent <list>` supports Claude Code,
  Cursor, Codex CLI, OpenClaw, Antigravity, OpenCode, and Hermes Agent
  (the latter three share the tool-agnostic `.agents/skills/` location).
- Language-scoped installer: `install.js --lang <list>` installs only the
  requested language's reference file(s) + shared core, and generates a
  filtered `SKILL.md` (via inline `<!--LANG:x-->` markers) instead of
  shipping all four languages' content into a single-language project.
- `--global` flag to install to each agent's home-directory location.
- `test/install.test.js` — 12 new automated checks covering agent
  grouping, language filtering, marker-cleanliness of the generated
  `SKILL.md`, reinstall protection, and invalid-flag handling.
- `comparison-example/` — a complete Spring Boot "Order Management"
  feature built twice (with and without the skill applied), plus
  `_stub-spring-api/` (a hand-written stub of the Spring/JPA/JUnit/Mockito
  surface used) and `verify-compile.sh`, which compiles both projects and
  actually executes the pure-JUnit test to confirm business logic
  correctness, not just that the code parses.
- Comprehensive top-level `README.md` for GitHub publishing, covering the
  skill, multi-agent installation, language-scoped installation, and a
  full side-by-side write-up of the comparison example.

### Changed
- `package.json` version bumped to 1.2.0.
- `npm test` now runs 23 checks total (11 structure + 12 installer).

## [1.1.0] — Agent improvements pass

### Added
- `references/checklist.md` — terse pre-commit checklist.
- `references/self-review-prompt.md` — reusable self-review prompt template.
- `references/limits.md` — canonical size/complexity limits, single source
  of truth (previously duplicated across `clean-code.md`, `code-smells.md`,
  `complexity.md`).
- `references/examples.md` — full before/after worked examples (Java,
  Python, C) applying the whole workflow end-to-end.
- `references/javascript-typescript.md` — JS/TS + React/Node practices.
- `references/sql-database.md` — schema design, indexing, N+1 detection,
  transactions, migrations.
- `references/api-design.md` — REST resource modeling, status codes,
  idempotency, versioning.
- `references/concurrency.md` — threading/async guidance across Java,
  Python, C, JS/TS.
- `config/` — machine-enforceable configs: `.editorconfig`,
  `checkstyle.xml`, `ruff.toml`, `.clang-format`, `.clang-tidy`.
- `test/structure.test.js` — validates SKILL.md references all exist,
  every reference file is linked, no orphaned/empty docs.
- `install.js --list` — preview install contents without copying.
- `.claude/CLAUDE.md` — general behavioral guidelines (think-before-coding,
  simplicity-first, surgical changes, goal-driven execution).
- Precedence section in `SKILL.md` clarifying how this skill and
  `.claude/CLAUDE.md` interact when guidance conflicts.
- Task-type-based reference loading table in `SKILL.md` (replaces
  load-everything guidance).

### Changed
- `clean-code.md`, `code-smells.md`, `complexity.md` now point to
  `limits.md` instead of repeating size/complexity numbers.
- `SKILL.md` frontmatter description updated to include JS/TS, SQL, API
  design, and concurrency.

## [1.0.0] — Initial release

- Ported and retargeted from `ramziddin/solid-skills` (TypeScript/NestJS
  focus) to Java/Spring Boot, Python, and C, with OOP concepts as a shared
  foundation.
- `skills/senior-engineer/SKILL.md` + 13 reference docs.
- Node.js `install.js` installer, no external CLI dependency.
