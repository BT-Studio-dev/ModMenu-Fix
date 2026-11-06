First public release, for Minecraft 26.3.

### Fixed

- Parent entries for mods that are **not installed** no longer show up in the mod list as phantom rows with no version, no description and a placeholder icon.
- Those phantoms are no longer counted, so "N mods loaded" on the title screen matches what the Mods screen actually lists.
- Installed add-ons of a missing parent are now visible as top-level mods and are counted there. Hiding a missing mod id can no longer hide the mods you do have.
- `NullPointerException` from the update checker when a mod with a missing parent has an update (`FabricMod#setUpdateInfo`). It is skipped, with one warning line naming the mod id.
- One log line per missing parent lists the affected mod ids, so a broken modpack is diagnosable.

### Added

- `title_screen_button` — defaults to `classic`, the full-width **Mods** button under Minecraft Realms (the 26.2 layout) instead of the 26.3 icon row.
- `game_menu_button` — defaults to `insert`, the full-width **Mods** button in the pause menu.
- `config/modmenufix.json`, created on first launch, with `keep` / `icon` / `shrink` / `replace_realms` escape hatches that leave Mod Menu's own settings alone.

### Notes

- Requires **Mod Menu 21.0.0**, Fabric Loader 0.20.1+, Java 25 and Fabric API.
- Client-side only; safe to keep in your client folder for multiplayer.
- Nothing is bundled: Mod Menu is a dependency and is patched in memory, never shipped.
