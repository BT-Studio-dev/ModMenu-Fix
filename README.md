# Mod Menu Fix

<p align="center">
  <img src="art/banner-mod-menu-fix-1280x640.png" alt="Mod Menu Fix banner" width="100%">
</p>

A small **Fabric client mod for Minecraft 26.3** that fixes how [Mod Menu](https://github.com/TerraformersMC/ModMenu)
handles **missing mods** — parent entries for mods that are not installed.

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

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) 0.20.1+ for Minecraft 26.3.
2. Install Mod Menu 21.0.0 (and Fabric API, which Mod Menu needs).
3. Drop `modmenufix-1.0.0.jar` into `.minecraft/mods`.
4. Launch the game. The log line `Mod Menu Fix 1.0.0 for Minecraft 26.3 ready` confirms it is active.

## Configuration

`config/modmenufix.json` is created on first launch:

```json
{
  "hide_missing_parent_mods": true,
  "count_orphaned_children_as_root_mods": true,
  "log_missing_parents": true,
  "guard_update_checker_against_missing_parents": true
}
```

| Key | Default | Meaning |
| --- | --- | --- |
| `hide_missing_parent_mods` | `true` | Remove phantom parent entries for mods that are not installed. `false` restores stock Mod Menu behaviour. |
| `count_orphaned_children_as_root_mods` | `true` | Count the surviving children of a missing parent as top-level mods. |
| `log_missing_parents` | `true` | Log one line per missing parent. |
| `guard_update_checker_against_missing_parents` | `true` | Null-guard the update checker. `false` reproduces the original crash. |

## Building

The project uses the non-remapping Loom plugin, because Minecraft 26.x ships unobfuscated
(no mappings, no `mod*` dependency configurations).

```bash
./gradlew build
# -> build/libs/modmenufix-1.0.0.jar
```

The built jar is committed to `dist/modmenufix-1.0.0.jar` so it can be downloaded without
running a build.

CI (`.github/workflows/build.yml`) runs on every push and:

1. builds the mod and runs the unit tests in `src/test/java` (the real map surgery against
   dummy `Mod` instances),
2. checks with `javap` that both injection points still exist in Mod Menu 21.0.0
   (`ModMenu#onInitializeClient`, and the `setChildHasUpdate()` call inside
   `FabricMod#setUpdateInfo`),
3. publishes the jar to `dist/`.

## Layout

```
src/main/java/dev/btstudio/modmenufix/
├── ModMenuFix.java        client entrypoint, logs the config in use
├── FixConfig.java         config/modmenufix.json
├── MissingParents.java    the fix: strip phantom parents, re-root their children
└── mixin/
    ├── ModMenuMixin.java  runs the fix at the tail of ModMenu#onInitializeClient
    └── FabricModMixin.java guards setUpdateInfo against a missing parent
```

## Artwork

* `art/banner-mod-menu-fix.png` — full resolution banner (1472×704), in the Mod Menu house style
* `art/banner-mod-menu-fix-1280x640.png` — social preview / README size
* `src/main/resources/assets/modmenufix/icon.png` — the 128×128 mod icon

## License

MIT. Mod Menu is MIT and © its authors; this project only injects into it.
