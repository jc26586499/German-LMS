#German Learning Management System (LMS)
德文單字學習管理系統

A professional Java Swing desktop application designed to gamify German learning through habit tracking, visual memory association, and a virtual economy.
這是一款專業的 Java Swing 桌面應用程式，結合習慣追蹤、圖像記憶法與虛擬點數經濟，讓德文學習更具遊戲化趣味與動力。

🎬 Live Demo


🚀 Core Features
1. Learning & Quiz Engine
Thematic Categorization (單字分類系統): 單字依主題分類（例如：水果 Fruits、國家 Countries 等），方便使用者有系統地擴充詞彙。

Image-Association Memory (圖像記憶法): 採用「圖像路徑法」，將圖片路徑儲存在數據庫中，並透過 JLabel 動態顯示，強化大腦對單字的長效記憶。

Hint System (智慧提示): 測驗中遇到困難時，可點選「Hint」按鈕彈出該單字對應的圖片提示。

Error Tracking (錯題管理): 系統自動統計錯誤超過 3 次的單字，幫助使用者針對弱點進行複習。

2. Habit Tracker (Daily Check-in)
Visual Calendar (打卡月曆): 自定義繪製的日曆組件，會以綠色標記已打卡的日期，視覺化呈現學習進度。

Streak Logic (連勝紀錄): 自動計算連續學習天數（Streak），透過天數累積提升使用者的學習成就感。

Milestone Rewards (里程碑獎勵):

Day 3: +15 Points 🎉

Day 20: +50 Points🎉

Full Month: +100 Points 🏆

3. Virtual Economy (Mock Economy)
Point-Based Unlocking (點數解鎖機制): 使用者透過每日打卡賺取點數，用以解鎖進階單字主題包。

Tiered Access (權限管理): 基礎類別（如：水果）開放免費練習，進階類別（如：國家、氣候）需消耗點數購買。

Top-up Simulation (模擬加值): 提供自主加值功能（例如：儲值 $500 獲得 500 點），包含完整的數值驗證與餘額即時更新。

4. Admin & Data Export
Vocabulary Management: 管理員可透過表格介面即時維護單字庫，包含新增、修改單字與圖片路徑。

Data Export (Excel 匯出): 支援將後台單字表與使用者點數交易紀錄匯出為 Excel 檔案，方便後續數據分析。

🗄️ Database Schema
users: 儲存帳號、密碼、權限角色及點數餘額。

words: 儲存單字、中文意思、分類等級及圖片檔案路徑。

study_logs: 紀錄每日打卡時間，作為日曆與連勝邏輯的數據源。

transactions: 詳盡紀錄每筆點數變動（包含加值、打卡獎勵、商店購買）。

🛠️ Technical Stack
Language: Java11 (Swing GUI)

Database: MySQL8.0.44

Layout Management: CardLayout（面板切換）與 JSplitPane（同步對齊佈局）。

Key Library: JDBC (數據庫連接)、Excel 匯出組件。

Layout Management: CardLayout（面板切換）與 JSplitPane（同步對齊佈局）。

Key Library: JDBC (數據庫連接)、Excel 匯出組件。
