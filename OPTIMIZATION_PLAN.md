# SH-Koth 繁中 Fork 優化計畫

> 目標：**讓上游 PR [#4](https://github.com/Smarts-Hub/SH-Koth/pull/4) 從 `CHANGES_REQUESTED` 走到 merge**，同時把 fork 的品質與可維護性一起拉起來。
>
> 定位：回 PR 給上游（需保持相容、可設定、程式面向國際貢獻者）。
> 現況：`master` 領先 `upstream/master` 13 個 commit；reviewer `yiyoperez` 已提出 7 條 inline 意見。

---

## 優先級總覽

| 級別 | 內容 | 為什麼 |
|------|------|--------|
| **P0** | 解除 PR #4 的 5 大阻擋項 | 沒過就 merge 不了，這是硬需求 |
| **P1** | 穩定性 / 不掉東西 | 獎勵掉失、錯誤吞掉是實際玩家傷害 |
| **P2** | 精簡體積與依賴 | 啟動速度、jar 大小、維護面 |
| **P3** | 效能熱路徑 | 目前相對健康，觀察為主 |
| **X** | Fork 同步策略 | 讓後續跟上游不再痛苦 |

---

## P0 — 解除 PR #4 阻擋（對應上游逐行意見）

### P0-1　修編輯器分頁重疊 bug　【功能阻擋】
- **上游意見**：`CreateKothGui` / `KothCommand:53` — 「tab items overlap slots and keep showing in other tabs」。
- **根因**：`refreshAll()`（`CreateKothGui.java:608`）只呼叫 `gui.getInventory().clear()` 清 Bukkit 底層 inventory，**沒有清 triumph-gui `Gui` 物件內部的 GuiItem map**。切分頁時上一個分頁 `setItem` 進去的 GuiItem 仍留在 map，`gui.update()` 又把它們重繪回來；`fillContentBackground()`（`:590`）以 `getGuiItem(slot) == null` 判斷是否填背景，殘留物品讓它跳過 → 舊分頁物品「透」到新分頁。
- **修法**：切分頁前，把 **CONTENT 區每個 slot 從 triumph `Gui` 真正移除**（逐 slot `gui.removeItem(slot)` 或重建 Gui），而不是只 clear 底層 inventory。改完務必手動走一輪「Basic → Time → Reward → Basic」確認無殘留。
- **驗收**：切換三個分頁各兩輪，內容區只出現當前分頁的物品 + 灰玻璃背景。

### P0-2　修 AnvilTextInput　【功能阻擋】
- **上游意見**：rename 沒作用（即使 listener 有註冊）、物品可被拿走留在背包、關閉沒返回建立/編輯器。疑似 WIP。
- **根因**：`Bukkit.createInventory(player, InventoryType.ANVIL)`（`AnvilTextInput.java:56`）建立的是**沒有鐵砧選單後端的假 anvil** → 沒有 vanilla 的 rename/repair-cost 處理，`PrepareAnvilEvent` 行為不可靠；且**缺 `InventoryDragEvent` 取消** → 玩家可拖曳搬走 slot 0 的紙；confirm/cancel 回編輯器靠 `runTask` 但因假 anvil 的關閉時序不穩。
- **修法（擇一）**：
  - **建議**：改用成熟的真鐵砧輸入函式庫 `net.wesjd:anvilgui`（relocate 進 shadow），把「輸入字串 → `Consumer<String>`」交給它，移除手刻的假 anvil 流程。
  - 或：改用 Paper 原生真鐵砧 container（開真的鐵砧選單），並補上 `InventoryDragEvent`／hotbar-swap／shift-click 的全面取消。
  - 兩種都要保證：confirm 與 cancel **都可靠地重開編輯器**（用 `Player#openInventory` 於下一 tick）。
- **驗收**：輸入字串能套用；任何點擊/拖曳/數字鍵都無法把物品拿進背包；確認與取消都會回到 `CreateKothGui`。

> P0-1、P0-2 是 reviewer 實測時卡住的點——**先修這兩個**，reviewer 才能繼續往下測。

### P0-3　程式語言政策：註解與 Log 全面英文化
- **上游意見**：`NotifyChannel:7`（類別/方法註解要英文）、`GrantRewardsService:45`（log 要英文）。
- **範圍**（我掃到的中文 log／註解，全部要轉英文）：
  - `KothTicker.java:24`、`RefreshInsideKothService.java:40`
  - `GrantRewardsService.java:45,63,78`
  - `OfflineRewardStorage.java:92,124`（含類別 Javadoc）
  - `AnvilTextInput.java:29-36 Javadoc、121、140`
  - `KothYamlSaver.java:45,51,64,68`
  - `NotifyChannel.java` 及其他新增 API 類別的 Javadoc
- **原則**：**面向開發者的文字（註解、log、例外訊息）= 英文**；**面向玩家的文字 = 走 MessageRepository（見 P0-5）**。

### P0-4　team 機制改 config 開關，不要硬砍
- **上游意見**：`SHKoth:250` — 插件用內部 team 系統，停用應是「config 選項停用指令」，保留內部系統避免與其他 team 插件 hook 衝突。
- **現況硬編碼**：`KothBuilder.java:51` `final boolean isSolo = true;`；`TeamCommand` 不註冊；編輯器隱藏 3 個 team 開關。
- **修法**：
  - 新增 config（例如 `config.yml` → `team.disable-command: false`）。
  - 移除 `isSolo` 寫死，恢復由 KOTH 設定決定；只在 config 開啟時**不註冊 `TeamCommand`**、隱藏編輯器 team 開關。
  - 保留 `ContextualTeamTracker`、team hooks、events、services 原封不動。
- **驗收**：預設（config = false）行為與上游一致；設 true 時只是指令消失、內部系統仍運作。

### P0-5　玩家可見字串一律走 MessageRepository + 可設定語言
- **上游意見**：`AddPhysicalRewardGui:1` — 開發期用母語可理解，但**要 merge 必須可設定**。
- **修法**：
  - 修 `OfflineRewardStorage.java:152` 寫死英文 `"You received ... reward(s)!"` → 加 message key，英文為預設，zh_tw 覆蓋。（注意此類別在 services 接線前建構，需確認取用 MessageRepository 的時機，或延後到 `giveTo` 時查。）
  - 新增 GUI（`AddPhysicalRewardGui`、`ScoreboardLineEditorGui`、`CreateKothGui` 內新字串）把硬編 zh-TW 抽成 message keys，**英文預設 + zh_tw 語言檔覆蓋**。
- **驗收**：把語言切回 `en` 時，全流程沒有任何殘留中文；`grep '[一-龥]' plugin/src/main/java` 只剩……理想上為 0（log 已於 P0-3 英文化）。

---

## P1 — 穩定性 / 不掉東西

### P1-1　補上測試（目前 154 檔 0 測試 = 最大結構風險）
優先為「錯了會掉東西」的純邏輯路徑加單元測試：
- `KothValidation`（id 字元集、captureTime ≤ maxTime、重複 id）
- `OfflineRewardStorage` 序列化 round-trip（rtag Base64 存→讀一致）
- `GrantRewardsService` 獎勵抽取/離線 fallback 分支
- `NotifyType#channels()` / `includes()` 對應關係
- 建議引入 JUnit 5 + MockBukkit（Paper 相容），先建最小 test sourceSet。

### P1-2　`printStackTrace()` ×7 → plugin logger（英文）
位置：`ConfigLoader:140`、`KothRegistrationFromTempDataService:86`、`KothScheduler:138,152`、`Configuration:44,57,65`。
改為 `logger.log(Level.SEVERE, "...", e)`，訊息英文。避免噪音吞進 stderr 抓不到。

### P1-3　`OfflineRewardStorage` 寫檔去抖動（debounce）
- **問題**：`scheduleSave()`（`:97`）每次 `add()`／`drain()` 都 `new BukkitRunnable().runTaskAsynchronously`。多名離線贏家同時入帳 → 噴一堆 async task、重複整檔覆寫。
- **修法**：改成單一「dirty flag + 延遲合併寫入」（例如已排程就不重複排、或固定間隔 flush），`onDisable` 仍同步 `flush()`。

---

## P2 — 精簡體積與依賴

> ⚠️ 認知修正：8 個 team 插件 hook（Factions/Towny/BentoBox…）都是 `compileOnly`，**不會進 jar**，所以「砍 team 減體積」不成立。減體積要看 shadowJar 真正打包的 runtime deps。

- **真正進 jar 的依賴**：`h2`、`triumph-gui`、`lamp`、`rtag(+item)`、`cron4j`、`fastboard`、`boosted-yaml`、`jdwebhooks`。
  - **評估 h2 是否必要**：若只存玩家 stats，H2 是相對重的依賴，可評估換 SQLite 或內建輕量儲存（此為較大改動，列為評估項，非必做）。
- **驗證 relocate 樣式**：`build.gradle.kts:52` `relocate("fr.mrmick", …)` 少了 `y`（套件是 `fr.mrmicky.fastboard`）。前綴仍會匹配但重定位名稱會怪，**確認 relocate 是否如預期**，順手改成 `fr.mrmicky`。
- 若 P0-4 讓 team 成為可選，可考慮把 team hooks 整理成清楚的「optional integration」區塊（文件面，不影響體積）。

---

## P3 — 效能熱路徑（目前相對健康，觀察為主）

- ✅ `RefreshInsideKothService` 已改用 `getNearbyEntitiesByType` 範圍查詢（好）。
- ✅ `KothTicker` 已隔離單一 KOTH 例外（好）。
- **觀察項**：scoreboard 刷新頻率（`SendScoreboardService`/`UpdateTask`）、`PlaceholderHelper` 每次查詢成本、`KothLoreBoardPreview` 快取失效正確性。目前無明顯瓶頸，等 P0/P1 完成再視情況 profile。

---

## X — Fork 同步策略

- 目前 `master` 領先 `upstream/master` 13 commit，PR #4 為 feature branch。
- 建議把改動拆成**乾淨、主題分明的 commit**，方便 reviewer 審：
  1. 功能 bug 修復（P0-1、P0-2）
  2. 語言政策（P0-3 英文化 + P0-5 message keys）
  3. team config 化（P0-4）
  4. 穩定性（P1）
- **定期 `git rebase upstream/master`** 保持同步；PR merge 後清掉本地分歧。
- 註：CHANGELOG 已標註 team-disable 與 hardcoded zh-TW 是 fork-specific，方向與上游 review 一致——照本計畫落實即可。

---

## 建議執行順序（對齊 PR merge）

1. **P0-1 + P0-2**（兩個功能 bug）→ 讓 reviewer 能實測通過。
2. **P0-3 + P0-4 + P0-5**（語言／config 政策）→ 滿足 merge 條件。
3. 在 PR #4 回覆 reviewer、請求重審（附上「已逐條對應」清單）。
4. **P1**（測試 + 穩定性）→ 可併入本 PR 或後續 PR。
5. **P2 / P3** → 後續 PR，非阻擋項。

---

## 逐條對應 PR #4 review（給 reviewer 的回覆草稿骨架）

| Reviewer 意見 | 對應計畫 | 狀態 |
|---|---|---|
| NotifyChannel 註解要英文 | P0-3 | ✅ 已英文化（NotifyChannel/NotifyType Javadoc + displayName）|
| 編輯器分頁重疊 (KothCommand/CreateKothGui) | P0-1 | ✅ 已修（`gui.clearItems()`），in-game 驗證通過 |
| GrantRewardsService log 要英文 | P0-3 | ✅ 已英文化（全 repo 掃描：Java source 0 中文，僅 zh_tw 語言檔保留）|
| team 停用改 config 選項 (SHKoth:250) | P0-4 | ✅ 內部系統保留；`internal-team.disable-command` 控制指令註冊＋編輯器 toggle；`KothBuilder` 恢復由 config 決定 |
| AddPhysicalRewardGui 語言要可設定 | P0-5 | ✅ 前次 i18n commit 已抽 message keys（en 預設 + zh_tw 覆蓋）|
| AnvilTextInput 多個 bug | P0-2 | ✅ 已改用 AnvilGUI（1.10.10），in-game 驗證通過 |
| CreateKothGui 分頁沒正確替換 | P0-1 | ✅ 同上 |

> **本輪進度（2026-07-05）**：P0 五項全數落地並通過 `:plugin:shadowJar` 編譯（AnvilGUI 正確 relocate 進 `dev.smartshub.shkoth.libs.anvilgui`）。
> - P0-4 補充：`KothBuilder` 改讀 `solo-koth` / `deny-entry-if-not-in-team` / `create-team-if-not-exists`（template 與編輯器實際寫入的 key，順帶修正上游 builder 讀 `solo-mode` 的既有 key 錯配）。
> - 待辦：全部 P0 需 **in-game 實測**（分頁殘留、anvil 輸入、team 指令開關、solo/team 編輯器 toggle）後再回 PR #4 請求重審。P1（測試、`printStackTrace`×7、離線獎勵 debounce）為後續。

> **本輪進度（2026-07-07）**：全部 P0 已於本地 Paper 1.21.1-133 測試伺服器以 headless bot（mineflayer）完成 in-game 實測，全數通過：
> - P0-1：BASIC → TIME → REWARD → BASIC 全程 slot dump 比對，無舊分頁物品殘留。
> - P0-2：AnvilGUI 開啟真實鐵砧選單；逐鍵 rename 正確寫入、點輸出槽確認後回到編輯器且 lore 顯示新值；pickup/shift-click 皆偷不走輸入紙張；ESC 關閉走取消路徑並返回編輯器。
> - P0-4：`disable-command: false` 時 `/team` 註冊、編輯器 37/39 顯示 Solo/Deny toggle（點擊可切換 Solo↔Team）；`true` 時 `/team` 未註冊、toggle 隱藏、`/koth` 不受影響。
> - Koth.java teamless entry 修正（已 commit）：無隊伍玩家進入 team KOTH → 自動建隊 → 開始佔領 → 勝利結算，端到端通過。
> - 實測時發現的**上游既有問題**（非本 PR 範圍，可另提 issue）：`commands-perform.start/end` 在程式中無任何呼叫點，從未被執行（僅 `to-winners` 會跑）；`to-winners` 的 `%player%` 僅靠 PlaceholderAPI 解析，未裝 PAPI 時原樣輸出，但 template 範例卻使用 `%player%`。
> - 已推進至：回覆 PR #4 review。
