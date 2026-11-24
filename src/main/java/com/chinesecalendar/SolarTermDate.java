package com.chinesecalendar;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 某个日期对应的节气。
 *
 * <p>不可变值对象，包含日期与节气，可获取节气的中文/英文名称。</p>
 */
public final class SolarTermDate {

    private final LocalDate date;
    private final SolarTerm term;

    SolarTermDate(LocalDate date, SolarTerm term) {
        this.date = Objects.requireNonNull(date, "date");
        this.term = Objects.requireNonNull(term, "term");
    }

    /** 日期 */
    public LocalDate getDate() {
        return date;
    }

    /** 节气 */
    public SolarTerm getTerm() {
        return term;
    }

    /** 节气中文名 */
    public String getChineseName() {
        return term.getChinese();
    }

    /** 节气英文名 */
    public String getEnglishName() {
        return term.getEnglish();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolarTermDate)) {
            return false;
        }
        SolarTermDate that = (SolarTermDate) o;
        return date.equals(that.date) && term == that.term;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, term);
    }

    @Override
    public String toString() {
        return date + " " + term.getChinese();
    }
}
