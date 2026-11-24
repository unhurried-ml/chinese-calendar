package com.chinesecalendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 中国节假日 / 工作日判断工具。
 *
 * <p>节假日与调休数据支持 2004 - 2027 年（与国务院发布的放假安排一致，
 * 含 2020 年春节延长）；24 节气支持 1900 - 2100 年。</p>
 *
 * <p>工具类，全部为静态方法，线程安全。零第三方依赖，仅使用 JDK 标准库。</p>
 */
public final class ChineseCalendar {

    /** 数据版本 */
    public static final String VERSION = "1.12.1";

    private static final int MIN_YEAR;
    private static final int MAX_YEAR;

    static {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (LocalDate date : HolidayConstants.HOLIDAYS.keySet()) {
            int year = date.getYear();
            if (year < min) {
                min = year;
            }
            if (year > max) {
                max = year;
            }
        }
        MIN_YEAR = min;
        MAX_YEAR = max;
    }

    private ChineseCalendar() {
    }

    /**
     * 判断某日期是否为休息日（法定节假日、调休或普通周末）。
     *
     * @param date 日期
     * @return 是休息日返回 true
     * @throws IllegalArgumentException 年份超出数据范围时抛出
     */
    public static boolean isHoliday(LocalDate date) {
        return !isWorkday(date);
    }

    /**
     * 判断某日期是否为工作日（含因调休而补班的周末）。
     *
     * @param date 日期
     * @return 是工作日返回 true
     * @throws IllegalArgumentException 年份超出数据范围时抛出
     */
    public static boolean isWorkday(LocalDate date) {
        LocalDate d = validateDate(date);
        // 周一=1 ... 周日=7，周一到周五为 1-5
        int weekday = d.getDayOfWeek().getValue();
        return HolidayConstants.WORKDAYS.containsKey(d)
                || (weekday <= 5 && !HolidayConstants.HOLIDAYS.containsKey(d));
    }

    /**
     * 判断某日期是否为调休休息日，即因法定节假日而休息的日子。
     *
     * @param date 日期
     * @return 是调休休息日返回 true
     * @throws IllegalArgumentException 年份超出数据范围时抛出
     */
    public static boolean isInLieu(LocalDate date) {
        LocalDate d = validateDate(date);
        return HolidayConstants.IN_LIEU_DAYS.containsKey(d);
    }

    /**
     * 判断某日期是否为休息日，并返回关联的节假日信息。
     *
     * <p>三种结果：法定节假日（休息）、周末补班日（上班，
     * 但返回其补偿的节假日）、普通日子（周末休息/工作日上班，节假日为 null）。</p>
     *
     * @param date 日期
     * @return 是否休息 + 关联的节假日
     * @throws IllegalArgumentException 年份超出数据范围时抛出
     */
    public static HolidayDetail getHolidayDetail(LocalDate date) {
        LocalDate d = validateDate(date);
        Holiday makeupWorkday = HolidayConstants.WORKDAYS.get(d);
        if (makeupWorkday != null) {
            return new HolidayDetail(false, makeupWorkday);
        }
        Holiday holiday = HolidayConstants.HOLIDAYS.get(d);
        if (holiday != null) {
            return new HolidayDetail(true, holiday);
        }
        return new HolidayDetail(d.getDayOfWeek().getValue() > 5, null);
    }

