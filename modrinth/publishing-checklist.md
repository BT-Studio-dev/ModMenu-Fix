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

Dependencies: **Mod Menu** — required, version `21.0.0`; **Fabric API** — required (Mod Menu
needs it). Nothing optional.

Changelog: paste the release notes from
<https://github.com/BT-Studio-dev/ModMenu-Fix/releases/tag/v1.2.0>.

## Tags

`Tags` → categories: **Utility**, **Client-side** (if offered), and anything about menus/GUI.
Do not tag it as a library.

## Links

`Links` → **Source**: `https://github.com/BT-Studio-dev/ModMenu-Fix` ·
**Issues**: `https://github.com/BT-Studio-dev/ModMenu-Fix/issues` ·
Discord/Wiki: leave empty unless you have one. The description also lists both, which is fine.

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
