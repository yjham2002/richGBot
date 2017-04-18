package util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by a on 2017-04-18.
 */
public class TimeExpression {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_RELATIVE = 100;
    public static final int TYPE_ABSOLUTE = 200;

    private String expression = "";

    private Integer start = -1;
    private Integer end = -1;
    private int unit = -1; // Calendar 클래스를 따름

    private boolean ellaborated = false;
    private Calendar date = Calendar.getInstance();
    private int type = TYPE_NONE;

    @Override
    public String toString() {
        return "TimeExpression{" +
                "start=" + start +
                ", end=" + end +
                ", ellaborated=" + ellaborated +
                ", date=" + date +
                ", type=" + type +
                '}';
    }

    public String getDateTime(){
        return date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH) + " " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND);
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public TimeExpression(){}

    public TimeExpression(Calendar date){
        this.date = date;
    }

    public TimeExpression(Calendar date, int type){
        this.date = date;
        this.type = type;
    }

    public void refresh(int constant, int value){
        date.set(constant, value);
    }

    public boolean isEllaborated() {
        return ellaborated;
    }

    public void setEllaborated(boolean ellaborated) {
        this.ellaborated = ellaborated;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
