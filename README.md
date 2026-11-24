# chinese-calendar

[![中文](https://img.shields.io/badge/README-中文-red)](README.md)
[![English](https://img.shields.io/badge/README-English-blue)](README.en.md)

> 📅 中国法定节假日 / 调休补班 / 工作日 / 二十四节气 工具库 —— 一个 jar 搞定，JDK 1.8 即可用

[![CI](https://github.com/unhurried-ml/chinese-calendar/actions/workflows/ci.yml/badge.svg)](https://github.com/unhurried-ml/chinese-calendar/actions/workflows/ci.yml)
[![JDK](https://img.shields.io/badge/JDK-1.8%2B-blue?logo=openjdk&logoColor=white)]()
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A36?logo=apache.maven&logoColor=white)]()
[![Tests](https://img.shields.io/badge/tests-462%20passed-brightgreen)]()
[![Data](https://img.shields.io/badge/data-2004--2027-orange)]()
[![Solar terms](https://img.shields.io/badge/solar%20terms-1900--2100-yellow)]()
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

> 🙏 **致敬**：本库是 Python 生态中优秀的
> [chinesecalendar](https://github.com/LKI/chinese-calendar)（v1.12.1，MIT License）的
> Java 版本，节假日数据与算法逻辑均移植自该项目，感谢原作者多年来的持续维护与数据更新。

## 目录

- [特性](#-特性)
- [快速开始](#-快速开始)
- [API 文档](#-api-文档)
- [构建与测试](#-构建与测试)
- [测试覆盖](#-测试覆盖)
- [目录结构](#-目录结构)
- [数据更新](#-数据更新)
- [参与贡献](#-参与贡献)
- [更新日志](#-更新日志)
- [致谢](#-致谢)
- [行为准则](#-行为准则)
- [License](#-license)

## ✨ 特性

| 能力 | 说明 |
| --- | --- |
| 🗓 节假日判断 | 某天是不是法定节假日 / 调休 / 普通周末 |
| 💼 工作日判断 | 自动处理周末补班（调休上班的周六周日） |
| 🔁 调休识别 | 判断某天是否为 in lieu 代休日 |
| 🏷 节日详情 | 一次调用同时拿到「是否休息 + 中英文名 + 法定天数」 |
| 📊 区间查询 | 任意日期区间内的休息日 / 工作日列表（两种周末口径） |
| ➡️ 工作日推算 | `findWorkday` 支持向前 / 向后偏移 N 个工作日 |
| ☀️ 二十四节气 | 1900 - 2100 年任意区间内的节气（寿星公式 + 修正表） |
| 🪶 零依赖 | 仅使用 JDK 8 自带的 `java.time`，无任何第三方依赖 |
| 🧵 线程安全 | 纯静态工具方法，数据加载后只读 |

## 🚀 快速开始

### 环境要求

- JDK 1.8+（编译目标 1.8，更高版本可运行）
- Maven 3.6+（构建时）

### 安装

clone 本仓库后安装到本地仓库：

```bash
mvn install
```

然后在 `pom.xml` 中引入：

```xml
<dependency>
    <groupId>com.chinesecalendar</groupId>
    <artifactId>chinese-calendar</artifactId>
    <version>1.12.1</version>
</dependency>
```

### 5 分钟上手

```java
import java.time.LocalDate;

import com.chinesecalendar.ChineseCalendar;
import com.chinesecalendar.HolidayDetail;
import com.chinesecalendar.SolarTermDate;

// 判断 2018-04-30 是不是节假日（劳动节调休）
LocalDate date = LocalDate.of(2018, 4, 30);
ChineseCalendar.isWorkday(date);          // false
ChineseCalendar.isHoliday(date);          // true

// 判断的同时获取节日详情
HolidayDetail detail = ChineseCalendar.getHolidayDetail(date);
detail.isOnHoliday();                     // true
detail.getHoliday();                      // Holiday.LABOUR_DAY
detail.getChineseName();                  // "劳动节"
detail.getEnglishName();                  // "Labour Day"
detail.getHoliday().getDays();            // 1（法定天数）

// 判断调休：2006-02-02 是春节的代休日
ChineseCalendar.isInLieu(LocalDate.of(2006, 2, 2));   // true

// 区间查询：2018-02-01 ~ 2018-03-01（含春节）的工作日
ChineseCalendar.getWorkdays(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 3, 1)).size();          // 18（含补班周日）
ChineseCalendar.getWorkdays(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 3, 1), false).size();  // 16（不含周末）

// 工作日推算
ChineseCalendar.findWorkday(1);                                         // 下一个工作日
ChineseCalendar.findWorkday(-1);                                        // 上一个工作日
ChineseCalendar.findWorkday(0, LocalDate.of(2018, 4, 30));              // 2018-05-02（假期顺延）
ChineseCalendar.findWorkday(3, LocalDate.of(2025, 1, 27));              // 2025-02-07（跨春节假期）

// 二十四节气：2025 年全年
for (SolarTermDate st : ChineseCalendar.getSolarTerms(
        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))) {
    System.out.println(st.getDate() + " " + st.getChineseName());
}
// 2025-01-05 小寒
// 2025-01-20 大寒
// 2025-02-03 立春
// ...
```

## 📖 API 文档

| 方法 | 说明 |
| --- | --- |
| `isWorkday(LocalDate)` | 是否工作日（含周末补班日） |
| `isHoliday(LocalDate)` | 是否休息日（节假日 / 调休 / 周末） |
| `isInLieu(LocalDate)` | 是否调休代休日 |
| `getHolidayDetail(LocalDate)` | 返回 `HolidayDetail`：是否休息 + 节假日（中英文名、法定天数） |
| `getDates(start, end)` | 区间内全部日期（含两端） |
| `getHolidays(start, end[, includeWeekends])` | 区间内休息日；`includeWeekends=false` 只返回法定节假日 |
| `getWorkdays(start, end[, includeWeekends])` | 区间内工作日；`includeWeekends=false` 排除周末补班日 |
| `findWorkday(deltaDays[, date])` | 偏移 N 个工作日：`0` 当天或顺延，`1` 下一个，`-1` 上一个 |
| `getSolarTerms(start, end)` | 区间内 24 节气，`List<SolarTermDate>`（日期 + 中英文名） |
| `HolidayConstants.HOLIDAYS / WORKDAYS / IN_LIEU_DAYS` | 底层数据表（不可变） |

- 日期统一使用 `java.time.LocalDate`；年份超出数据范围时抛出 `IllegalArgumentException`
- 24 节气采用通用寿星公式 `[Y×D+C]-L` 计算（小寒/大寒/立春/雨水取 `L=(Y-1)/4`，
  2000 年这四个节气按 20 世纪 C 值），个别年份由修正表纠偏，详见 `SolarTerm`

## 🔨 构建与测试

```bash
mvn test
```

```text
All tests passed (462 assertions).
[INFO] BUILD SUCCESS
```

不用 Maven 也能跑：

```bash
javac -encoding UTF-8 -d target/classes src/main/java/com/chinesecalendar/*.java
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/com/chinesecalendar/*.java
java -cp "target/classes;target/test-classes" com.chinesecalendar.SelfTest   # Windows
java -cp "target/classes:target/test-classes" com.chinesecalendar.SelfTest   # Linux / macOS
```

仓库内置 [GitHub Actions CI](.github/workflows/ci.yml)，在 JDK 8 / 11 / 17 / 21 四个版本上运行完整测试。

## ✅ 测试覆盖

自测类 `SelfTest` 共 **462 个断言**，无 JUnit 等第三方依赖：

| 类别 | 覆盖内容 |
| --- | --- |
| 判断类 | 工作日 / 休息日 / 调休 / 节日详情（法定假、补班日、普通日、中英文名） |
| 区间类 | 日期序列、休息日 / 工作日数量（含与不含周末两种口径、跨月、跨年、空区间） |
| 推算类 | `findWorkday` 前后偏移、以假期为起点、不同起点一致性 |
| 对账类 | 每年假期总量对账（2007 - 2027）、调休与补班数量对账（2004 - 2027） |
| 节气类 | 2000、2004 - 2023 共 21 年整年 24 节气逐项校验、空区间、年份越界 |
| 边界类 | 数据范围外年份抛异常、版本号 |

## 📁 目录结构

```
├── pom.xml
├── .github/                         CI、Issue / PR 模板与社区文件
└── src
    ├── main/java/com/chinesecalendar
    │   ├── ChineseCalendar.java     核心工具类
    │   ├── Holiday.java             法定节假日枚举
    │   ├── HolidayConstants.java    节假日/补班/代休数据表（数据维护入口）
    │   ├── HolidayDetail.java       节日详情结果
    │   ├── SolarTerm.java           二十四节气枚举
    │   └── SolarTermDate.java       节气结果
    └── test/java/com/chinesecalendar
        ├── SelfTest.java            自测（462 断言）
        └── SolarTermExpected.java   节气期望校验数据
```

## 🔄 数据更新

次年节假日安排一般在每年 11 月前后由国务院发布，同步步骤：

1. 在 [`HolidayConstants.java`](src/main/java/com/chinesecalendar/HolidayConstants.java) 的
   `HOLIDAYS` / `WORKDAYS` / `IN_LIEU_DAYS` 三个表中按现有格式追加、调整对应日期条目
2. `mvn test` 完成每年假期总量与调休补班数量的对账校验
   （自测会精确指出哪一年的账目不平；必要时同步 `SelfTest` 中的年份边界与期望值）
3. 校验通过后更新 [CHANGELOG.md](CHANGELOG.md) 并递增版本号

## 🤝 参与贡献

欢迎 PR 与 Issue！见 [CONTRIBUTING.md](CONTRIBUTING.md)。

## 📜 更新日志

见 [CHANGELOG.md](CHANGELOG.md)。

## 🙏 致谢

本库是 Python 生态中前辈项目
[chinesecalendar](https://github.com/LKI/chinese-calendar)
（[PyPI](https://pypi.org/project/chinesecalendar/)）的 Java 版本，
能够存在完全要感谢该项目及其作者 [Lirian Su](https://github.com/LKI)：

- **数据**：2004 - 2027 年的节假日 / 补班 / 代休数据（650 + 155 + 156 条）全部来自该项目。
  国务院每年发布次年放假安排后，原作者都会在 11 月前后跟进更新，这份持续多年的维护是整个生态的源头
- **算法**：二十四节气的寿星公式系数与年份修正表、工作日判断 / 调休识别 / 工作日推算的算法设计，
  均移植自该项目
- **协议**：原项目以 MIT 协议开源，本项目沿用 MIT 并保留原作者的版权声明（见 [LICENSE](LICENSE)）

如果这个 Java 版对你有帮助，请务必也去
[给原项目点个 ⭐](https://github.com/LKI/chinese-calendar/stargazers)。

## 📜 行为准则

参与本项目即代表你同意遵守 [行为准则](.github/CODE_OF_CONDUCT.md)。

## 📄 License

[MIT](LICENSE) © 2017 Lirian Su（chinesecalendar 原作者）及本项目贡献者
