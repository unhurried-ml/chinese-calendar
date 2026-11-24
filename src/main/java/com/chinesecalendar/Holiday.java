package com.chinesecalendar;

/**
 * 法定节假日类型。
 *
 * <p>每个节假日包含中英文名称与法定天数。</p>
 */
public enum Holiday {

    /** 元旦 */
    NEW_YEARS_DAY("New Year's Day", "元旦", 1),

    /** 春节 */
    SPRING_FESTIVAL("Spring Festival", "春节", 3),

    /** 清明 */
    TOMB_SWEEPING_DAY("Tomb-sweeping Day", "清明", 1),

    /** 劳动节 */
    LABOUR_DAY("Labour Day", "劳动节", 1),

    /** 端午 */
    DRAGON_BOAT_FESTIVAL("Dragon Boat Festival", "端午", 1),

    /** 国庆节 */
    NATIONAL_DAY("National Day", "国庆节", 3),

    /** 中秋 */
    MID_AUTUMN_FESTIVAL("Mid-autumn Festival", "中秋", 1),

    /** 中国人民抗日战争暨世界反法西斯战争胜利70周年纪念日（2015 年特别假日） */
    ANTI_FASCIST_70TH_DAY("Anti-Fascist 70th Day", "中国人民抗日战争暨世界反法西斯战争胜利70周年纪念日", 1);

    private final String english;
    private final String chinese;
    private final int days;

    Holiday(String english, String chinese, int days) {
        this.english = english;
        this.chinese = chinese;
        this.days = days;
    }

    /** 英文名 */
    public String getEnglish() {
        return english;
    }

    /** 中文名 */
    public String getChinese() {
        return chinese;
    }

    /** 法定天数 */
    public int getDays() {
        return days;
    }

    /**
     * 按英文名查找节假日。
     *
     * @param english 英文名
     * @return 对应的节假日
     * @throws IllegalArgumentException 找不到对应节假日时抛出
     */
    public static Holiday ofEnglish(String english) {
        for (Holiday holiday : values()) {
            if (holiday.english.equals(english)) {
                return holiday;
            }
        }
        throw new IllegalArgumentException("no holiday named " + english);
    }

    @Override
    public String toString() {
        return english;
    }
}
