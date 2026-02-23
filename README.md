# 🇩🇪 German Learning Management System (LMS)
### **德文單字學習管理系統**

> **A professional Java Swing desktop application designed to gamify German learning through habit tracking, visual memory association, and a virtual economy."**
> **這是一款Java Swing 桌面應用程式，結合習慣追蹤、圖像記憶法與虛擬點數經濟，讓德文學習更具遊戲化趣味與動力。**

---

## 🎬 Live Demo | 實機演示
### Admin
<p align="center">
  <video src="https://github.com/user-attachments/assets/e2c78cb4-d9a7-435d-bc02-b37acc66b53b" width="850" autoplay loop muted controls></video>
</p>

### Users
<p align="center">
  <video src="https://github.com/user-attachments/assets/18499188-14be-4baf-b734-d7b1df2fd810" width="850" autoplay loop muted controls></video>
</p>


---

## 🚀 Core Features

### 📝 Learning & Quiz Engine

*  **Thematic Categorization (單字分類系統)**：依主題（如：水果 `Fruits`、國家 `Countries`）分類，有系統地擴充詞彙。
*  **Image-Association Memory (圖像記憶法)**：採用「圖像路徑法」，透過 `JLabel` 動態顯示圖片，強化長效記憶。
*  **Hint System (智慧提示)**：測驗中遇到困難時，點選「Hint」即可彈出對應的圖片提示。
*  **Error Tracking (錯題管理)**：自動統計錯誤超過 3 次的單字，生成專屬「弱點複習清單」。

### 🗓️ Habit Tracker (Daily Check-in)

*  **Visual Calendar (打卡月曆)**：自定義繪製組件，綠色標記當月進度，學習成果一眼瞬間。
*  **Streak Logic (連勝紀錄)**：自動計算連續打卡天數，透過天數累積提升使用者的學習成就感。
*  **Milestone Rewards (里程碑獎勵)**：
    * **Day 3**：`+15 Points` 🎉
    * **Day 20**：`+50 Points` 🔥
    * **Full Month**：`+100 Points` 🏆

### 💳 Virtual Economy (Mock Economy)

*  **Point-Based Unlocking (點數解鎖)**：透過打卡賺取點數，用以購買並解鎖進階單字主題包。
*  **Tiered Access (權限管理)**：基礎類別（如：水果）免費開放；進階類別（如：國家、氣候）需點數購買。
*  **Top-up Simulation (模擬加值)**：支援自主加值（如：儲值 $500 = 500 點），具備數值驗證與餘額即時更新。

### ⚙️ Admin & Data Export

*  **Vocabulary Management**：管理員可透過表格介面即時維護單字、中文意思及圖片路徑。
*  **Data Export (Excel 匯出)**：支援將單字庫與交易紀錄匯出為 Excel，方便數據分析與離線備份。

---

## 🗄️ Database Schema

*  **users**：儲存帳號、角色、權限及點數餘額。
*  **words**：儲存單字、中文意思、分類等級及圖片路徑。
*  **study_logs**：紀錄每日打卡時間，作為日曆與連勝邏輯的數據源。
*  **transactions**：詳盡紀錄每筆點數變動（獎勵、加值、消費）。

---

## 🛠️ Technical Stack

* **Language**：`Java 11 (Swing GUI)`
* **Database**：`MySQL 8.0.44`
* **Layout Management**：`CardLayout`（面板切換）與 `JSplitPane`（同步對齊佈局）。
* **Key Components**：`JDBC` (Database Connection)、`Apache POI` (Excel Export).




---
