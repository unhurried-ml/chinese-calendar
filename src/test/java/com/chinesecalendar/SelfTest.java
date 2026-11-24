package com.chinesecalendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * chinese-calendar 自测类，无第三方依赖。
 *
 * <p>覆盖：节假日/工作日判断、调休判断、节日详情、区间查询、
 * 工作日查找、每年假期总量与调休数量对账、24 节气（2000、2004 - 2023 整年校验）。</p>
 *
 * <p>运行方式（任选其一）：</p>
 * <ul>
 *   <li>mvn test（exec 插件在 test 阶段调用本类的 main）</li>
 *   <li>java -cp target/classes;target/test-classes com.chinesecalendar.SelfTest</li>
 *   <li>IDE 中直接运行 main 方法</li>
 * </ul>
 * <p>全部通过时输出 "All tests passed"；失败抛 AssertionError 并终止。</p>
 */
public final class SelfTest {

    private int assertions = 0;

    public static void main(String[] args) {
        SelfTest test = new SelfTest();
        test.testOpposite();
        test.testHolidays();
        test.testWorkdays();
        test.testDetail();
        test.testYearNotSupported();
        test.testDocument();
        test.testHolidayInEnglish();
        test.testHolidayInChinese();
        test.testInLieuWorkdayInChinese();
        test.testNormalDayInChinese();
        test.testGetDates();
        test.testGetWorkdaysHolidays();
        test.testFindWorkday();
        test.testInLieuShouldBeHoliday();
        test.testInLieuShouldEqualWorkdaysAmount();
        test.testInLieuShouldBeWeekday();
        test.testHolidayAmount();
        test.testSolarTerms();
        System.out.println("All tests passed (" + test.assertions + " assertions).");
    }

    private void testOpposite() {
        LocalDate today = LocalDate.now();
        assertTrue(ChineseCalendar.isHoliday(today) == !ChineseCalendar.isWorkday(today), "isHoliday is opposite of isWorkday");
    }

    private void testHolidays() {
        LocalDate[] dates = {
                LocalDate.of(2017, 5, 30),
                LocalDate.of(2017, 6, 4),
                LocalDate.of(2017, 10, 1),
                LocalDate.of(2018, 5, 1),
        };
        for (LocalDate date : dates) {
            assertFalse(ChineseCalendar.isWorkday(date), "isWorkday " + date);
            assertTrue(ChineseCalendar.isHoliday(date), "isHoliday " + date);
        }
    }

    private void testWorkdays() {
        LocalDate[] dates = {
                LocalDate.of(2017, 5, 27),
                LocalDate.of(2017, 6, 5),
                LocalDate.of(2017, 10, 31),
                LocalDate.of(2018, 5, 10),
        };
        for (LocalDate date : dates) {
            assertFalse(ChineseCalendar.isHoliday(date), "isHoliday " + date);
            assertTrue(ChineseCalendar.isWorkday(date), "isWorkday " + date);
        }
    }

    private void testDetail() {
        Object[][] cases = {
                {LocalDate.of(2018, 2, 10), true, null},
                {LocalDate.of(2018, 2, 11), false, Holiday.SPRING_FESTIVAL},
                {LocalDate.of(2018, 2, 12), false, null},
                {LocalDate.of(2018, 2, 15), true, Holiday.SPRING_FESTIVAL},
        };
        for (Object[] c : cases) {
            LocalDate date = (LocalDate) c[0];
            HolidayDetail detail = ChineseCalendar.getHolidayDetail(date);
            assertEquals(c[1], detail.isOnHoliday(), "onHoliday " + date);
            assertEquals(c[2], detail.getHoliday(), "holiday " + date);
        }
    }

    private void testYearNotSupported() {
        // 年份超出数据范围时抛 IllegalArgumentException
        final LocalDate date = LocalDate.of(2001, 1, 1);
        assertThrows(new Runnable() {
            @Override
            public void run() {
                ChineseCalendar.isHoliday(date);
            }
        }, "isHoliday(2001-01-01)");
    }

