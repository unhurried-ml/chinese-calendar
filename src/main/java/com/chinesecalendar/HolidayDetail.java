package com.chinesecalendar;

/**
 * 是否休息，以及该日期关联的法定节假日（普通日子为 null）。
 *
 * <p>不可变值对象。普通周末/普通工作日的 {@link #getHoliday()} 为 null；
 * 周末补班日返回其补偿的节假日，且 {@link #isOnHoliday()} 为 false。</p>
 */
public final class HolidayDetail {

    private final boolean onHoliday;
    private final Holiday holiday;

    HolidayDetail(boolean onHoliday, Holiday holiday) {
        this.onHoliday = onHoliday;
        this.holiday = holiday;
    }

    /** 是否休息（法定节假日、调休或普通周末均为 true） */
    public boolean isOnHoliday() {
        return onHoliday;
    }

    /** 该日期关联的法定节假日；普通周末或普通工作日返回 null */
    public Holiday getHoliday() {
        return holiday;
    }

    /** 英文节日名；普通日子返回 null */
    public String getEnglishName() {
        return holiday == null ? null : holiday.getEnglish();
    }

    /** 中文节日名；普通日子返回 null */
    public String getChineseName() {
        return holiday == null ? null : holiday.getChinese();
    }

    @Override
    public String toString() {
        if (holiday == null) {
            return onHoliday ? "weekend" : "workday";
        }
        return (onHoliday ? "holiday: " : "workday in lieu of: ") + holiday;
    }
}
