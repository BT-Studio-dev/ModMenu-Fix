# Mod Menu Fix

<p align="center">
  <img src="art/banner-mod-menu-fix-1280x640.png" alt="Mod Menu Fix banner" width="100%">
</p>

A small **Fabric client mod for Minecraft 26.3** that fixes how [Mod Menu](https://github.com/TerraformersMC/ModMenu)
handles **missing mods** — parent entries for mods that are not installed.

**Download:** **[Releases](https://github.com/BT-Studio-dev/ModMenu-Fix/releases)** — every release carries the built jar.

* Mod Menu version: **21.0.0** (the Minecraft 26.3 release)
* Loader: Fabric Loader 0.20.1+
* Java: 25
* Environment: client only

## What was wrong

A mod can name a parent in `fabric.mod.json`:

```json
"custom": { "modmenu": { "parent": "some-other-mod" } }
```

That is how Mod Menu groups add-ons under their main mod. When the parent is **not installed**,
Mod Menu 21 on Minecraft 26.3 invents a stand-in (`FabricDummyParentMod`) and puts it in
`ModMenu.MODS`, where it is treated like an installed mod:

| Symptom | Cause in Mod Menu 21 |
| --- | --- |
| A phantom row with no version, no description and a placeholder icon appears in the mod list | the dummy parent is added to `MODS` and rendered as a normal parent entry |
| The title screen says "N mods loaded" but the Mods screen lists fewer | `getDisplayedModCount()` counts the dummy parent |
| The "Showing N mods" counter ignores the add-ons of a missing parent | dummy parents are never added to `ROOT_MODS` |
| Adding the missing mod id to `hidden_mods` also hides the add-ons that *are* installed | children are only listed underneath their parent |
| `NullPointerException` from the update checker | `FabricMod#setUpdateInfo` does `ModMenu.MODS.get(parent).setChildHasUpdate()` with no null check |

In short: a mod that does not exist is shown and counted, while the mods that do exist can be
hidden or miscounted.

## What this mod does

1. **Hides missing parents.** After Mod Menu builds its mod maps, every non-real (dummy) parent
   entry is removed from `MODS`, so it is no longer listed and no longer counted.
2. **Keeps installed mods visible.** The children of a removed parent are unlinked from
   `PARENT_MAP` and re-rooted in `ROOT_MODS`, so they show up as ordinary top-level mods and are
   included in the "Showing N mods" counter. Hiding a missing mod id can no longer hide them.
3. **Guards the update checker.** The `setChildHasUpdate()` call on a missing parent is skipped,
   with one warning line in the log instead of an exception.
4. **Tells you what is broken.** One log line per missing parent, naming the mod ids involved.

Nothing is bundled: Mod Menu still has to be installed, and this mod only patches it in memory.

## Mods GUI entry: title screen and pause menu

Minecraft 26.3 moved its menu icon rows, so Mod Menu's defaults (`icon` on the title screen,
`icon` in the pause menu) turn the Mods entry into a 20×20 icon instead of the labelled button
people know from 26.2. Both places can be switched back, independently:

**Title screen — `title_screen_button`**

| Value | Title screen |
| --- | --- |
| `classic` *(default)* | full-width **Mods** button in the menu column, under Minecraft Realms — the 26.2 look |
| `icon` | 20×20 Mods icon inside the 26.3 icon row (Mod Menu's own default) |
| `shrink` | half-width Mods button next to Minecraft Realms |
| `replace_realms` | Mods button in place of the Minecraft Realms row |
| `keep` | do not touch Mod Menu's `mods_button_style` at all |

**Pause menu — `game_menu_button`**

| Value | In-game pause menu |
| --- | --- |
| `insert` *(default)* | full-width **Mods** button in the game menu grid |
| `icon` | 20×20 Mods icon in the pause menu's icon row (Mod Menu's own default) |
| `keep` | do not touch Mod Menu's `game_menu_button_style` at all |

Both accept Mod Menu's own aliases (`below_bugs` for `insert`, `replace` / `replace_bugs` for
`icon`), because those are the serialized names older config files use.

