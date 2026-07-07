# Changelog

All notable changes to SH-KoTH are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.5.0-zh_tw.1] — 2026-07-07

This release addresses all P0 items from the upstream PR #4 review and fixes a
team-mode capture bug found during in-game verification. All changes below were
verified end-to-end on a clean Paper 1.21.1 server.

### Changed

- **`AnvilTextInput` rewritten on top of [AnvilGUI](https://github.com/WesJD/AnvilGUI) 1.10.10**
  (shaded and relocated to `dev.smartshub.shkoth.libs.anvilgui`). The previous
  hand-rolled `Bukkit.createInventory(ANVIL)` approach produced an anvil with no menu
  backend: rename text did not apply, the input item could be dragged out, and closing
  did not return to the caller. AnvilGUI opens a genuine anvil menu, cancels all item
  movement, and confirm/cancel both reliably re-open the calling editor on the next tick.

- **Language policy applied project-wide**: developer-facing text (comments, Javadoc,
  log and exception messages) is now English only; player-facing text goes through
  `MessageRepository` with per-language overrides (`en` defaults, `zh_tw` provided).
  Java sources now contain zero non-Latin characters.

- **Internal team command is now config-gated instead of hard-removed.**
  New `internal-team.disable-command` option in `hooks.yml` (default `false`) controls
  whether the built-in `/team` command is registered and whether the editor shows the
  Solo/Deny team toggles. The internal team system itself (services, listeners, events,
  hooks, API) always stays active so other team plugins keep integrating.

### Fixed

- **Editor tabs no longer overlap.** `refreshAll()` only cleared the backing Bukkit
  inventory but not triumph-gui's logical item map, so switching tabs let items from
  the previous tab bleed through. Now `gui.clearItems()` is called before re-registering
  the active tab's content.

- **`solo-koth` config key is honored again.** `KothBuilder` was reading `solo-mode` /
  `create-team-if-not-exists-on-enter`, while the bundled template and the editor's YAML
  writer both produce `solo-koth` / `create-team-if-not-exists`, so the configured value
  was silently ignored. The builder now reads the same keys the template and editor write.

- **Teamless players can capture team KOTHs again.** The teamless-entry branch was gated
  on `deny-entry-if-not-in-team`, so with deny disabled a teamless player could stand in
  a team KOTH without capture ever starting (the tally could not resolve them to a team).
  A team-mode KOTH now auto-assigns an internal team first (when
  `create-team-if-not-exists` allows it) and only then applies the deny rule.

## [1.4.0-zh_tw.1] — 2026-07-01

This release is a large optimization and editor-UX pass. It is split into three logical groups:
backend hardening, GUI editor redesign, and fork-specific changes.

### Added

- **`OfflineRewardStorage`** (`service/reward/OfflineRewardStorage.java`)
  Persists physical rewards for winners who are offline at the moment the KoTH ends.
  Rewards are stored per-UUID in `plugins/SH-Koth/offline-rewards.yml` using rtag Base64
  serialization and are auto-delivered on the next `PlayerJoinEvent`. On `onDisable()`
  the storage is flushed synchronously so nothing is lost on restart.

- **`AnvilTextInput`** (`service/gui/input/AnvilTextInput.java`)
  A self-contained `Listener` wrapping the vanilla anvil inventory as a text input prompt.
  Used by the editor GUI in place of the previous "close window → type in chat → re-open"
  flow. Listens for `PrepareAnvilEvent` to keep the result slot synced with the rename
  text, captures the confirmed value on a click of slot 2, and treats a plain close as
  cancellation. Uses the modern `AnvilView#getRenameText()` / `setRepairCost()` API
  introduced in Paper 1.21.

- **`ScoreboardLineEditorGui`** (`gui/ScoreboardLineEditorGui.java`)
  A paginated 6-row editor for the per-state scoreboard line lists (capturing and waiting).
  Each line gets its own row with edit, move up, move down, and delete buttons; add / clear
  / back live in the header; pagination in the footer. Text editing and new-line creation
  go through `AnvilTextInput`.

- **`KothValidation`** now also checks:
  - KOTH ID character set (`^[A-Za-z0-9_-]+$`)
  - `captureTime` must not exceed `maxTime`

- **`NotifyChannel`** enum (`api/koth/guideline/NotifyChannel.java`)
  Channel-level granularity (`TITLE`, `ACTIONBAR`, `CHAT`, `BOSSBAR`) so that future code
  can ask "is this channel enabled for this KoTH?" instead of branching on `NotifyType`
  enum values. Exposed via `NotifyType#channels()` and `NotifyType#includes(channel)`;
  existing `NotifyType` values are preserved as aliases.

- **`KothYamlSaver#saveToYamlAsync`** returns a `CompletableFuture<Boolean>` for callers
  (such as the editor's save-edits flow) that don't need the file to be on disk before
  control returns. The original synchronous `saveToYaml` is preserved for the
  registration path that immediately re-reads the file.

- **Editor footer actions** in `CreateKothGui`: Test (starts the saved KoTH for a quick
  verification), Copy-from (opens the `EditKothListGui` to mirror another KoTH's
  configuration), Validation summary (live ✓/✗ status with translated error names),
  Save (material/title swap between Create and Edit modes).

- **Per-player edit-tab state**: the tabbed editor remembers which tab a player was
  viewing across re-renders (e.g. after an anvil prompt returns).

### Changed

- **`KothTicker`** now isolates per-KOTH `tick()` exceptions with a try/catch and logs
  the offending KOTH id via the plugin logger. Previously, a single KOTH throwing would
  abort the entire iteration and prevent other running KOTHs from ticking.

- **`RefreshInsideKothService`** has been rewritten to use
  `World#getNearbyEntitiesByType(Player.class, area.center, dx, dy, dz)`. The previous
  implementation iterated all online players per world per second and asked each KOTH if
  the player was inside. The new path scopes the query to the KOTH's bounding box, which
  scales much better when the world has many players but the KOTH areas are small.
  Behaviour is preserved: entry, exit, eligibility-to-stay, and offline cleanup all still
  fire through the existing `Koth#playerEnter/playerLeave/removePlayerDirectly` API.

- **`GrantRewardsService`** delivers rewards individually rather than in batch:
  - One random reward per winner (`ThreadLocalRandom`) drawn from
    `Koth#getPhysicalRewards()`.
  - Online winners receive the item immediately; offline winners hand off to
    `OfflineRewardStorage`.
  - Leftover items that don't fit in the inventory are now dropped with
    `Item#setOwner(player.getUniqueId())` so they cannot be picked up by other players.

- **`CreateKothGui`** has been rewritten as a 6-row, 3-tab editor:
  - Tab bar: Basic / Time & Scoreboard / Rewards & Commands.
  - Footer: Test, Copy-from, Validation summary, Save.
  - Slot positions are named constants (no magic numbers).
  - Item factories `promptItem` / `numericItem` / `toggleItem` cut boilerplate.
  - Edit-mode is detected automatically by looking the temp data's id up in
    `KothRegistry`; on save the existing KOTH is unregistered first so the
    registration service can re-create it.
  - Text fields (id, display name, scoreboard titles) open `AnvilTextInput` with a
    `Consumer<String>` setter and an optional validator that re-opens the anvil with
    an inline error message on bad input.

- **`KothLoreBoardPreview`** now caches the rendered preview lore per player UUID and
  exposes `invalidate(UUID)` for callers that mutate the underlying data. Click handlers
  in the editor and the line editor call `invalidate` before re-rendering.

- **`Bukkit.getLogger()`** has been replaced with
  `JavaPlugin.getProvidingPlugin(...).getLogger()` across the project (`SchemaCreator`,
  `MessageParser`, `PhysicalRewardsMapper`, reward services, refresh service). The
  vendored bStats `Metrics.java` is intentionally left as-is.

### Fixed

- A single KOTH whose `tick()` throws will no longer prevent later KOTHs in the iteration
  from running.
- Reward items dropped to the ground when a winner's inventory is full can no longer be
  picked up by other nearby players.
- The previous editor had no guard against `captureTime > maxTime` or against duplicate
  KOTH ids on creation; both are now blocked at validation time and surfaced in the
  editor footer.

### Removed

- Nothing in this changelog. (The fork branch additionally disables the team mechanism
  at the GUI/command surface; that change is intentionally not described here because
  it is fork-specific and should be reworked as an opt-in config toggle before any
  upstream PR.)

### Notes for upstream PR reviewers

The fork this changelog ships in additionally:

1. Localises every user-facing string to Traditional Chinese. The new code added in this
   release follows the same pattern (hardcoded zh-TW strings) and would need to be moved
   behind a `MessageRepository` lookup before it lands upstream. Suggested approach:
   add message keys for all new GUI text, ship English defaults, and let downstream
   forks override via language files.
2. Disables the team mechanism by:
   - Not registering `TeamCommand`
   - Forcing `isSolo = true` in `KothBuilder`
   - Hiding the three team-related toggles in the editor GUI
   The underlying API, events, hooks, and services are intact, so re-enabling is a
   one-line change. Before a PR this should become a config toggle (e.g.
   `disable-team-mechanic: false` in `config.yml`) instead of a hard-coded behaviour
   change.

### Migration

- No YAML schema changes. KoTH config files written by previous versions load unchanged.
- `offline-rewards.yml` is created on first need; nothing to do for existing servers.
- `Koth#isBossbarEnabled()` now derives from both the explicit boss-bar flag and the
  notify-type's channels, so behaviour is unchanged in the default `ALL` case.

### Files

**New:**
- `api/src/main/java/dev/smartshub/shkoth/api/koth/guideline/NotifyChannel.java`
- `plugin/src/main/java/dev/smartshub/shkoth/gui/ScoreboardLineEditorGui.java`
- `plugin/src/main/java/dev/smartshub/shkoth/service/gui/input/AnvilTextInput.java`
- `plugin/src/main/java/dev/smartshub/shkoth/service/reward/OfflineRewardStorage.java`

**Notable rewrites:**
- `plugin/src/main/java/dev/smartshub/shkoth/gui/CreateKothGui.java`
- `plugin/src/main/java/dev/smartshub/shkoth/service/koth/RefreshInsideKothService.java`
- `plugin/src/main/java/dev/smartshub/shkoth/service/reward/GrantRewardsService.java`
- `plugin/src/main/java/dev/smartshub/shkoth/service/gui/menu/other/KothLoreBoardPreview.java`

---

## Prior history

For changes before this release see the upstream
[Smarts-Hub/SH-Koth commit history](https://github.com/Smarts-Hub/SH-Koth/commits/master).
