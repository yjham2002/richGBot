package util;

import java.util.Calendar;

/**
 * Created by a on 2017-04-18.
 */
public class TimeUnit {

    public static final String TIME_UNIT_HOUR = "HOUR";
    public static final String TIME_UNIT_MIN = "MIN";
    public static final String TIME_UNIT_SEC = "SEC";
    public static final String TIME_UNIT_YEAR = "YR";
    public static final String TIME_UNIT_MONTH = "MON";
    public static final String TIME_UNIT_WEEK = "WEEK";
    public static final String TIME_UNIT_DAY = "DAY";
    public static final String TIME_UNIT_WEEKDAY = "WEEKDAY";

    private String desc = "";
    private boolean standalone = false;
    private String meaning = "";

    private int diff = 0;

    public TimeUnit(){}

    public TimeUnit(String desc, String meaning, int diff, boolean standalone){
        this.desc = desc;
        this.meaning = meaning;
        this.diff = diff;
        this.standalone = standalone;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public boolean isStandalone() {
        return standalone;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    public static int toCalendarType(String meaning){
        switch(meaning){
            case TIME_UNIT_DAY : return Calendar.DAY_OF_MONTH;
            case TIME_UNIT_HOUR : return Calendar.HOUR_OF_DAY;
            case TIME_UNIT_MIN : return Calendar.MINUTE;
            case TIME_UNIT_MONTH : return Calendar.MONTH;
            case TIME_UNIT_SEC : return Calendar.SECOND;
            case TIME_UNIT_WEEK : return Calendar.WEEK_OF_YEAR;
            case TIME_UNIT_YEAR : return Calendar.YEAR;
            case TIME_UNIT_WEEKDAY : return Calendar.DAY_OF_WEEK;
            default: return -1;
        }
    }

}
