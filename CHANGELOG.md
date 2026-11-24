# Changelog

本项目所有重要变更均记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。
本库是 [chinesecalendar](https://github.com/LKI/chinese-calendar) 的 Java 移植版本，
版本号与上游保持对应。

## [1.12.1] - 2026-11-23

首个 Java 移植版本（对应上游 v1.12.1）。

### Added

- 核心判断：`isWorkday` / `isHoliday` / `isInLieu`
- 节日详情：`getHolidayDetail`（是否休息 + 中英文名 + 法定天数）
- 区间查询：`getDates` / `getHolidays` / `getWorkdays`（含与不含周末两种口径）
- 工作日推算：`findWorkday`（`0` 当天或顺延，正数向后，负数向前）
- 二十四节气：`getSolarTerms`（1900 - 2100，通用寿星公式 + 年份修正表）
- 节假日数据：2004 - 2027 年（节假日 650 条、补班 155 条、代休 156 条，
  含 2020 年春节延长与 2015 年抗战胜利 70 周年特别假日）
- 自测：462 个断言，无第三方测试框架依赖
- CI：GitHub Actions，JDK 8 / 11 / 17 / 21 矩阵