    private void testDocument() {
        // README 示例
        LocalDate aprilLast = LocalDate.of(2018, 4, 30);
        assertFalse(ChineseCalendar.isWorkday(aprilLast), "isWorkday(2018-04-30)");
        assertTrue(ChineseCalendar.isHoliday(aprilLast), "isHoliday(2018-04-30)");

        HolidayDetail detail = ChineseCalendar.getHolidayDetail(aprilLast);
        assertTrue(detail.isOnHoliday(), "onHoliday(2018-04-30)");
        assertEquals(Holiday.LABOUR_DAY.getEnglish(), detail.getEnglishName(), "holiday name of 2018-04-30");

        assertFalse(ChineseCalendar.isInLieu(LocalDate.of(2006, 2, 1)), "isInLieu(2006-02-01)");
        assertTrue(ChineseCalendar.isInLieu(LocalDate.of(2006, 2, 2)), "isInLieu(2006-02-02)");
    }

    private void testHolidayInEnglish() {
        LocalDate nationalDay = LocalDate.of(2025, 10, 1);
        HolidayDetail detail = ChineseCalendar.getHolidayDetail(nationalDay);
        assertTrue(detail.isOnHoliday(), "onHoliday(2025-10-01)");
        assertEquals("National Day", detail.getEnglishName(), "english name of 2025-10-01");
        assertEquals(Holiday.NATIONAL_DAY.getEnglish(), detail.getEnglishName(), "english name of 2025-10-01");
    }

    private void testHolidayInChinese() {
        LocalDate nationalDay = LocalDate.of(2025, 10, 1);
        assertEquals("国庆节", ChineseCalendar.getHolidayDetail(nationalDay).getChineseName(), "chinese name of 2025-10-01");
    }

    private void testInLieuWorkdayInChinese() {
        // 2018-02-11 是周日，但是春节的补班日
        HolidayDetail detail = ChineseCalendar.getHolidayDetail(LocalDate.of(2018, 2, 11));
        assertFalse(detail.isOnHoliday(), "onHoliday(2018-02-11)");
        assertEquals("春节", detail.getChineseName(), "chinese name of 2018-02-11");
    }

    private void testNormalDayInChinese() {
        HolidayDetail weekend = ChineseCalendar.getHolidayDetail(LocalDate.of(2018, 2, 10));
        assertTrue(weekend.isOnHoliday(), "onHoliday(2018-02-10)");
        assertNull(weekend.getChineseName(), "chinese name of 2018-02-10");

        HolidayDetail workday = ChineseCalendar.getHolidayDetail(LocalDate.of(2018, 2, 12));
        assertFalse(workday.isOnHoliday(), "onHoliday(2018-02-12)");
        assertNull(workday.getChineseName(), "chinese name of 2018-02-12");
    }

    private void testGetDates() {
        // {sy, sm, sd, ey, em, ed, 期望数量}
        int[][] cases = {
                {2018, 2, 1, 2018, 1, 1, 0},
                {2018, 1, 1, 2018, 1, 1, 1},
                {2018, 1, 1, 2018, 1, 2, 2},
                {2018, 1, 1, 2018, 2, 1, 32},
                {2019, 1, 1, 2020, 1, 1, 366},
                {2020, 1, 1, 2021, 1, 1, 367},
        };
        for (int[] c : cases) {
            LocalDate start = LocalDate.of(c[0], c[1], c[2]);
            LocalDate end = LocalDate.of(c[3], c[4], c[5]);
            List<LocalDate> dates = ChineseCalendar.getDates(start, end);
            assertEquals(c[6], dates.size(), "getDates " + start + " ~ " + end);
            if (!dates.isEmpty()) {
                assertTrue(dates.contains(start), "getDates contains start " + start);
                assertTrue(dates.contains(end), "getDates contains end " + end);
            }
        }
    }

