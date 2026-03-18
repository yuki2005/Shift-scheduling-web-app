# Restaurant Shift Scheduling Web Application

Java / Spring Boot / PostgreSQL / REST API / JPA / Web Application

---

## 1. 概要（Overview）

本アプリケーションは、私が学生時代にアルバイトをしていた飲食店をモデルとして開発した  
**シフト自動生成Webアプリケーション**です。

シフト作成における人的負担や作業時間の削減を目的とし、  
従業員の **スキル・希望シフト・日ごとの必要人数** を考慮した  
自動シフト生成ロジックを実装しています。

生成結果はデータベースに保存され、

- 履歴の参照
- 内容の確認
- 時間帯・ポジション単位での手動修正

を行うことができます。

シフト作成者が最終確認と微調整に集中できるよう、
シフト作成業務の効率化を目的として設計しました。

---

## 2. 動作紹介動画（Demo）

アプリケーションの基本的な操作は  
以下の動画で確認できます。

▶ **デモ動画（約1分40秒）**  
https://youtu.be/LU-z3zEHb18

動画では以下の流れを紹介しています。

1. 従業員データの読み込み  
2. 希望シフトの入力  
3. シフト自動生成  
4. 生成結果の表示  
5. 手動修正  
6. シフト履歴の保存  

---

## 3. 主な機能（Features）

- 従業員情報の管理
- 希望シフトの入力・管理
- シフトの自動生成
- 生成結果の保存（履歴管理）
- 保存済みシフトの一覧表示
- 日付による検索
- シフトの手動修正（時間帯 × ポジション）
- 重複割り当ての検出・警告表示

---

## 4. スクリーンショット（Screenshots）

### メインメニュー

![main](docs/main.png)

### シフト自動生成画面

![shift](docs/shift.png)

### シフト履歴画面

![history](docs/history.png)

※ UI は現在改善途中です。

---

## 5. 使用技術（Tech Stack）

### バックエンド
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Jackson

### フロントエンド
- HTML
- JavaScript（Vanilla JS）

### データベース
- PostgreSQL（本番想定）
- H2（開発用）

---

## 6. システム構成・アーキテクチャ

本システムは以下の構成で実装しています。

```
Browser (HTML / JavaScript)
        ↓
Spring Boot REST API
        ↓
Service Layer
        ↓
Repository Layer (JPA)
        ↓
PostgreSQL Database
```

Controller / Service / Repository の  
**レイヤードアーキテクチャ**を採用しています。

---

## 7. ディレクトリ構成

本プロジェクトは Maven / Spring Boot の標準構成に従っています。

```
src
 └ main
    ├ java
    │   └ position
    │       ├ controller
    │       ├ dto
    │       ├ entity
    │       ├ factory
    │       ├ mapper
    │       ├ model
    │       ├ repository
    │       ├ service
    │       ├ strategy
    │       ├ util
    │       └ PositionApplication.java
    │
    └ resources
        ├ static
        │   ├ css
        │   ├ js
        │   └ html
        └ application.properties
```

スタッフ選定ロジックは  
**Strategy パターン**として service 層に実装しています。

---

## 8. データベース設計（ER）

本システムで使用している主要エンティティは以下の通りです。

- Employee
- ShiftPreferenceHeader
- ShiftPreferenceDetail
- FinalShiftRecord
- FinalShiftRecordAssignment

### ER図

主要エンティティ間の関係を以下に示します。

![er](docs/er.png)

### 設計上の工夫

希望シフトは **正規化** し、更新・整合性を重視しています。

一方で確定シフトは、

- JSON保存（履歴再現・表示用）
- 明細テーブル（検索・編集用）

の **ハイブリッド構成**を採用し、  
用途に応じて最適なデータ構造を使い分けています。

---

## 9. クラス設計

- ドメインモデル中心設計
- Strategyパターンによるスタッフ選定ロジック分離
- AutoShift / PosAssign による役割分担

将来的なアルゴリズム変更に対応できる設計にしています。

---

## 10. シフト自動生成アルゴリズム

考慮する要素

- 従業員の希望
- スキル
- 時間帯ごとの必要人数
- ポジションの重要度

実際の現場では

- 体調
- 人間関係
- 突発的事情

など数値化できない要素が存在します。

そのため本アプリでは  
**厳密な最適化問題として定式化することは行わず**

制約を満たすことを優先し

**ポジション重要度順の貪欲法（ヒューリスティック）**

を採用しています。

---

## 11. 実装上の工夫

### Entity / DTO 分離

- Entity を直接 API レスポンスに使用しない
- 循環参照による JSON 無限生成の防止
- フロントに不要なデータを送らない

### 確定シフト保存設計

確定シフトは

```
finalAssignmentJson
```

に JSON 形式で保存。

同時に

```
FinalShiftRecordAssignment
```

テーブルに分解して保存することで

- 編集
- 検索

を可能にしています。

---

## 12. 発生した主なエラーと解決

### JSONパースエラー

原因  
JSON構造不整合

対応  
フロント / バックエンドの形式統一

### Hibernate TransientPropertyValueException

原因  
親Entity未保存状態で子Entity保存

対応  

```
Record → Assignment
```

の順で保存

---

## 13. ビルド構成問題の解決

開発途中で Maven 構成変更により  
IDE上で大量エラーが発生しました。

原因

```xml
<sourceDirectory>src</sourceDirectory>
```

設定により  
Maven が `src` をソースフォルダと誤認識していました。

対応

- 設定削除
- `src/main/java` に統一
- Maven Update

---

## 14. AI（ChatGPT）の活用

### 活用内容

- エラーログ解析
- 設計レビュー
- JSONデータ解析
- 修正方針検討

### 方針

- 丸投げしない
- ログを渡して原因分析
- 提案を比較検討
- 理解したもののみ実装

---

## 15. 起動方法（Getting Started）

### 必要環境

- Java 21
- Maven
- PostgreSQL

### 1. リポジトリ取得

```
git clone https://github.com/yourname/position.git
cd position
```

### 2. 設定ファイル作成

```
src/main/resources/application.properties
```

を作成

```
spring.datasource.url=jdbc:postgresql://localhost:5432/shiftdb
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

※ `application.properties.example` を参考

### 3. アプリ起動

```
mvn spring-boot:run
```

または

```
java -jar target/position.jar
```

### 4. アクセス

```
http://localhost:8080
```

---

## 16. 今後の改善予定

- UI / UX 改善
- シフト生成アルゴリズム高度化
- AWSデプロイ
- 複数店舗対応
- 権限管理

---

## 17. 学び

- 設計の重要性
- エラー解析力
- AIとの協働開発

単なる動作アプリではなく  
**保守性・拡張性を考慮した設計経験**を得ました。

---

## 18. Author

明治大学 理工学部 情報科学科  山下裕貴
制作期間：2025年9月〜2026年3月
