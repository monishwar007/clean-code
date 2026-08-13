#!/usr/bin/env node

/**
 * install.js
 *
 * Installs the "senior-engineer" skill into one or more AI coding agents'
 * skill directories, optionally filtered to a single language.
 *
 * The skill folder itself (SKILL.md + references/) follows the open
 * Agent Skills standard (agentskills.io), so the same files work
 * unmodified across every agent below — this script only handles placing
 * a (possibly language-filtered) copy into each agent's expected
 * directory, since agents differ on *where* they look, not what they read.
 *
 * Usage:
 *   node install.js                          # install full pack into all known agents (project scope)
 *   node install.js --agent claude,cursor     # install into specific agents only
 *   node install.js --lang java               # install only Java/Spring Boot content (+ shared core)
 *   node install.js --agent claude --lang java
 *   node install.js --target DIR              # install relative to DIR instead of CWD
 *   node install.js --global                  # install to each agent's home-directory (personal) location
 *   node install.js --force                   # overwrite existing installs
 *   node install.js --list                    # preview only, no files written
 *
 * --agent accepts a comma-separated list of: claude, cursor, codex,
 *   openclaw, antigravity, opencode, hermes, all (default: all)
 * --lang accepts a comma-separated list of: java, python, c, javascript,
 *   all (default: all)
 */

const fs = require("fs");
const path = require("path");
const os = require("os");

const SKILL_NAME = "senior-engineer";
const SOURCE_SKILL_DIR = path.join(__dirname, "skills", SKILL_NAME);
const SOURCE_SKILL_MD = path.join(SOURCE_SKILL_DIR, "SKILL.md");
const SOURCE_REFS_DIR = path.join(SOURCE_SKILL_DIR, "references");

// Where each agent looks for Agent-Skills-standard skill folders.
// Antigravity, OpenCode, and Hermes Agent all converge on the emerging
// tool-agnostic `.agents/skills/` location (OpenCode is configurable and
// documented to fall back here; Hermes Agent has no confirmed dedicated
// convention as of this writing, so `.agents/skills/` is the safest bet —
// see README "Agent Compatibility Notes").
const AGENT_INFO = {
  claude: { dir: ".claude/skills", label: "Claude Code" },
  cursor: { dir: ".cursor/skills", label: "Cursor" },
  codex: { dir: ".codex/skills", label: "Codex CLI" },
  openclaw: { dir: ".openclaw/skills", label: "OpenClaw" },
  antigravity: { dir: ".agents/skills", label: "Antigravity" },
  opencode: { dir: ".agents/skills", label: "OpenCode" },
  hermes: { dir: ".agents/skills", label: "Hermes Agent" },
};
const ALL_AGENT_KEYS = Object.keys(AGENT_INFO);

// Language-specific reference files. Every other .md file in references/
// is treated as shared/core and always included regardless of --lang.
const LANGUAGE_FILES = {
  java: "java-spring-boot.md",
  python: "python.md",
  c: "c-programming.md",
  javascript: "javascript-typescript.md",
};
const ALL_LANG_KEYS = Object.keys(LANGUAGE_FILES);

function parseArgs(argv) {
  const args = {
    target: process.cwd(),
    force: false,
    list: false,
    global: false,
    agent: "all",
    lang: "all",
  };
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    if (a === "--target" && argv[i + 1]) {
      args.target = path.resolve(argv[++i]);
    } else if (a === "--force") {
      args.force = true;
    } else if (a === "--list") {
      args.list = true;
    } else if (a === "--global") {
      args.global = true;
    } else if (a === "--agent" && argv[i + 1]) {
      args.agent = argv[++i];
    } else if (a === "--lang" && argv[i + 1]) {
      args.lang = argv[++i];
    }
  }
  return args;
}

function parseListArg(raw, validKeys, allKeys, flagName) {
  const requested = raw.split(",").map((s) => s.trim()).filter(Boolean);
  if (requested.includes("all")) return new Set(allKeys);
  const invalid = requested.filter((k) => !validKeys.includes(k));
  if (invalid.length > 0) {
    console.error(
      `Unknown ${flagName} value(s): ${invalid.join(", ")}. Valid: ${validKeys.join(", ")}, all`
    );
    process.exit(1);
  }
  return new Set(requested);
}

/** Group agent keys by their target directory so shared locations
 *  (e.g. .agents/skills used by antigravity/opencode/hermes) are only
 *  written once, with a combined label for clear reporting. */
function groupAgentsByDir(agentKeys) {
  const groups = new Map(); // dir -> { dir, labels: [] }
  for (const key of agentKeys) {
    const info = AGENT_INFO[key];
    if (!groups.has(info.dir)) groups.set(info.dir, { dir: info.dir, labels: [] });
    groups.get(info.dir).labels.push(info.label);
  }
  return [...groups.values()];
}

/** Strip/resolve <!--LANG:x--> markers in SKILL.md content for the
 *  selected language set. Two marker forms are used in the source file,
 *  both single-line so they never break markdown table/list continuity:
 *    - trailing:  `...row content... <!--LANG:x-->`        (keep/drop whole line)
 *    - paired:    `...<!-- LANG:x -->inner text<!-- /LANG:x -->...` (keep/drop inner span only)
 */
