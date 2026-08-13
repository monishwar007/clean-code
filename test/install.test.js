const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const os = require("node:os");
const { spawnSync } = require("node:child_process");

const ROOT = path.join(__dirname, "..");
const INSTALL_JS = path.join(ROOT, "install.js");

function run(args, opts = {}) {
  return spawnSync(process.execPath, [INSTALL_JS, ...args], {
    encoding: "utf8",
    ...opts,
  });
}

function tmpDir() {
  return fs.mkdtempSync(path.join(os.tmpdir(), "install-test-"));
}

test("--list exits 0 and prints all 5 physical agent destinations by default", () => {
  const dir = tmpDir();
  const result = run(["--target", dir, "--list"]);
  assert.equal(result.status, 0, result.stderr);
  for (const label of ["Claude Code", "Cursor", "Codex CLI", "OpenClaw", "Antigravity"]) {
    assert.match(result.stdout, new RegExp(label));
  }
  // antigravity/opencode/hermes share one physical destination
  assert.match(result.stdout, /Antigravity \/ OpenCode \/ Hermes Agent/);
});

test("default install writes exactly 5 physical skill directories", () => {
  const dir = tmpDir();
  const result = run(["--target", dir]);
  assert.equal(result.status, 0, result.stderr);

  const expected = [
    ".claude/skills/senior-engineer",
    ".cursor/skills/senior-engineer",
    ".codex/skills/senior-engineer",
    ".openclaw/skills/senior-engineer",
    ".agents/skills/senior-engineer",
  ];
  for (const rel of expected) {
    const full = path.join(dir, rel, "SKILL.md");
    assert.ok(fs.existsSync(full), `${full} should exist`);
  }
});

test("--agent claude installs only into .claude/skills", () => {
  const dir = tmpDir();
  const result = run(["--target", dir, "--agent", "claude"]);
  assert.equal(result.status, 0, result.stderr);
  assert.ok(fs.existsSync(path.join(dir, ".claude/skills/senior-engineer/SKILL.md")));
  assert.ok(!fs.existsSync(path.join(dir, ".cursor")));
  assert.ok(!fs.existsSync(path.join(dir, ".codex")));
});

test("--lang java excludes python/c/javascript reference files", () => {
  const dir = tmpDir();
  const result = run(["--target", dir, "--agent", "claude", "--lang", "java"]);
  assert.equal(result.status, 0, result.stderr);

  const refsDir = path.join(dir, ".claude/skills/senior-engineer/references");
  const files = fs.readdirSync(refsDir);
  assert.ok(files.includes("java-spring-boot.md"));
  assert.ok(!files.includes("python.md"));
  assert.ok(!files.includes("c-programming.md"));
  assert.ok(!files.includes("javascript-typescript.md"));
  // shared/core files still present
  assert.ok(files.includes("solid-principles.md"));
  assert.ok(files.includes("tdd.md"));
});

test("--lang java leaves no orphaned LANG marker syntax in generated SKILL.md", () => {
  const dir = tmpDir();
  run(["--target", dir, "--agent", "claude", "--lang", "java"]);
  const content = fs.readFileSync(
    path.join(dir, ".claude/skills/senior-engineer/SKILL.md"),
    "utf8"
  );
  assert.doesNotMatch(content, /LANG:/);
});

test("--lang java generated SKILL.md contains no Python/C-only lines", () => {
  const dir = tmpDir();
  run(["--target", dir, "--agent", "claude", "--lang", "java"]);
  const content = fs.readFileSync(
    path.join(dir, ".claude/skills/senior-engineer/SKILL.md"),
    "utf8"
  );
  assert.doesNotMatch(content, /In Python: type hints/);
  assert.doesNotMatch(content, /In C: every `malloc`/);
  assert.match(content, /In Java\/Spring Boot: constructor injection only/);
});

test("--lang all (default) generated SKILL.md keeps all four language rows", () => {
  const dir = tmpDir();
  run(["--target", dir, "--agent", "claude"]);
  const content = fs.readFileSync(
    path.join(dir, ".claude/skills/senior-engineer/SKILL.md"),
    "utf8"
  );
  assert.match(content, /java-spring-boot\.md/);
  assert.match(content, /python\.md/);
  assert.match(content, /c-programming\.md/);
  assert.match(content, /javascript-typescript\.md/);
});

test("reinstall without --force is blocked with a non-zero exit code", () => {
  const dir = tmpDir();
  run(["--target", dir, "--agent", "claude"]);
  const result = run(["--target", dir, "--agent", "claude"]);
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /already exist/);
});

test("reinstall with --force succeeds", () => {
  const dir = tmpDir();
  run(["--target", dir, "--agent", "claude"]);
  const result = run(["--target", dir, "--agent", "claude", "--force"]);
  assert.equal(result.status, 0, result.stderr);
});

test("unknown --agent value exits non-zero with a helpful message", () => {
  const dir = tmpDir();
  const result = run(["--target", dir, "--agent", "bogus", "--list"]);
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /Unknown --agent value/);
});

test("unknown --lang value exits non-zero with a helpful message", () => {
  const dir = tmpDir();
  const result = run(["--target", dir, "--lang", "rust", "--list"]);
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /Unknown --lang value/);
});

test("--global installs under the home directory instead of --target", () => {
  const dir = tmpDir();
  const result = run(["--global", "--agent", "codex", "--list"], { env: { ...process.env, HOME: dir } });
  assert.equal(result.status, 0, result.stderr);
  assert.match(result.stdout, new RegExp(dir.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")));
});