    private void testGetWorkdaysHolidays() {
        // {sy, sm, sd, ey, em, ed, 假期含周末, 假期不含周末, 工作日含周末补班, 工作日不含周末}
        int[][] cases = {
                {2018, 2, 1, 2018, 1, 1, 0, 0, 0, 0},
                {2018, 1, 1, 2018, 1, 1, 1, 1, 0, 0},
                {2018, 1, 1, 2018, 1, 7, 3, 1, 4, 4},
                {2018, 1, 1, 2018, 2, 1, 9, 1, 23, 23},
                {2018, 2, 1, 2018, 3, 1, 11, 7, 18, 16},
        };
        for (int[] c : cases) {
            LocalDate start = LocalDate.of(c[0], c[1], c[2]);
            LocalDate end = LocalDate.of(c[3], c[4], c[5]);
            String range = start + " ~ " + end;
            assertEquals(c[6], ChineseCalendar.getHolidays(start, end).size(), "holidays(include weekends) " + range);
            assertEquals(c[7], ChineseCalendar.getHolidays(start, end, false).size(), "holidays(exclude weekends) " + range);
            assertEquals(c[8], ChineseCalendar.getWorkdays(start, end).size(), "workdays(include weekends) " + range);
            assertEquals(c[9], ChineseCalendar.getWorkdays(start, end, false).size(), "workdays(exclude weekends) " + range);
        }
    }

    private void testFindWorkday() {
        LocalDate[] dates = {
                LocalDate.of(2017, 12, 30),
                LocalDate.of(2017, 12, 31),
                LocalDate.of(2018, 1, 1),
                LocalDate.of(2018, 1, 2),
        };
        List<List<LocalDate>> cases = new ArrayList<>();
        for (LocalDate date : dates) {
            List<LocalDate> row = new ArrayList<>();
            for (int i = -7; i <= 6; i++) {
                row.add(ChineseCalendar.findWorkday(i, date));
            }
            cases.add(row);
        }
        // 不同起点出发，向前/向后数同样的工作日数，结果应一致
        for (int i = 0; i < cases.size() - 1; i++) {
            assertEquals(cases.get(i), cases.get(i + 1), "findWorkday rows " + i + " and " + (i + 1));
        }
    }

    private void testInLieuShouldBeHoliday() {
        for (LocalDate date : HolidayConstants.IN_LIEU_DAYS.keySet()) {
            assertTrue(ChineseCalendar.isHoliday(date), "in lieu day " + date + " should be holiday");
        }
    }

    private void testInLieuShouldEqualWorkdaysAmount() {
        int minYear = Integer.MAX_VALUE;
        int maxYear = Integer.MIN_VALUE;
        for (LocalDate date : HolidayConstants.IN_LIEU_DAYS.keySet()) {
            minYear = Math.min(minYear, date.getYear());
        }
        for (LocalDate date : HolidayConstants.HOLIDAYS.keySet()) {
            maxYear = Math.max(maxYear, date.getYear());
        }
        Map<Integer, Integer> inLieuAmount = countByYear(HolidayConstants.IN_LIEU_DAYS);
        Map<Integer, Integer> workdaysAmount = countByYear(HolidayConstants.WORKDAYS);
        for (int year = minYear; year <= maxYear; year++) {
            int inLieu = inLieuAmount.containsKey(year) ? inLieuAmount.get(year) : 0;
            int workdays = workdaysAmount.containsKey(year) ? workdaysAmount.get(year) : 0;
            // 06年要补班07年的元旦假期；07年可以享受06年补班的假；11/12、18年同理
            if (year == 2006) {
                inLieu += 2;
            } else if (year == 2007) {
                workdays += 2;
            } else if (year == 2011) {
                inLieu += 1;
            } else if (year == 2012) {
                workdays += 1;
            } else if (year == 2018) {
                workdays += 1;
            }
            assertEquals(inLieu, workdays, "in lieu / workdays amount of year " + year);
        }
    }

    private void testInLieuShouldBeWeekday() {
        for (LocalDate date : HolidayConstants.IN_LIEU_DAYS.keySet()) {
            assertTrue(date.getDayOfWeek().getValue() <= 5, "in lieu day " + date + " should be Mon-Fri");
        }
    }

