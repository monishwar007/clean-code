const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");

const ROOT = path.join(__dirname, "..");
const SKILL_DIR = path.join(ROOT, "skills", "senior-engineer");
const REFS_DIR = path.join(SKILL_DIR, "references");
const SKILL_MD = path.join(SKILL_DIR, "SKILL.md");

test("package.json is valid JSON with required fields", () => {
  const pkg = JSON.parse(fs.readFileSync(path.join(ROOT, "package.json"), "utf8"));
  assert.ok(pkg.name, "package.json must have a name");
  assert.ok(pkg.version, "package.json must have a version");
  assert.ok(pkg.bin, "package.json must expose a bin entry");
});

test("SKILL.md exists and has YAML frontmatter with name + description", () => {
  const content = fs.readFileSync(SKILL_MD, "utf8");
  assert.match(content, /^---\n/, "SKILL.md must start with frontmatter");
  assert.match(content, /name:\s*senior-engineer/);
  assert.match(content, /description:\s*>/);
});

test("every reference file mentioned in SKILL.md actually exists on disk", () => {
  const content = fs.readFileSync(SKILL_MD, "utf8");
  const matches = [...content.matchAll(/references\/([a-z0-9-]+\.md)/g)];
  assert.ok(matches.length > 0, "SKILL.md should reference at least one file");

  const missing = [];
  for (const [, filename] of matches) {
    const full = path.join(REFS_DIR, filename);
    if (!fs.existsSync(full)) missing.push(filename);
  }
  assert.deepEqual(missing, [], `Missing reference files: ${missing.join(", ")}`);
});

test("every .md file in references/ is referenced somewhere in SKILL.md", () => {
  const content = fs.readFileSync(SKILL_MD, "utf8");
  const actualFiles = fs.readdirSync(REFS_DIR).filter((f) => f.endsWith(".md"));
  // SKILL.md's task-loading table references files by bare name
  // (e.g. `tdd.md`), while Step 2 uses the full `references/x.md` form.
  // A file counts as linked if either form appears.
  const orphaned = actualFiles.filter((f) => !content.includes(f));
  assert.deepEqual(orphaned, [], `Reference files not linked from SKILL.md: ${orphaned.join(", ")}`);
});

test("all language-specific reference files exist", () => {
  const required = [
    "java-spring-boot.md",
    "python.md",
    "c-programming.md",
    "javascript-typescript.md",
  ];
  for (const f of required) {
    assert.ok(fs.existsSync(path.join(REFS_DIR, f)), `${f} should exist`);
  }
});

test("core reference files exist", () => {
  const required = [
    "solid-principles.md",
    "oop-concepts.md",
    "tdd.md",
    "testing.md",
    "clean-code.md",
    "code-smells.md",
    "design-patterns.md",
    "architecture.md",
    "object-design.md",
    "complexity.md",
    "limits.md",
    "checklist.md",
    "self-review-prompt.md",
    "examples.md",
    "sql-database.md",
    "api-design.md",
    "concurrency.md",
  ];
  for (const f of required) {
    assert.ok(fs.existsSync(path.join(REFS_DIR, f)), `${f} should exist`);
  }
});

test("no reference file is empty", () => {
  const files = fs.readdirSync(REFS_DIR).filter((f) => f.endsWith(".md"));
  for (const f of files) {
    const size = fs.statSync(path.join(REFS_DIR, f)).size;
    assert.ok(size > 100, `${f} looks suspiciously empty (${size} bytes)`);
  }
});

test("config/ directory ships all four enforcement configs", () => {
  const required = [".editorconfig", "checkstyle.xml", "ruff.toml", ".clang-format", ".clang-tidy"];
  for (const f of required) {
    assert.ok(fs.existsSync(path.join(ROOT, "config", f)), `config/${f} should exist`);
  }
});

test(".claude/CLAUDE.md exists at repo root", () => {
  assert.ok(fs.existsSync(path.join(ROOT, ".claude", "CLAUDE.md")));
});

test("CHANGELOG.md exists", () => {
  assert.ok(fs.existsSync(path.join(ROOT, "CHANGELOG.md")));
});

test("install.js is syntactically valid", () => {
  const { spawnSync } = require("node:child_process");
  const result = spawnSync(process.execPath, ["--check", path.join(ROOT, "install.js")]);
  assert.equal(result.status, 0, result.stderr && result.stderr.toString());
});