function filterByLanguage(content, langSet) {
  const keepAll = langSet.has("all") || ALL_LANG_KEYS.every((l) => langSet.has(l));

  if (keepAll) {
    return content.replace(/<!--\s*\/?LANG:\w+\s*-->/g, "");
  }

  const lines = content.split("\n");
  const out = [];

  for (let line of lines) {
    // Paired inline markers: keep inner text only if tag is selected.
    line = line.replace(
      /<!--\s*LANG:(\w+)\s*-->([\s\S]*?)<!--\s*\/LANG:\1\s*-->/g,
      (_m, tag, inner) => (langSet.has(tag) ? inner : "")
    );

    // Trailing single marker: drop the whole line if tag isn't selected.
    const trailing = line.match(/<!--LANG:(\w+)-->\s*$/);
    if (trailing) {
      if (!langSet.has(trailing[1])) continue;
      line = line.replace(/\s*<!--LANG:\w+-->\s*$/, "");
    }

    // Safety cleanup for any remaining marker syntax.
    line = line.replace(/<!--\s*\/?LANG:\w+\s*-->/g, "");
    out.push(line);
  }

  return out.join("\n");
}

/** Determine which references/*.md files to include for the given
 *  language set: all shared/core files, plus only the requested
 *  language-specific files. */
function selectReferenceFiles(langSet) {
  const all = fs.readdirSync(SOURCE_REFS_DIR).filter((f) => f.endsWith(".md"));
  const langFileNames = new Set(Object.values(LANGUAGE_FILES));
  return all.filter((f) => {
    if (!langFileNames.has(f)) return true; // shared/core, always included
    const lang = Object.keys(LANGUAGE_FILES).find((k) => LANGUAGE_FILES[k] === f);
    return langSet.has(lang);
  });
}

function buildPayload(langSet) {
  const skillMdRaw = fs.readFileSync(SOURCE_SKILL_MD, "utf8");
  return {
    skillMd: filterByLanguage(skillMdRaw, langSet),
    referenceFiles: selectReferenceFiles(langSet),
  };
}

function writeSkill(destSkillDir, payload, force) {
  if (fs.existsSync(destSkillDir)) {
    if (!force) return { ok: false, reason: "exists" };
    fs.rmSync(destSkillDir, { recursive: true, force: true });
  }
  const refsDest = path.join(destSkillDir, "references");
  fs.mkdirSync(refsDest, { recursive: true });
  fs.writeFileSync(path.join(destSkillDir, "SKILL.md"), payload.skillMd);
  for (const f of payload.referenceFiles) {
    fs.copyFileSync(path.join(SOURCE_REFS_DIR, f), path.join(refsDest, f));
  }
  return { ok: true };
}

function main() {
  const { target, force, list, global, agent, lang } = parseArgs(process.argv.slice(2));

  const agentKeys = [...parseListArg(agent, ALL_AGENT_KEYS, ALL_AGENT_KEYS, "--agent")];
  const langSet = parseListArg(lang, ALL_LANG_KEYS, ALL_LANG_KEYS, "--lang");
  const groups = groupAgentsByDir(agentKeys);
  const baseRoot = global ? os.homedir() : target;
  const payload = buildPayload(langSet);

  const langLabel =
    langSet.has("all") || ALL_LANG_KEYS.every((l) => langSet.has(l))
      ? "all languages"
      : [...langSet].join(", ");

  if (list) {
    console.log(`Would install "${SKILL_NAME}" (${langLabel}) under ${baseRoot} for:\n`);
    for (const g of groups) {
      console.log(`  ${g.labels.join(" / ")}`);
      console.log(`    -> ${path.join(baseRoot, g.dir, SKILL_NAME)}`);
    }
    console.log(`\nFiles per install (${payload.referenceFiles.length + 1} total):`);
    console.log("  - SKILL.md");
    for (const f of payload.referenceFiles) console.log(`  - references/${f}`);
    return;
  }

  // Pre-flight: refuse to partially install if any destination already
  // exists and --force wasn't passed.
  const destinations = groups.map((g) => ({
    g,
    dest: path.join(baseRoot, g.dir, SKILL_NAME),
  }));
  const blocked = destinations.filter((d) => fs.existsSync(d.dest) && !force);
  if (blocked.length > 0) {
    console.error("The following destinations already exist. Re-run with --force to overwrite:");
    for (const b of blocked) console.error(`  - ${b.dest} (${b.g.labels.join(" / ")})`);
    process.exit(1);
  }

  console.log(`Installing "${SKILL_NAME}" (${langLabel}) under ${baseRoot}:\n`);
  for (const { g, dest } of destinations) {
    const result = writeSkill(dest, payload, force);
    if (result.ok) {
      console.log(`  [ok] ${g.labels.join(" / ")} -> ${dest}`);
    } else {
      console.log(`  [skip] ${g.labels.join(" / ")} -> ${dest} (${result.reason})`);
    }
  }

  console.log(`\n${payload.referenceFiles.length + 1} files installed per agent.`);
  console.log("The agent(s) will pick this up automatically on next run.");
  console.log(
    "\nNote: Hermes Agent and some emerging tools don't have a confirmed dedicated skills " +
      "directory as of this writing — this script places their copy at the tool-agnostic " +
      "`.agents/skills/` location. See README 'Agent Compatibility Notes' if that doesn't work."
  );
}

main();