    /**
     * 获取 start 到 end 之间的所有日期（含两端）。
     * start 在 end 之后时返回空列表。
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 日期列表
     */
    public static List<LocalDate> getDates(LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            dates.add(d);
        }
        return dates;
    }

    /**
     * 获取 start 到 end 之间的休息日（含两端），包括普通周末。
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 休息日列表
     * @throws IllegalArgumentException 年份超出数据范围时抛出
     */
    public static List<LocalDate> getHolidays(LocalDate start, LocalDate end) {
        return getHolidays(start, end, true);
    }

    /**
     * 获取 start 到 end 之间的休息日（含两端）。
     *
     * @param start          开始日期
     * @param end            结束日期
     * @param includeWeekends false 时只返回法定节假日，不含普通周末
     * @return 休息日列表
     * @throws IllegalArgumentException 年份超出数据范围时抛出
     */
    public static List<LocalDate> getHolidays(LocalDate start, LocalDate end, boolean includeWeekends) {
        LocalDate s = validateDate(start);
        LocalDate e = validateDate(end);
        List<LocalDate> result = new ArrayList<>();
        for (LocalDate d : getDates(s, e)) {
            if (includeWeekends ? isHoliday(d) : HolidayConstants.HOLIDAYS.containsKey(d)) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * 获取 start 到 end 之间的工作日（含两端），包括因调休补班的周末。
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 工作日列表
     * @throws IllegalArgumentException 年份超出数据范围时抛出
     */
    public static List<LocalDate> getWorkdays(LocalDate start, LocalDate end) {
        return getWorkdays(start, end, true);
    }

    /**
     * 获取 start 到 end 之间的工作日（含两端）。
     *
     * @param start          开始日期
     * @param end            结束日期
     * @param includeWeekends false 时排除周六周日（即使当天是补班日）
     * @return 工作日列表
     * @throws IllegalArgumentException 年份超出数据范围时抛出
     */
    public static List<LocalDate> getWorkdays(LocalDate start, LocalDate end, boolean includeWeekends) {
        LocalDate s = validateDate(start);
        LocalDate e = validateDate(end);
        List<LocalDate> result = new ArrayList<>();
        for (LocalDate d : getDates(s, e)) {
            if (isWorkday(d) && (includeWeekends || d.getDayOfWeek().getValue() <= 5)) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * 以今天为起点，寻找偏移 deltaDays 个工作日之后的日期。
     *
     * @param deltaDays 0 表示今天（若是休息日则顺延到下一个工作日），1 表示下一个工作日，-1 表示上一个工作日
     * @return 目标工作日
     */
    public static LocalDate findWorkday(int deltaDays) {
        return findWorkday(deltaDays, null);
    }

    /**
     * 寻找偏移 deltaDays 个工作日之后的日期。
     *
     * @param deltaDays 0 表示当天（若是休息日则顺延到下一个工作日），1 表示下一个工作日，-1 表示上一个工作日
     * @param date      起点；null 表示今天
     * @return 目标工作日
     * @throws IllegalArgumentException 起点或途经日期超出数据范围时抛出
     */
    public static LocalDate findWorkday(int deltaDays, LocalDate date) {
        LocalDate d = date == null ? LocalDate.now() : date;
        int delta = deltaDays;
        if (delta >= 0) {
            delta += 1;
        }
        int sign = delta >= 0 ? 1 : -1;
        for (int i = 0; i < Math.abs(delta); i++) {
            if (delta < 0 || i > 0) {
                d = d.plusDays(sign);
            }
            while (!isWorkday(d)) {
                d = d.plusDays(sign);
            }
        }
        return d;
    }

    /**
     * 获取 start 到 end 之间的 24 节气（含两端），按日期升序返回。
     *
     * <p>采用通用寿星公式 [Y×D+C]-L 计算：Y 为年份后两位，D = 0.2422，
     * C 为节气系数（20/21 世纪各一组）；小寒、大寒、立春、雨水取 L = (Y-1)/4，
     * 且 2000 年这四个节气按 20 世纪 C 值计算；个别年份另有偏移修正。</p>
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 节气列表
     * @throws IllegalArgumentException 年份不在 [1900, 2100] 时抛出
     */
    public static List<SolarTermDate> getSolarTerms(LocalDate start, LocalDate end) {
        if (start.getYear() < 1900 || start.getYear() > 2100
                || end.getYear() < 1900 || end.getYear() > 2100) {
            throw new IllegalArgumentException("only year between [1900, 2100] supported");
        }
        final double d = 0.2422;
        List<SolarTermDate> result = new ArrayList<>();
        int year = start.getYear();
        int month = start.getMonthValue();
        while (year < end.getYear() || (year == end.getYear() && month <= end.getMonthValue())) {
            // 按月计算节气
            for (SolarTerm term : SolarTerm.termsOfMonth(month)) {
                // 2000 年的小寒、大寒、立春、雨水按照 20 世纪的 C 值来算
                double c = year < 2000 || (year == 2000 && SolarTerm.SPECIAL_L_TERMS.contains(term))
                        ? term.c20() : term.c21();
                int y = year % 100;
                int l = y / 4;
                if (SolarTerm.SPECIAL_L_TERMS.contains(term)) {
                    // 小寒、大寒、立春、雨水的 L=(Y-1)/4
                    l = (y - 1) / 4;
                }
                int day = (int) (y * d + c) - l;
                // 计算偏移量
                Integer delta = SolarTerm.deltaFor(year, term);
                if (delta != null) {
                    day += delta;
                }
                LocalDate date = LocalDate.of(year, month, day);
                if (date.isBefore(start) || date.isAfter(end)) {
                    continue;
                }
                result.add(new SolarTermDate(date, term));
            }
            if (month == 12) {
                year += 1;
                month = 1;
            } else {
                month += 1;
            }
        }
        return result;
    }

    /**
     * 校验日期非空且在数据支持的年份范围内。
     */
    private static LocalDate validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        if (date.getYear() < MIN_YEAR || date.getYear() > MAX_YEAR) {
            throw new IllegalArgumentException(
                    "no available data for year " + date.getYear()
                            + ", only year between [" + MIN_YEAR + ", " + MAX_YEAR + "] supported");
        }
        return date;
    }
}
