package com.chinesecalendar;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 二十四节气。
 *
 * <p>日期采用通用寿星公式 [Y×D+C]-L 计算（方括号内取整数）：Y 为年份后两位，
 * D = 0.2422，C 为节气系数（20 世纪与 21 世纪各一组）；L = Y/4，
 * 其中小寒、大寒、立春、雨水取 L = (Y-1)/4，且 2000 年这四个节气
 * 按 20 世纪的 C 值计算。个别年份公式计算有偏差，由偏移修正表纠偏。</p>
 *
 */
public enum SolarTerm {

    /** 立春 */
    THE_BEGINNING_OF_SPRING("the Beginning of Spring", "立春", 4.6295, 3.87),

    /** 雨水 */
    RAIN_WATER("Rain Water", "雨水", 19.4599, 18.73),

    /** 惊蛰 */
    THE_WAKING_OF_INSECTS("the Waking of Insects", "惊蛰", 6.3926, 5.63),

    /** 春分 */
    THE_SPRING_EQUINOX("the Spring Equinox", "春分", 21.4155, 20.646),

    /** 清明 */
    PURE_BRIGHTNESS("Pure Brightness", "清明", 5.59, 4.81),

    /** 谷雨 */
    GRAIN_RAIN("Grain Rain", "谷雨", 20.888, 20.1),

    /** 立夏 */
    THE_BEGINNING_OF_SUMMER("the Beginning of Summer", "立夏", 6.318, 5.52),

    /** 小满 */
    LESSER_FULLNESS_OF_GRAIN("Lesser Fullness of Grain", "小满", 21.86, 21.04),

    /** 芒种 */
    GRAIN_IN_BEARD("Grain in Beard", "芒种", 6.5, 5.678),

    /** 夏至 */
    THE_SUMMER_SOLSTICE("the Summer Solstice", "夏至", 22.2, 21.37),

    /** 小暑 */
    LESSER_HEAT("Lesser Heat", "小暑", 7.928, 7.108),

    /** 大暑 */
    GREATER_HEAT("Greater Heat", "大暑", 23.65, 22.83),

    /** 立秋 */
    THE_BEGINNING_OF_AUTUMN("the Beginning of Autumn", "立秋", 28.35, 7.5),

    /** 处暑 */
    THE_END_OF_HEAT("the End of Heat", "处暑", 23.95, 23.13),

    /** 白露 */
    WHITE_DEW("White Dew", "白露", 8.44, 7.646),

    /** 秋分 */
    THE_AUTUMN_EQUINOX("the Autumn Equinox", "秋分", 23.822, 23.042),

    /** 寒露 */
    COLD_DEW("Cold Dew", "寒露", 9.098, 8.318),

    /** 霜降 */
    FROSTS_DESCENT("Frost's Descent", "霜降", 24.218, 23.438),

    /** 立冬 */
    THE_BEGINNING_OF_WINTER("the Beginning of Winter", "立冬", 8.218, 7.438),

    /** 小雪 */
    LESSER_SNOW("Lesser Snow", "小雪", 23.08, 22.36),

    /** 大雪 */
    GREATER_SNOW("Greater Snow", "大雪", 7.9, 7.18),

    /** 冬至 */
    THE_WINTER_SOLSTICE("the Winter Solstice", "冬至", 22.6, 21.94),

    /** 小寒 */
    LESSER_COLD("Lesser Cold", "小寒", 6.11, 5.4055),

    /** 大寒 */
    GREATER_COLD("Greater Cold", "大寒", 20.84, 20.12);

    private final String english;
    private final String chinese;
    /** 20 世纪（1900 - 1999 年）C 值 */
    private final double c20;
    /** 21 世纪（2000 年及以后）C 值 */
    private final double c21;

    SolarTerm(String english, String chinese, double c20, double c21) {
        this.english = english;
        this.chinese = chinese;
        this.c20 = c20;
        this.c21 = c21;
    }

    /** 英文名 */
    public String getEnglish() {
        return english;
    }

    /** 中文名 */
    public String getChinese() {
        return chinese;
    }

    /** 20 世纪（1900 - 1999 年）C 值 */
    double c20() {
        return c20;
    }

    /** 21 世纪（2000 年及以后）C 值 */
    double c21() {
        return c21;
    }

    /**
     * 返回某个月的两个节气（如 1 月返回小寒与大寒）。
     *
     * @param month 月份（1 - 12）
     * @return 该月的节气数组
     */
    static SolarTerm[] termsOfMonth(int month) {
        return TERMS_BY_MONTH[month - 1];
    }

