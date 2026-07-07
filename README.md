# SH-KoTH（繁體中文增強版）

> 📄 **本 Fork 為繁中化與功能增強版本** — 想看上游原作者 Smarts Hub 的原始 README，請點 **➡️ [README_author.md](README_author.md)**

[![Modrinth](https://img.shields.io/badge/Modrinth-Download-1bd96a?logo=modrinth&logoColor=white)](https://modrinth.com/plugin/sh-koth)
[![Upstream](https://img.shields.io/badge/Upstream-Smarts--Hub%2FSH--Koth-555?logo=github)](https://github.com/Smarts-Hub/SH-Koth)
[![Documentation](https://img.shields.io/badge/Docs-Available-blue?logo=gitbook)](https://docs.smartshub.dev/sh-koth/intro/introduction/)
[![License](https://img.shields.io/badge/License-CC--BY--NC--SA%204.0-lightgrey)](LICENSE.md)

---

## 📖 關於本 Fork

**SH-Koth** 是一款免費開源的 Minecraft King of the Hill（搶山頭／佔領據點）插件。本 Fork 在原作的基礎上做了三件事：

1. **完整繁體中文化** — UI、訊息、計分板、指令說明全面 zh_tw 化
2. **後端強化** — 修掉效能瓶頸、加入離線獎勵補發、強化錯誤隔離
3. **編輯體驗大改** — `/koth editor` 採用分頁式 GUI，含驗證提示、防呆、一鍵測試

> 想要原作完整介紹（社群、技術債、貢獻指引等），請看 **[README_author.md](README_author.md)**。

---

## ✨ 本 Fork 的主要增強

### 🌐 本地化系統
- 內建 `zh_tw` 語言包，所有玩家可見訊息都已翻譯
- 透過 `plugins/SH-Koth/configuration/hooks.yml` 中的 `language: zh_tw` 切換

### 🛠️ 後端與穩定性（2026-07 大型優化）
- `KothTicker` 每個 KOTH 的 tick 異常獨立隔離，單一活動爆掉不會波及其他
- `RefreshInsideKothService` 改用 `world.getNearbyEntitiesByType`，玩家多的伺服器負擔大幅降低
- **離線獎勵儲存系統**：贏家不在線會把獎勵存進 `offline-rewards.yml`，下次上線自動補發
- 獎勵掉地會綁定 `Item#setOwner`，避免被路過玩家撿走
- `Bukkit.getLogger` 全面汰換為插件 logger，log 帶 `[SH-Koth]` 前綴
- `KothYamlSaver` 新增 async 版本，編輯流程不卡主線程

### 🎨 重新設計的 `/koth editor` GUI
- **6 列 × 3 分頁版面**：基本資料 ／ 時間與計分板 ／ 獎勵與指令
- **Anvil 文字輸入**：ID、顯示名稱、計分板標題全部改用 Bukkit 原生 Anvil GUI 輸入，告別「關閉視窗→去聊天欄打字→再開」的流程；輸入當下即時驗證（規則錯誤會帶著錯誤訊息再開一次 Anvil）
- **計分板逐行編輯器**：點計分板內容開啟分頁列表，每行可獨立編輯（Anvil）／上移／下移／刪除，新增也是 Anvil
- **即時驗證提示**：每個欄位 lore 顯示 ✓/✗ 狀態，缺項列在儲存按鈕 lore
- **防呆機制**：ID 字元規則檢查、`捕獲時間 ≤ 最長時間`、新建時不能撞已存在 ID
- **新建/編輯模式區分**：儲存按鈕材質與文字會自動切換（Slime Ball 建立 / Anvil 儲存變更）
- **複製其他 KOTH 設定**：一鍵從現有 KOTH 帶入欄位
- **一鍵測試**：直接啟動該 KOTH 一回合驗證設定
- 程式內 slot 編號全部具名常數化、共用 `numericItem` / `toggleItem` / `promptItem` 工廠

### 🚫 已停用的功能
- **組隊機制**：本 Fork 預設停用 `/koth team` 指令與相關 GUI toggle，KOTH 一律 solo。底層 API 保留，需要時可恢復。

---

## 🚀 快速開始

### 安裝
1. 從 [Releases](https://github.com/EvansGoethe/SH-Koth-zh_tw/releases) 下載最新 `SH-Koth.jar`（或自行編譯，見下節）
2. 丟進 `plugins/` 重啟一次伺服器讓設定生成
3. 編輯 `plugins/SH-Koth/configuration/hooks.yml`：
   ```yaml
   language: zh_tw
   ```
4. 執行 `/koth reload`，完成

### 建立第一個 KOTH
1. 遊戲內輸入 `/koth editor`
2. 點擊「建立新 KOTH」
3. 在分頁式 GUI 填寫：基本資料 → 時間與計分板 → 獎勵與指令
4. 看到驗證按鈕變綠 → 點儲存

### 編譯
需要 **JDK 21**：
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
.\gradlew.bat shadowJar
```
產出在 `plugin/build/libs/SH-Koth-*.jar`。

---

## 🔗 相關連結

| 用途 | 連結 |
|---|---|
| 上游原作 | [Smarts-Hub/SH-Koth](https://github.com/Smarts-Hub/SH-Koth) |
| 原作者 README | [**README_author.md**](README_author.md) |
| 官方文件 | [docs.smartshub.dev/sh-koth](https://docs.smartshub.dev/sh-koth/intro/introduction/) |
| 本 Fork Issues | [EvansGoethe/SH-Koth-zh_tw/issues](https://github.com/EvansGoethe/SH-Koth-zh_tw/issues) |
| Modrinth 下載 | [modrinth.com/plugin/sh-koth](https://modrinth.com/plugin/sh-koth) |

---

## 📜 授權條款

繼承上游採用 **[CC-BY-NC-SA 4.0](LICENSE.md)**。
非商業用途可自由分享與修改，須以相同條款釋出並標註原作者 **Smarts Hub**。

## 🙌 致謝

- **原作者**：[Smarts Hub](https://github.com/Smarts-Hub) — 提供 SH-Koth 核心架構
- **繁中化與功能增強**：[EvansGoethe](https://github.com/EvansGoethe)
- 感謝所有貢獻者、Minecraft 社群、以及 LSMP 伺服器玩家的回饋
