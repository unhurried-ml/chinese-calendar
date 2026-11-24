# chinese-calendar

[![中文](https://img.shields.io/badge/README-中文-red)](README.md)
[![English](https://img.shields.io/badge/README-English-blue)](README.en.md)

> 📅 Chinese statutory holidays / make-up workdays / workdays / 24 solar terms — one jar, JDK 1.8 ready

[![CI](https://github.com/unhurried-ml/chinese-calendar/actions/workflows/ci.yml/badge.svg)](https://github.com/unhurried-ml/chinese-calendar/actions/workflows/ci.yml)
[![JDK](https://img.shields.io/badge/JDK-1.8%2B-blue?logo=openjdk&logoColor=white)]()
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A36?logo=apache.maven&logoColor=white)]()
[![Tests](https://img.shields.io/badge/tests-462%20passed-brightgreen)]()
[![Data](https://img.shields.io/badge/data-2004--2027-orange)]()
[![Solar terms](https://img.shields.io/badge/solar%20terms-1900--2100-yellow)]()
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

> 🙏 **Acknowledgements**: this library is the Java port of the excellent Python project
> [chinesecalendar](https://github.com/LKI/chinese-calendar) (v1.12.1, MIT License).
> Both the holiday data and the algorithms are ported from that project — many thanks for
> years of maintenance and data updates.

## Table of Contents

- [Features](#-features)
- [Getting Started](#-getting-started)
- [API Reference](#-api-reference)
- [Build & Test](#-build--test)
- [Test Coverage](#-test-coverage)
- [Project Layout](#-project-layout)
- [Updating the Data](#-updating-the-data)
- [Contributing](#-contributing)
- [Changelog](#-changelog)
- [Acknowledgements](#-acknowledgements)
- [Code of Conduct](#-code-of-conduct)
- [License](#-license)

## ✨ Features

| Feature | Description |
| --- | --- |
| 🗓 Holiday check | Is a date a statutory holiday, a swapped rest day, or a plain weekend? |
| 💼 Workday check | Handles make-up workdays (weekend shifts due to holidays) |
| 🔁 In-lieu check | Tells whether a date is a rest day taken in lieu of a holiday |
| 🏷 Holiday detail | One call: on-holiday flag + Chinese/English name + statutory days |
| 📊 Range queries | Holidays / workdays in any date range (two weekend modes) |
| ➡️ Workday offset | `findWorkday` walks N workdays forward or backward |
| ☀️ 24 solar terms | 1900 - 2100, computed with the Shouxing formula + corrections |
| 🪶 Zero dependencies | Only `java.time` shipped with JDK 8 |
| 🧵 Thread safe | Static utility, read-only data after load |

## 🚀 Getting Started

### Requirements

- JDK 1.8+ (compiled for 1.8, runs on newer versions)
- Maven 3.6+ (to build)

### Installation

Clone this repository and install it into your local repository:

```bash
mvn install
```

Then add the dependency:

```xml
<dependency>
    <groupId>com.chinesecalendar</groupId>
    <artifactId>chinese-calendar</artifactId>
    <version>1.12.1</version>
</dependency>
```

### Quick Tour

```java
import java.time.LocalDate;

import com.chinesecalendar.ChineseCalendar;
import com.chinesecalendar.HolidayDetail;
import com.chinesecalendar.SolarTermDate;

// Is 2018-04-30 a workday? (Labour Day swapped rest day)
LocalDate date = LocalDate.of(2018, 4, 30);
ChineseCalendar.isWorkday(date);          // false
ChineseCalendar.isHoliday(date);          // true

// Get the holiday detail at the same time
HolidayDetail detail = ChineseCalendar.getHolidayDetail(date);
detail.isOnHoliday();                     // true
detail.getHoliday();                      // Holiday.LABOUR_DAY
detail.getChineseName();                  // "劳动节" (Labour Day)
detail.getEnglishName();                  // "Labour Day"
detail.getHoliday().getDays();            // 1 statutory day

// In-lieu check: 2006-02-02 is a swapped rest day for Spring Festival
ChineseCalendar.isInLieu(LocalDate.of(2006, 2, 2));   // true

// Range query: workdays of Feb 2018 (Spring Festival included)
ChineseCalendar.getWorkdays(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 3, 1)).size();          // 18 (incl. make-up Sundays)
ChineseCalendar.getWorkdays(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 3, 1), false).size();  // 16 (weekends excluded)

// Workday offsets
ChineseCalendar.findWorkday(1);                                         // next workday
ChineseCalendar.findWorkday(-1);                                        // previous workday
ChineseCalendar.findWorkday(0, LocalDate.of(2018, 4, 30));              // 2018-05-02 (skips the holiday)
ChineseCalendar.findWorkday(3, LocalDate.of(2025, 1, 27));              // 2025-02-07 (across Spring Festival)

// 24 solar terms of 2025
for (SolarTermDate st : ChineseCalendar.getSolarTerms(
        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))) {
    System.out.println(st.getDate() + " " + st.getChineseName());
}
```

## 📖 API Reference

| Method | Description |
| --- | --- |
| `isWorkday(LocalDate)` | Workday or not (make-up workdays included) |
| `isHoliday(LocalDate)` | Rest day or not (holidays / in-lieu / weekends) |
| `isInLieu(LocalDate)` | In-lieu rest day or not |
| `getHolidayDetail(LocalDate)` | `HolidayDetail`: on-holiday flag + holiday (names, statutory days) |
| `getDates(start, end)` | All dates in range (inclusive) |
| `getHolidays(start, end[, includeWeekends])` | Rest days in range; `includeWeekends=false` keeps statutory holidays only |
| `getWorkdays(start, end[, includeWeekends])` | Workdays in range; `includeWeekends=false` excludes make-up workdays |
| `findWorkday(deltaDays[, date])` | N workdays from a date: `0` today-or-next, `1` next, `-1` previous |
| `getSolarTerms(start, end)` | Solar terms in range as `List<SolarTermDate>` |
| `HolidayConstants.HOLIDAYS / WORKDAYS / IN_LIEU_DAYS` | Raw immutable data tables |

- Dates use `java.time.LocalDate`; years outside the data range throw `IllegalArgumentException`
- Solar terms use the general Shouxing formula `[Y×D+C]-L` (Lesser Cold, Greater Cold,
  Beginning of Spring and Rain Water use `L=(Y-1)/4` and 20th-century C values in 2000),
  with a correction table for specific years — see `SolarTerm` for details

## 🔨 Build & Test

```bash
mvn test
```

```text
All tests passed (462 assertions).
[INFO] BUILD SUCCESS
```

No Maven? Plain javac works too:

```bash
javac -encoding UTF-8 -d target/classes src/main/java/com/chinesecalendar/*.java
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/com/chinesecalendar/*.java
java -cp "target/classes;target/test-classes" com.chinesecalendar.SelfTest   # Windows
java -cp "target/classes:target/test-classes" com.chinesecalendar.SelfTest   # Linux / macOS
```

A [GitHub Actions workflow](.github/workflows/ci.yml) runs the full test suite on JDK 8 / 11 / 17 / 21.

## ✅ Test Coverage

`SelfTest` contains **462 assertions** with no third-party test framework:

| Category | Coverage |
| --- | --- |
| Checks | Workday / holiday / in-lieu / detail (holidays, make-up workdays, normal days, Chinese & English names) |
| Ranges | Date sequences, holiday / workday counts (both weekend modes, cross-month, cross-year, empty ranges) |
| Offsets | `findWorkday` forward / backward, holiday starting points, consistency across starting points |
| Reconciliation | Statutory holiday totals per year (2007 - 2027), in-lieu vs make-up counts per year (2004 - 2027) |
| Solar terms | Full-year checks for 2000 and 2004 - 2023 (21 years), empty ranges, out-of-range years |
| Boundaries | Out-of-range years throw, version constant |

## 📁 Project Layout

```
├── pom.xml
├── .github/                         CI, Issue / PR templates and community files
└── src
    ├── main/java/com/chinesecalendar
    │   ├── ChineseCalendar.java     core utility
    │   ├── Holiday.java             statutory holiday enum
    │   ├── HolidayConstants.java    holiday/make-up/in-lieu data tables (data entry point)
    │   ├── HolidayDetail.java       holiday detail result
    │   ├── SolarTerm.java           24 solar terms enum
    │   └── SolarTermDate.java       solar term result
    └── test/java/com/chinesecalendar
        ├── SelfTest.java            self tests (462 assertions)
        └── SolarTermExpected.java   expected solar term data for full-year checks
```

## 🔄 Updating the Data

China publishes next year's holiday schedule around November each year. To sync:

1. Add or adjust entries in the `HOLIDAYS` / `WORKDAYS` / `IN_LIEU_DAYS` tables of
   [`HolidayConstants.java`](src/main/java/com/chinesecalendar/HolidayConstants.java),
   following the existing format
2. Run `mvn test` — the per-year holiday-total and in-lieu/make-up reconciliation checks
   will point out exactly which year is off (update year boundaries and expectations in
   `SelfTest` if needed)
3. Bump the version and update [CHANGELOG.md](CHANGELOG.md) after checks pass

## 🤝 Contributing

Issues and PRs are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md).

## 📜 Changelog

See [CHANGELOG.md](CHANGELOG.md).

## 🙏 Acknowledgements

This library is the Java port of the Python project
[chinesecalendar](https://github.com/LKI/chinese-calendar)
([PyPI](https://pypi.org/project/chinesecalendar/)) and would not exist without it
and its author [Lirian Su](https://github.com/LKI):

- **Data**: the 2004 - 2027 holiday / make-up workday / in-lieu data (650 + 155 + 156 entries)
  comes entirely from that project. After the State Council publishes each year's schedule,
  the original author updates the data around November — years of that continued maintenance
  are the source of this whole ecosystem
- **Algorithms**: the solar-term formula coefficients and year correction table, along with
  the design of the workday / in-lieu / workday-offset logic, are ported from that project
- **License**: the upstream project is MIT-licensed; this port keeps MIT and preserves the
  original author's copyright notice (see [LICENSE](LICENSE))

If this Java port helps you, please also
[star the original project](https://github.com/LKI/chinese-calendar/stargazers).

## 📜 Code of Conduct

By participating in this project you agree to abide by the [Code of Conduct](.github/CODE_OF_CONDUCT.md).

## 📄 License

[MIT](LICENSE) © 2017 Lirian Su (original chinesecalendar author) and contributors of this project
