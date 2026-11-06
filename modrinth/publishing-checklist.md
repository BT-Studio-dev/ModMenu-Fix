# Modrinth publishing checklist

What to click, in order, for `modmenufix`. Settings tabs are the ones in the project sidebar.

## License

`License` → search box → **MIT License** (`MIT` SPDX id).

The repo now ships a matching `LICENSE` (MIT, © 2026 BT Studio) and
`fabric.mod.json` already declares `"license": "MIT"`. All three must agree, or review
will bounce the project.

Custom license URL: leave empty — MIT is in Modrinth's list, so no URL is needed.

## Versions

`Versions` → **Create version**:

| Field | Value |
| --- | --- |
| Name | `1.2.0 for Minecraft 26.3` |
| Version number | `1.2.0` |
| Release channel | `Release` |
| Game versions | `26.3` only |
| Loaders | `Fabric` |
| File | `dist/modmenufix-1.2.0.jar` (27,134 bytes, SHA-256 `a3d93e4e…0cfac949`) |

**Dependencies** (the wizard's Details step, `+ Add dependency`) — add exactly two:

1. **Mod Menu** · version `21.0.0` · type **Required** — the jar's own metadata says
   `modmenu >=21.0.0 <22.0.0`, because the mixins patch classes that exist in 21.x.
2. **Fabric API** · any version · type **Required** — not a direct dependency of this mod, but
   Mod Menu 21.0.0 declares `fabric-resource-loader-v1`, `fabric-screen-api-v1`,
   `fabric-key-mapping-api-v1` and `fabric-lifecycle-events-v1`, so nothing works without it.

Nothing optional, nothing embedded. Minecraft, the loader and Java are not added here — they
come from the game version and loader fields.

**Environment step:** choose **Client-side only** — "All functionality is done client-side and
is compatible with vanilla servers." Do not pick "Client and server": the jar declares
`"environment": "client"`, its entrypoint is a `ClientModInitializer`, and every mixin is in the
`client` section of `modmenufix.mixins.json`, so Fabric never loads it on a dedicated server.
"Ignore missing"/optional-server options do not apply.

CI enforces this: the `Verify client-only environment` step fails the build if `environment`,
the entrypoints, or the mixin sections ever stop being client-only, so the jar can't drift away
from the environment answer given here.

Version type `Release`, version number `1.2.0`. Subtitle: `1.2.0 for Minecraft 26.3` (the
wizard pre-fills `Mod Menu Fix 1.2.0`, which duplicates the project name).

Changelog: `modrinth/changelog-1.2.0.md` — one file per version, kept next to this checklist so
the Modrinth text and the GitHub release notes match.

## Tags

`Tags` → Categories: **Utility** only. Nothing else on that list fits, and a wrong tag is a
review finding:

| Category | Why not |
| --- | --- |
| Library | it is not a library — nothing depends on it |
| Optimization | no performance claims, it only changes what Mod Menu displays |
| Management | that is server/admin tooling, not a client GUI |
| Game Mechanics, Decoration, Equipment, … | gameplay content, which this has none of |

Featured tags: **Utility** (1/3). The counter is a maximum, not a quota — with one category
selected, one featured tag is all that can be chosen and all that is needed.

Client-side is not a tag; it comes from the version's Environment answer.

## Links

`Links` tab — two fields, both verified to return HTTP 200:

| Field | Value |
| --- | --- |
| Issue tracker | `https://github.com/BT-Studio-dev/ModMenu-Fix/issues` |
| Source code | `https://github.com/BT-Studio-dev/ModMenu-Fix` |
| Wiki page | leave empty — there is no wiki |
| Discord invite | leave empty — Modrinth only accepts invites that never expire, and there is no server for this project |

Donation links: leave empty.

**Before submitting, `main` must have the code.** Right now the arrow points at a branch: `main`
still contains a one-line README and no `src/`, `LICENSE`, `.github/` or `dist/`. A reviewer who
follows the source link will see an empty project, which is a rejection reason. Merge the pull
request first (PR #1), then check that `github.com/BT-Studio-dev/ModMenu-Fix` shows README,
LICENSE, src/, and the v1.2.0 release.

## Disclosures

Answer honestly, all of these are **no** for this project:

- no network requests (the mod never opens a connection — only Mod Menu does, for its own update checker);
- no data collection or telemetry;
- no external binaries or tools launched;
- no world/download behaviour;
- jar contains only compiled classes from this repo, plus an icon made for it — nothing Mojang-owned, nothing from Mod Menu.

## Description / Summary / Gallery

- **Summary**: `modrinth/summary.txt` (250 characters, fits the 256 limit)
- **Description**: `modrinth/description.md`, pure Markdown, no HTML
- **Gallery**: `art/banner-mod-menu-fix-1280x640.png` first (featured), then any screenshots
- **Icon**: `art/icon-512.png` (already uploaded)

## Before you hit submit

- [ ] Project is set to the version you actually want reviewed (`1.2.0`)
- [ ] `dist/modmenufix-1.2.0.jar` is the file uploaded to the version, not a local rebuild
- [ ] License, description and dependency versions match `fabric.mod.json`
- [ ] No external download links in the description (review treats those as a red flag)