This does not reimplement any layout: it selects the style inside Mod Menu's own config right
after Mod Menu has loaded it, before the title screen is built, so Mod Menu places the button
itself. If `modify_title_screen` / `modify_game_menu` are off in `config/modmenu.json`, they are
switched on when a style is requested (otherwise no button would be drawn at all). Picking a
style in Mod Menu's options screen afterwards simply overwrites this.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) 0.20.1+ for Minecraft 26.3.
2. Install Mod Menu 21.0.0 (and Fabric API, which Mod Menu needs).
3. Grab `modmenufix-1.2.0.jar` from [Releases](https://github.com/BT-Studio-dev/ModMenu-Fix/releases)
   (or `dist/` in the repo) and drop it into `.minecraft/mods`.
4. Launch the game. The log line `Mod Menu Fix 1.2.0 for Minecraft 26.3 ready` confirms it is active.

## Configuration

`config/modmenufix.json` is created on first launch:

```json
{
  "title_screen_button": "classic",
  "game_menu_button": "insert",
  "hide_missing_parent_mods": true,
  "count_orphaned_children_as_root_mods": true,
  "log_missing_parents": true,
  "guard_update_checker_against_missing_parents": true
}
```

| Key | Default | Meaning |
| --- | --- | --- |
| `title_screen_button` | `"classic"` | `classic` / `icon` / `shrink` / `replace_realms` / `keep` — see above. |
| `game_menu_button` | `"insert"` | `insert` / `icon` / `keep` — the pause menu entry, see above. |
| `hide_missing_parent_mods` | `true` | Remove phantom parent entries for mods that are not installed. `false` restores stock Mod Menu behaviour. |
| `count_orphaned_children_as_root_mods` | `true` | Count the surviving children of a missing parent as top-level mods. |
| `log_missing_parents` | `true` | Log one line per missing parent. |
| `guard_update_checker_against_missing_parents` | `true` | Null-guard the update checker. `false` reproduces the original crash. |

## Building

The project uses the non-remapping Loom plugin, because Minecraft 26.x ships unobfuscated
(no mappings, no `mod*` dependency configurations).

```bash
./gradlew build
# -> build/libs/modmenufix-1.2.0.jar
```

The built jar is committed to `dist/modmenufix-1.2.0.jar` so it can be downloaded without
running a build.

`v*` tags are published by `.github/workflows/release.yml`, which refuses to publish when the
tag does not match `mod_version`, builds and tests the mod, checks the jar contents, and then
creates the [release](https://github.com/BT-Studio-dev/ModMenu-Fix/releases) with the jar attached.
It can also be started by hand (`workflow_dispatch`) with the tag to publish.

CI (`.github/workflows/build.yml`) runs on every push and:

1. builds the mod and runs the unit tests in `src/test/java` (the real map surgery against
   dummy `Mod` instances),
2. checks with `javap` that everything it patches still exists in Mod Menu 21.0.0
   (`ModMenu#onInitializeClient`, the `setChildHasUpdate()` call inside
   `FabricMod#setUpdateInfo`, `ModMenuConfig#MODS_BUTTON_STYLE`, `GAME_MENU_BUTTON_STYLE`,
   `MODIFY_TITLE_SCREEN`, `MODIFY_GAME_MENU`, `TitleMenuButtonStyle#CLASSIC`,
   `GameMenuButtonStyle#INSERT` and the option `setValue` methods),
3. publishes the jar to `dist/`.

## Layout

```
src/main/java/dev/btstudio/modmenufix/
├── ModMenuFix.java        client entrypoint, logs the config in use
├── FixConfig.java         config/modmenufix.json
├── MissingParents.java    the fix: strip phantom parents, re-root their children
├── MenuButtons.java       title screen + pause menu Mods entry (classic / insert / icon / keep)
└── mixin/
    ├── ModMenuMixin.java  runs the fix at the tail of ModMenu#onInitializeClient
    └── FabricModMixin.java guards setUpdateInfo against a missing parent
```

## Store listing

Copy that is ready to paste into Modrinth / CurseForge lives in `modrinth/`:

* `modrinth/summary.txt` — the 250-character summary field
* `modrinth/description.md` — the full project description (markdown)

## Artwork

* `art/icon-1024.png` — master icon, transparent rounded square
* `art/icon-512.png` — store listing / upload size (Modrinth, CurseForge, GitHub)
* `src/main/resources/assets/modmenufix/icon.png` — 128×128 icon shipped inside the jar
* `art/banner-mod-menu-fix.png` — full resolution banner (1472×704), in the Mod Menu house style
* `art/banner-mod-menu-fix-1280x640.png` — social preview / README size

The icon follows the Mod Menu artwork: blue rounded tile, list rows, one dashed ghost row
struck out (the missing mod that gets removed), and a wrench badge for the fix.

## License

MIT. Mod Menu is MIT and © its authors; this project only injects into it.
