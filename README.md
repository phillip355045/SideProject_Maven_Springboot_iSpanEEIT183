# SideProject_Maven_Springboot_iSpanEEIT183\

專案名稱
Polarstar Nexus / 布告欄系統

專案簡介
員工心情留言、公司公告、公司社團活動資訊

使用技術
前端:
1. Thymeleaf
2. Javascript
3. HTML5
4. CSS3
5. jQuery
6. Bootstrap

後端:
1. JDK17
2. Springboot
3. JSP
4. JPA
5. Hibernate

資料庫:
1. SQL Server

其他工具 / 技術:
1. Fetch API
2. RESTful API
3. WebSocket

功能介紹
 1. 文章發布 / 編輯 / 刪除
 2. 文章留言回覆
 3. 文章按讚
 4. 文章分類瀏覽與關鍵字搜尋

環境需求
  1. Java: JDK17
  2. IDE: Spring Tool Suite4(或用其餘支援Maven的IDE替代)
  3. 資料庫: 執行於 Microsoft SQL Server 2022 (16.0.1000.6) RTM 版環境下，建議使用同等或更高版本執行

快速啟動與執行
  1. Clone專案(使用cmd)
    a. git clone https://github.com/phillip355045/SideProject_Maven_Springboot_iSpanEEIT183.git
  2. 匯入專案
    a. 開啟 Spring Tool Suite 4
    b. File → Import → Maven → Existing Maven Projects
    c. 選擇專案資料夾 → Finish
  3. 執行專案
    a. 找到找到 src/main/java/com.example.demo 下的 ProjectApplication.java 啟動類別
    b. 右鍵 → Run As → Spring Boot App
  4. 瀏覽系統
    a. 啟動網址:"http://localhost:8088/PSNEXUS/"
    b. 預設埠口與路徑可依 application.properties 進行調整：
       server.port=8088
       server.servlet.context-path=/PSNEXUS

資料庫設定
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=PSdb;trustServerCertificate=true
spring.datasource.username=(yourusername)
spring.datasource.password=(yourpassword)

資料表建置
專案啟動時，會自動根據 JPA Entity 建立對應資料表

應用情境
適用於中小型企業內部的公告佈達、活動資訊分享、員工互動留言，簡單好用、維運方便，能有效提升組織內部溝通與凝聚力

🖼️ 預覽畫面（可選）

1.貼文牆首頁、文章分類搜尋
<img width="959" alt="image" src="https://github.com/user-attachments/assets/9b5bc5de-39b8-46e8-9f0e-3e8317aedae3" />

2.文章按讚功能
<img width="934" alt="image" src="https://github.com/user-attachments/assets/2c90551a-8f2e-4b35-ac90-4db3ad920b41" />

3.文章留言功能
<img width="934" alt="image" src="https://github.com/user-attachments/assets/99713ac7-9acb-41b1-88ba-dfd1c28334e6" />
 
📄 授權 License
本專案採用 Apache License 2.0 授權，詳情請參閱 LICENSE 檔案。