    private void testHolidayAmount() {
        Map<Integer, Integer> holidayAmounts = new HashMap<>();
        for (LocalDate date : HolidayConstants.HOLIDAYS.keySet()) {
            if (date.getDayOfWeek().getValue() <= 5) {
                // 周一~周五放假
                merge(holidayAmounts, date.getYear(), 1);
            }
        }
        for (LocalDate date : HolidayConstants.WORKDAYS.keySet()) {
            if (date.getDayOfWeek().getValue() > 5) {
                // 周六/周日补班
                merge(holidayAmounts, date.getYear(), -1);
            }
        }
        merge(holidayAmounts, 2007, -1); // 07年法定节假日有12天（国庆多了两天，元旦少了一天）
        merge(holidayAmounts, 2011, 1);  // 11年要补班12年的元旦假期
        merge(holidayAmounts, 2012, -1); // 12年可以享受11年补班的假
        merge(holidayAmounts, 2015, -1); // 15年是抗战胜利70周年，多放1天
        merge(holidayAmounts, 2020, -2); // 20年春节因为疫情防控，延长假期2天
        int maxSupportedYear = Integer.MIN_VALUE;
        for (LocalDate date : HolidayConstants.HOLIDAYS.keySet()) {
            maxSupportedYear = Math.max(maxSupportedYear, date.getYear());
        }
        for (int year = 2007; year <= maxSupportedYear; year++) {  // 06年数据少，不测了
            int expected = year <= 2024 ? 11 : 13;
            Integer actual = holidayAmounts.get(year);
            assertEquals(expected, actual == null ? 0 : actual, "Holiday amount of year " + year);
        }
    }

    private void testSolarTerms() {
        for (Map.Entry<Integer, List<SolarTermDate>> entry : SolarTermExpected.BY_YEAR.entrySet()) {
            int year = entry.getKey();
            List<SolarTermDate> expected = entry.getValue();
            List<SolarTermDate> actual =
                    ChineseCalendar.getSolarTerms(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
            assertEquals(expected, actual, "solar terms of " + year);
        }
        // 空区间
        List<SolarTermDate> empty =
                ChineseCalendar.getSolarTerms(LocalDate.of(2023, 1, 6), LocalDate.of(2023, 1, 19));
        assertTrue(empty.isEmpty(), "no solar terms between 2023-01-06 and 2023-01-19");
        // 年份范围校验
        final LocalDate start = LocalDate.of(1899, 1, 1);
        final LocalDate end = LocalDate.of(2101, 12, 31);
        assertThrows(new Runnable() {
            @Override
            public void run() {
                ChineseCalendar.getSolarTerms(start, LocalDate.of(1900, 12, 31));
            }
        }, "getSolarTerms(1899, 1900)");
        assertThrows(new Runnable() {
            @Override
            public void run() {
                ChineseCalendar.getSolarTerms(LocalDate.of(1900, 1, 1), end);
            }
        }, "getSolarTerms(1900, 2101)");
    }

    // ---------------- 断言工具 ----------------

    private void assertTrue(boolean condition, String message) {
        assertions++;
        if (!condition) {
            throw new AssertionError("expected true: " + message);
        }
    }

    private void assertFalse(boolean condition, String message) {
        assertions++;
        if (condition) {
            throw new AssertionError("expected false: " + message);
        }
    }

    private void assertNull(Object value, String message) {
        assertions++;
        if (value != null) {
            throw new AssertionError("expected null but was <" + value + ">: " + message);
        }
    }

    private void assertEquals(Object expected, Object actual, String message) {
        assertions++;
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private void assertThrows(Runnable runnable, String message) {
        assertions++;
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError(message + ": expected IllegalArgumentException");
    }

    private static Map<Integer, Integer> countByYear(Map<LocalDate, Holiday> dates) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (LocalDate date : dates.keySet()) {
            merge(counts, date.getYear(), 1);
        }
        return counts;
    }

    private static void merge(Map<Integer, Integer> counts, int year, int delta) {
        Integer current = counts.get(year);
        counts.put(year, (current == null ? 0 : current) + delta);
    }
}