    /**
     * 使用特殊 L 值的四个节气：小寒、大寒、立春、雨水。
     * 这些节气 L 取 (Y-1)/4，且 2000 年的 C 值按 20 世纪取值。
     */
    static final Set<SolarTerm> SPECIAL_L_TERMS = Collections.unmodifiableSet(
            EnumSet.of(LESSER_COLD, GREATER_COLD, THE_BEGINNING_OF_SPRING, RAIN_WATER));

    /** 月份与节气对应关系（下标 0 表示 1 月） */
    private static final SolarTerm[][] TERMS_BY_MONTH = {
            {LESSER_COLD, GREATER_COLD},
            {THE_BEGINNING_OF_SPRING, RAIN_WATER},
            {THE_WAKING_OF_INSECTS, THE_SPRING_EQUINOX},
            {PURE_BRIGHTNESS, GRAIN_RAIN},
            {THE_BEGINNING_OF_SUMMER, LESSER_FULLNESS_OF_GRAIN},
            {GRAIN_IN_BEARD, THE_SUMMER_SOLSTICE},
            {LESSER_HEAT, GREATER_HEAT},
            {THE_BEGINNING_OF_AUTUMN, THE_END_OF_HEAT},
            {WHITE_DEW, THE_AUTUMN_EQUINOX},
            {COLD_DEW, FROSTS_DESCENT},
            {THE_BEGINNING_OF_WINTER, LESSER_SNOW},
            {GREATER_SNOW, THE_WINTER_SOLSTICE},
    };

    /** 个别年份公式计算不够准确时的偏移修正值（外层 key 为节气，内层 key 为年份） */
    private static final Map<SolarTerm, Map<Integer, Integer>> DELTA;

    static {
        Map<SolarTerm, Map<Integer, Integer>> delta = new EnumMap<SolarTerm, Map<Integer, Integer>>(SolarTerm.class);
        putDelta(delta, RAIN_WATER, 2026, -1);
        putDelta(delta, THE_SPRING_EQUINOX, 2084, 1);
        putDelta(delta, THE_BEGINNING_OF_SUMMER, 1911, 1);
        putDelta(delta, LESSER_FULLNESS_OF_GRAIN, 2008, 1);
        putDelta(delta, GRAIN_IN_BEARD, 1902, 1);
        putDelta(delta, THE_SUMMER_SOLSTICE, 1928, 1);
        putDelta(delta, LESSER_HEAT, 1925, 1);
        putDelta(delta, LESSER_HEAT, 2016, 1);
        putDelta(delta, GREATER_HEAT, 1922, 1);
        putDelta(delta, THE_BEGINNING_OF_AUTUMN, 2002, 1);
        putDelta(delta, WHITE_DEW, 1927, 1);
        putDelta(delta, THE_AUTUMN_EQUINOX, 1942, 1);
        putDelta(delta, FROSTS_DESCENT, 2089, 1);
        putDelta(delta, THE_BEGINNING_OF_WINTER, 2089, 1);
        putDelta(delta, LESSER_SNOW, 1978, 1);
        putDelta(delta, GREATER_SNOW, 1954, 1);
        putDelta(delta, THE_WINTER_SOLSTICE, 1918, -1);
        putDelta(delta, THE_WINTER_SOLSTICE, 2021, -1);
        putDelta(delta, LESSER_COLD, 1982, 1);
        putDelta(delta, LESSER_COLD, 2019, -1);
        putDelta(delta, GREATER_COLD, 2000, 1);
        putDelta(delta, GREATER_COLD, 2082, 1);
        DELTA = Collections.unmodifiableMap(delta);
    }

    private static void putDelta(Map<SolarTerm, Map<Integer, Integer>> delta, SolarTerm term, int year, int value) {
        delta.computeIfAbsent(term, k -> new HashMap<Integer, Integer>()).put(year, value);
    }

    /**
     * 返回指定年份、节气的偏移修正值。
     *
     * @param year 年份
     * @param term 节气
     * @return 偏移值；无修正时返回 null
     */
    static Integer deltaFor(int year, SolarTerm term) {
        Map<Integer, Integer> byYear = DELTA.get(term);
        return byYear == null ? null : byYear.get(year);
    }

    /**
     * 按中文名查找节气。
     *
     * @param chinese 中文名
     * @return 对应的节气
     * @throws IllegalArgumentException 找不到对应节气时抛出
     */
    public static SolarTerm ofChinese(String chinese) {
        for (SolarTerm term : values()) {
            if (term.chinese.equals(chinese)) {
                return term;
            }
        }
        throw new IllegalArgumentException("no solar term named " + chinese);
    }

    @Override
    public String toString() {
        return chinese;
    }
}
