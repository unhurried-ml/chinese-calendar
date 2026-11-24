# 参与贡献

感谢关注 chinese-calendar！欢迎提交 Issue 与 Pull Request。

## 环境准备

- JDK 1.8+（本项目编译目标为 1.8，请勿使用更高版本的语法特性）
- Maven 3.6+

## 开发流程

1. Fork 并 clone 本仓库
2. 从 `main` 切出特性分支：`git checkout -b feat/xxx` 或 `fix/xxx`
3. 修改代码 / 数据 / 文档
4. 本地自测通过：`mvn test`（应输出 `All tests passed (462 assertions).`）
5. 提交 PR，关联相关 Issue，按模板填写说明

## 更新节假日数据

国务院一般在每年 11 月前后发布次年安排，按以下流程同步：

1. 在 [`HolidayConstants.java`](src/main/java/com/chinesecalendar/HolidayConstants.java) 的
   `HOLIDAYS` / `WORKDAYS` / `IN_LIEU_DAYS` 三个表中按现有格式追加或调整条目
2. `mvn test` 全部通过（对账断言会指出账目不平的年份；
   新增年份时同步调整 `SelfTest` 中的年份边界与期望值）
3. 更新 [CHANGELOG.md](CHANGELOG.md) 并递增版本号
4. 提交 PR，标题注明数据年份，例如 `data: 2028 年放假安排`

## 代码约定

- 语法目标 JDK 1.8（可用 `java.util.function`、`java.util.stream`、`Map.computeIfAbsent` 等，不可用 `var`、`record` 等）
- 注释统一 Javadoc 风格，描述类 / 方法自身职责
- 节假日数据集中维护在 `HolidayConstants.java`，追加条目时保持现有格式（一行一条 `put`，按年份分组）
- 保持零第三方依赖（含测试，测试通过 `SelfTest` 断言实现）

## 提交信息

- 建议遵循 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/)：
  `feat: ...`、`fix: ...`、`docs: ...`、`data: ...`、`chore: ...`

## 行为准则

参与本项目即代表你同意遵守[行为准则](.github/CODE_OF_CONDUCT.md)。发现安全漏洞请按
[安全策略](.github/SECURITY.md) 私下报告，请勿在公开 Issue 中披露。
